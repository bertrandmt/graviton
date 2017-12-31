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
        MassModel self = this;

        particles.forEach(other -> {
            if (other==self) return;

            Vector d = other.pos.sub(self.pos);
            double r2 = d.magnitude_sq();
            double a_magnitude = GRAVITATION_CONSTANT * other.mass / r2;
            Vector a_local = d.setMagnitude(a_magnitude);

            a.add(a_local);
        });

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
