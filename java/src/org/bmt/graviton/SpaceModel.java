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
/*
 * Space.java
 *
 * Created on 9 fevrier 2002, 11:59
 */

package org.bmt.graviton;

import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;

/**
 *
 * @author  bmt
 * @version
 */
public class SpaceModel
{
    Set<MassModel> particles;
    double elapsed;
    double totalMass;
    MassModel barycenter;

    public SpaceModel()
    {
        particles = new HashSet<MassModel>();
        elapsed = 0;
        totalMass = 0;
    }

    public void add(MassModel p)
    {
        particles.add(p);
        totalMass += p.mass;
        barycenter(); /* recompute the barycenter */
    }

    public boolean remove(MassModel p)
    {
        boolean removed = particles.remove(p);
        if (removed) {
            totalMass -= p.mass;
            barycenter(); /* recompute the barycenter */
        }
        return removed;
    }

    double totalMass()
    {
        totalMass = particles.stream().mapToDouble(p -> p.mass).sum();
        return totalMass;
    }

    public void step(double dt)
    {
        elapsed += dt;

        particles.forEach(p -> p.accelerate(particles));
        particles.forEach(p -> p.step(dt));
    }

    public boolean collide(MassModel p, MassModel into) {
        if (!particles.contains(p) || !particles.contains(into)) return false;

        into.absorb(p);
        return true;
    }

    public MassModel barycenter() {
        barycenter = new MassModel(totalMass);

        particles.forEach(p -> {
            barycenter.pos.add(p.pos.mul(p.mass));
            barycenter.v.add(p.v.mul(p.mass));
        });

        barycenter.pos = barycenter.pos.mul(1/totalMass);
        barycenter.v = barycenter.v.mul(1/totalMass);

        return barycenter;
    }

    public void recenter() {
        /* recompute barycenter */
        barycenter();

        particles.forEach(p -> {
            p.pos = p.pos.sub(barycenter.pos);
            p.v = p.v.sub(barycenter.v);
        });
    }

    public String toString()
    {
        StringBuffer b = new StringBuffer();

        b.append(elapsed)
         .append(",");

        for (Iterator i = particles.iterator(); i.hasNext(); ) b.append(i.next().toString()).append(",");

        return b.toString();
    }
}
