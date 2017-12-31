/*
 * Mass.java
 *
 * Created on 11 fevrier 2002, 10:00
 */

package org.bmt.graviton;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

/**
 *
 * @author  bmt
 * @version
 */
public class Mass
{
    final MassModel model;

    final double radius;
    final Color c;

    public Mass(MassModel model, double radius, Color c)
    {
        this.model = model;
        this.radius = Math.max(radius, 0.4/Space.SCALE);
        this.c = c;
    }

    public void render(Graphics2D g2d)
    {
        Ellipse2D shape = new Ellipse2D.Double(model.pos.x - this.radius, model.pos.y - this.radius, 2*this.radius, 2*this.radius);
        g2d.setColor(c);
        g2d.fill(shape);
    }
}
