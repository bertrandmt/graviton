/* BSD 2-Clause License
 * 
 * Copyright (c) 2017, Bertrand Mollinier Toublet
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * 
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
'use strict';

const G = 6.67259e-11;
const ROCHE_LIMIT = 500000000;  /* 500,000 km */
const do_verbose = 1;
const canvas_width = 1640;
const canvas_height = 640;

var space;
var ratio = 1e-10;
var zoom = 1;
var iterations_per_step = 200;
var step_dt = 1000;
var do_stop = true;
var iteration = 0;
var threshold = 5;

var fmt = Intl.NumberFormat('en-US', { maximumFractionDigits: 3 });
var pct = Intl.NumberFormat('en-US', { style: 'percent', maximumFractionDigits: 4 });


main();

function Vector(x, y) {
    this.x = x;
    this.y = y;

    this.magnitude = function() {
        return Math.sqrt(this.x*this.x + this.y*this.y);
    }

    this.magnitude_sq = function() {
        return this.x*this.x + this.y*this.y;
    }

    this.set_magnitude = function(magnitude) {
        return this.mul(magnitude / this.magnitude());
    }

    this.set_angle = function(theta) {
        this.x = Math.cos(theta);
        this.y = Math.sin(theta);
        return this;
    }

    this.orthogonal_left = function() {
        return new Vector(this.y, -this.x);
    }

    this.sub = function(other) {
        return new Vector(this.x - other.x, this.y - other.y);
    }

    this.mul = function(scalar) {
        return new Vector(this.x * scalar, this.y * scalar);
    }

    this.add = function(other) {
        this.x += other.x;
        this.y += other.y;
        return this;
    }

    this.toString = function() {
        return "{ x: "+fmt.format(this.x)+", y: "+fmt.format(this.y)+" }";
    }
}

function MassModel(mass, pos, v) {

    this.mass = mass;
    this.pos = pos;
    this.v = v;
    this.a = new Vector(0, 0);

    this.accelerate = function(particles) {
        var a = new Vector(0, 0);
        var self = this;

        particles.forEach(function(other) {
            if (other === self) return;

            var d = other.pos.sub(self.pos);
            var r2 = d.magnitude_sq();

            var a_magnitude = G * other.mass / r2;
            var local_a = d.set_magnitude(a_magnitude);

            a.add(local_a);
        });

        this.a = a;
    };

    this.absorb = function(other) {
        var mass_ratio = other.mass / (other.mass + this.mass);
        this.v.add(other.v.sub(this.v).mul(mass_ratio));
        this.pos.add(other.pos.sub(this.pos).mul(mass_ratio));
        this.mass += other.mass;
    }

    this.step = function(dt) {
        this.v.add(this.a.mul(dt));

        this.pos.add(this.v.mul(dt));
    };

    this.toString = function() {
        return "{ m: "+this.mass +
               ", pos: "+this.pos.toString() +
               ", v: "+this.v.toString() +
               ", a: "+this.a.toString() +
               " }";
    };
}

function Mass(name, model, color) {
    this.name = name;
    this.model = model;
    this.color = color;
    this.radius = 2; //Math.log10(this.model.radius); //this.model.radius * this.ratio;

    this.render = function(pov, do_bright) {
        /* relative position */
        var d = this.model.pos.sub(pov.model.pos);
        //var d = this.model.pos;
        /* scaled */
        d = d.mul(ratio * zoom);

        if (do_bright) { 
            this.ctx.fillStyle = this.color;
        } else {
            this.ctx.filter = "opacity(50%)";
            this.ctx.fillStyle = 'black';
            //this.ctx.fillStyle = this.color;
        }
        this.ctx.beginPath();
        this.ctx.arc(d.x, d.y, this.radius, 0, 2 * Math.PI, true);
        this.ctx.fill();

        this.ctx.filter = "none";
    }
}

