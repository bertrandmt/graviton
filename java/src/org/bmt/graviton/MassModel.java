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
 * Mass.java
 *
 * Created on 9 fevrier 2002, 21:03
 */
package org.bmt.graviton;

import java.util.Iterator;
import java.util.Set;

/**
 *
 * @author  bmt
 * @version
 */
public class MassModel
{
    double mass;

    Vector pos;
    Vector v;
    Vector a;

    public static final double GRAVITATION_CONSTANT = 6.67259e-11;

    public MassModel(double mass)
    {
        this(mass, new Vector(), new Vector());
    }

    public MassModel(double mass, Vector pos)
    {
        this(mass, pos, new Vector());
    }

    public MassModel(double mass, Vector pos, Vector v)
    {
        this.mass = mass;
        this.pos = pos;
        this.v = v;
        this.a = new Vector();
    }

    public void accelerate(Set<MassModel> particles)
    {
        Vector a = new Vector();

        for (MassModel other : particles) {
            if (other==this) continue;

            Vector d = other.pos.sub(this.pos);
            double r2 = d.magnitude_sq();
            double a_magnitude = GRAVITATION_CONSTANT * other.mass / r2;
            Vector a_local = d.setMagnitude(a_magnitude);

            a.add(a_local);
        }

        this.a = a;
    }

    public void step(double dt)
    {
        this.v.add(this.a.mul(dt));

        this.pos.add(this.v.mul(dt));
    }

    public void absorb(MassModel other) {
        double mass_ratio = other.mass / (other.mass + this.mass);
        this.v.add(other.v.sub(this.v).mul(mass_ratio));
        this.pos.add(other.pos.sub(this.pos).mul(mass_ratio));
        this.mass += other.mass;
    }

    public String toString()
    {
        StringBuffer b = new StringBuffer();

        b.append("{pos:{x:").append(this.pos.x).append(",y:").append(this.pos.y).append("},")
         .append("{v:{x:")  .append(this.v.x)  .append(",y:").append(this.v.y)  .append("},")
         .append("{a:{x:")  .append(this.a.x)  .append(",y:").append(this.a.y)  .append("}}");

        return b.toString();
    }
}
