/*
 * Mass.java
 *
 * Created on 11 fevrier 2002, 10:00
 */

package org.bmt.graviton;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.RenderingHints;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;

/**
 *
 * @author  bmt
 * @version
 */
public class Mass
{
    final MassModel model;
    
    final Color c;
    
    /** Creates new Mass */
    public Mass(double m, double r, Color c)
    {
        this(m, r, 0, 0, 0, 0, c);
    }
    
    public Mass(double m, double r, double x, double y, Color c)
    {
        this(m, r, x, y, 0, 0, c);
    }
    
    public Mass(double m, double r, double x, double y, double vx, double vy, Color c)
    {
        this(new MassModel(m, r, x, y, vx, vy), c);
    }
    
    public Mass(MassModel model, Color c)
    {
        this.model = model;
        
        this.c = c;
    }
    
    public void render(Graphics2D g2d)
    {
        Ellipse2D shape = new Ellipse2D.Double(model.x - model.r, model.y - model.r, 2*model.r, 2*model.r);
        g2d.setColor(c);
        g2d.fill(shape);
    }
    
    public void clear(Graphics2D g2d)
    {
        Rectangle2D clearRect = new Rectangle2D.Double(model.x - model.r - 1/Space.SCALE, model.y - model.r - 1/Space.SCALE, 2*(model.r + 1/Space.SCALE), 2*(model.r + 1/Space.SCALE));
        g2d.setColor(Space.BG_COLOR);
        g2d.fill(clearRect);
    }
}