function SpaceModel() {
    this.particles = [];
    this.elapsed = 0;
    this.total_mass = 0;

    this.add = function(p) {
        this.particles.push(p);
        this.total_mass += p.mass;
    };

    this.remove = function(p) {
        var idx = this.particles.indexOf(p);
        if (-1 != idx) {
            this.total_mass -= p.mass;
            this.particles.splice(idx, 1);
        }
    };

    this.step = function(dt) {
        this.elapsed += dt;

        var particles = this.particles;
        particles.forEach(function(p) { p.accelerate(particles); });
        particles.forEach(function(p) { p.step(dt); });
    }

    this.collide = function(p, into) {
        if (-1 == this.particles.indexOf(p) || -1 == this.particles.indexOf(into)) return false;
        into.absorb(p);
        return true;
    }

    this.barycenter = function() {
        var bpos = new Vector(0, 0);
        var bv = new Vector(0, 0);

        this.particles.forEach(function(p) {
            bpos.add(p.pos.mul(p.mass));
            bv.add(p.v.mul(p.mass));
        })
        return { pos: bpos.mul(1/this.total_mass), v: bv.mul(1/this.total_mass) };
    }

    this.recenter = function() {
        var b = this.barycenter();
        this.particles.forEach(function(p) { 
            p.pos = p.pos.sub(b.pos);
            p.v = p.v.sub(b.v);
        });
    }

    this.toString = function() {
        var str = "{ elapsed = "+this.elapsed;
        if (do_verbose) {
            str += ", ";
            this.particles.forEach(function(p) { str += p.toString()+", " });
        }
        str += " }";
        return str;
    }
}

function Space(model, ctx) {
    this.model = model;
    this.ctx = ctx;

    this.particles = [];
    this.pov_idx = 0;

    this.add = function(p) {
        p.ctx = ctx;

        this.particles.push(p);
        this.model.add(p.model);
    }

    this.remove = function(p) {
        var idx = this.particles.indexOf(p);
        if (-1 != idx) {
            this.model.remove(p.model);
            this.particles.splice(idx, 1);
        }
    }

    this.step = function(dt) {
        this.model.step(dt);

        var ip = this.interesting_particles();

        var tmp = ip.most_accel.a * step_dt;
        if (tmp > threshold && step_dt > 1) {
            step_dt /= 2;
        }
        if (tmp < (threshold/4)) {
            step_dt *= 2;
        }

        if (ip.closest.p1.model.pos.sub(ip.closest.p2.model.pos).magnitude() < ROCHE_LIMIT) {
            if (ip.closest.p1.model.mass > ip.closest.p2.model.mass) {
                this.collide(ip.closest.p2, ip.closest.p1);
            } else {
                this.collide(ip.closest.p1, ip.closest.p2);
            }
        }
    }

    this.render = function(do_bright) {
        var self = this;
        this.particles.forEach(function(p) { p.render(self.pov(), do_bright); });
    }

    this.clear = function() {
        this.ctx.fillStyle = 'black';
        this.ctx.fillRect(-canvas_width / 2, -canvas_height / 2, canvas_width, canvas_height);
        if (do_stop) this.render(true);
    }

    this.nextPOV = function() {
        this.pov_idx++;
        if (this.pov_idx >= this.particles.length) this.pov_idx = 0;
        this.clear();
    }

    this.prevPOV = function() {
        this.pov_idx--;
        if (this.pov_idx < 0) this.pov_idx = this.particles.length - 1;
        this.clear();
    }

    this.pov = function() {
        return this.particles[this.pov_idx];
    }

    this.interesting_particles = function() {
        var self = this;

        var most_accel = undefined;
        var min_d = -1;
        var closest_1 = undefined;
        var closest_2 = undefined;

        this.particles.forEach(function(p) {
            if (most_accel === undefined || p.model.a.magnitude_sq() > most_accel.model.a.magnitude_sq()) {
                most_accel = p;
            }

            self.particles.forEach(function(other) {
                if (other === p) return;

                var d = other.model.pos.sub(p.model.pos).magnitude_sq();
                if (-1 == min_d || d < min_d) {
                    min_d = d;
                    closest_1 = p;
                    closest_2 = other;
                }
            });
        });

        return { most_accel: { p: most_accel, a: most_accel.model.a.magnitude() }, closest: { d: Math.sqrt(min_d), p1: closest_1, p2: closest_2 } };
    }

    this.collide = function(p, into) {
        this.model.collide(p.model, into.model);
        this.remove(p);
    }
}

function print_params() {
    const params = document.querySelector("#parameters");
    var b = space.model.barycenter();
    var ip = space.interesting_particles();

    params.innerHTML = "space elapsed: "+Math.floor(space.model.elapsed)+" s ("+fmt.format(space.model.elapsed/31557600)+" yrs)<br>"+
                       "space barycenter { pos: "+b.pos.toString()+", vel: "+b.v.toString()+" }<br>"+
                       "closest pair: ['"+ip.closest.p1.name+"', '"+ip.closest.p2.name+"'] at "+Math.floor(ip.closest.d/1000)+" km<br>"+
                       "most accelerated: "+ip.most_accel.p.name+" at "+fmt.format(1000*ip.most_accel.a)+" mm/s<sup>2</sup><br>"+
                       "<br>"+
                       "POV: "+space.pov().name+"<br>"+
                       "zoom: "+fmt.format(zoom)+"<br>"+
                       "iterations_per_step: "+iterations_per_step+"<br>"+
                       "step_dt: "+step_dt;
}
       
function main() {
    const canvas = document.querySelector("#space");
    const ctx = canvas.getContext("2d");
    ctx.fillStyle = 'black';
    ctx.fillRect(0, 0, canvas_width, canvas_height);
    ctx.translate(canvas_width / 2, canvas_height / 2);

    document.addEventListener('keydown', function(event) {
        switch(event.key) {
            case 'w': zoom *= 1.1; space.clear(); break;
            case 's': zoom /= 1.1; space.clear(); break;
            case 'u': iterations_per_step *= 1.1; break;
            case 'j': iterations_per_step /= 1.1; break;
            case 'i': step_dt *= 1.1; break;
            case 'k': step_dt /= 1.1; break;
            case 'd': space.nextPOV(); break;
            case 'a': space.prevPOV(); break;
            case ' ': if (do_stop) start(); else stop(); break;
            case 'ArrowRight': stop(); step(true); break;
            case 'ArrowLeft': stop(); step(false); break;
        }
        print_params();
    });

    space = new Space(new SpaceModel(), ctx);
    var bar;

    /* sun */
    var sun = new Mass('sun', new MassModel(1.99e30, new Vector(0, 0), new Vector(0, 0)), 'yellow');
    space.add(sun);

    /* mercury */
    const mercury_r = 5.79100e10;
    bar = space.model.barycenter();
    var mercury_pos = new Vector().set_angle(0).set_magnitude(mercury_r).add(bar.pos);
    var mercury_v = mercury_pos.orthogonal_left().set_magnitude(Math.sqrt(G * space.model.total_mass / mercury_r)).add(bar.v);
    var mercury = new Mass('mercury', new MassModel(3.30e23, mercury_pos, mercury_v), 'gray');
    space.add(mercury);

    /* venus */
    const venus_r = 1.08200e11;
    bar = space.model.barycenter();
    var venus_pos = new Vector().set_angle(-Math.PI / 8).set_magnitude(venus_r).add(bar.pos);
    var venus_v = venus_pos.orthogonal_left().set_magnitude(Math.sqrt(G * space.model.total_mass / venus_r)).add(bar.v);
    var venus = new Mass('venus', new MassModel(4.87e24, venus_pos, venus_v), 'cyan');
    space.add(venus);

    /* earth */
    const earth_r = 1.49600e11;
    bar = space.model.barycenter();
    var earth_pos = new Vector().set_angle(-Math.PI / 4).set_magnitude(earth_r).add(bar.pos);
    var earth_v = earth_pos.orthogonal_left().set_magnitude(Math.sqrt(G * space.model.total_mass / earth_r)).add(bar.v);
    var earth = new Mass('earth', new MassModel(5.97e24, earth_pos, earth_v), 'blue');
    space.add(earth);

    /* mars */
    const mars_r = 2.27940e11;
    bar = space.model.barycenter();
    var mars_pos = new Vector().set_angle(-3 * Math.PI / 8).set_magnitude(mars_r).add(bar.pos);
    var mars_v = mars_pos.orthogonal_left().set_magnitude(Math.sqrt(G * space.model.total_mass / mars_r)).add(bar.v);
    var mars = new Mass('mars', new MassModel(6.42e23, mars_pos, mars_v), 'red');
    space.add(mars);

    /* jupiter */
    const jupiter_r = 7.78330e11;
    bar = space.model.barycenter();
    var jupiter_pos = new Vector().set_angle(-Math.PI / 2).set_magnitude(jupiter_r).add(bar.pos);
    var jupiter_v = jupiter_pos.orthogonal_left().set_magnitude(Math.sqrt(G * space.model.total_mass / jupiter_r)).add(bar.v);
    var jupiter = new Mass('jupiter', new MassModel(1.90e27, jupiter_pos, jupiter_v), 'orange');
    space.add(jupiter);

    /* saturn */
    const saturn_r = 1.42940e12;
    bar = space.model.barycenter();
    var saturn_pos = new Vector().set_angle(-5 * Math.PI / 8).set_magnitude(saturn_r).add(bar.pos);
    var saturn_v = saturn_pos.orthogonal_left().set_magnitude(Math.sqrt(G * space.model.total_mass / saturn_r)).add(bar.v);
    var saturn = new Mass('saturn', new MassModel(5.68e26, saturn_pos, saturn_v), 'green');
    space.add(saturn);

    /* uranus */
    const uranus_r = 2.87099e12;
    bar = space.model.barycenter();
    var uranus_pos = new Vector().set_angle(-3 * Math.PI / 4).set_magnitude(uranus_r).add(bar.pos);
    var uranus_v = uranus_pos.orthogonal_left().set_magnitude(Math.sqrt(G * space.model.total_mass / uranus_r)).add(bar.v);
    var uranus = new Mass('uranus', new MassModel(8.68e25, uranus_pos, uranus_v), 'pink');
    space.add(uranus);

    /* neptune */
    const neptune_r = 4.50430e12;
    bar = space.model.barycenter();
    var neptune_pos = new Vector().set_angle(-7 * Math.PI / 8).set_magnitude(neptune_r).add(bar.pos);
    var neptune_v = neptune_pos.orthogonal_left().set_magnitude(Math.sqrt(G * space.model.total_mass / neptune_r)).add(bar.v);
    var neptune = new Mass('neptune', new MassModel(1.02e26, neptune_pos, neptune_v), 'magenta');
    space.add(neptune);

    /* pluto */
    const pluto_r = 7.3752e+12; /* furthest point from the sun */
    bar = space.model.barycenter();
    var pluto_pos = new Vector().set_angle(-Math.PI).set_magnitude(pluto_r).add(bar.pos);
    var pluto_v = pluto_pos.orthogonal_left().set_magnitude(0.865 * Math.sqrt(G * space.model.total_mass / pluto_r)).add(bar.v);
    var pluto = new Mass('pluto', new MassModel(1.27e22, pluto_pos, pluto_v), 'lightGray');
    space.add(pluto);

    /* wandering star */
    //var wandering_star = new Mass('wandering star', new MassModel(1.99e30, new Vector(-2.0e12, 0), new Vector(0, 1.15e4)), 'white');
    //var wandering_star = new Mass('wandering star', new MassModel(1.99e30, new Vector(-6.0e13, -1.80000e12), new Vector(6.000e3, 0)), 'white');
    //space.add(wandering_star);

    space.model.recenter();
    space.render(true);

    print_params();
};

function step(do_forward) {
    space.render(false);
    for (var i = 0; i < iterations_per_step; i++) {
        space.step(do_forward ? step_dt : -step_dt);
    }
    space.render(true);

    if (do_stop || (iteration++ % 32) == 0) print_params();

}

function step_forward() {
    step(true);

    if (!do_stop) window.requestAnimationFrame(step_forward);
}

function start() {
    do_stop = false;
    window.requestAnimationFrame(step_forward);
}

function stop() {
    do_stop = true;
}
