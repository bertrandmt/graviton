/*
 * Window.java
 *
 * Created on 9 février 2002, 11:22
 */

package org.bmt.graviton;

import org.bmt.graviton.JGraviton;
import org.bmt.graviton.Particle;
import org.bmt.graviton.SpaceModel;
import org.bmt.graviton.event.SpaceEvent;
import org.bmt.graviton.event.SpaceListener;

import java.io.PrintWriter;
import java.io.FileOutputStream;

import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import java.util.Collections;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import javax.swing.*;

/**
 *
 * @author  bmt
 * @version
 */
public class Space
extends
Container
implements
Runnable
{
    static final Color BG_COLOR = Color.black;
    
    SpaceModel model;
    Set particles;
    Particle pov;
    
    PrintWriter log;
    
    /** Creates new Window */
    public Space()
    throws
        Exception
    {
        this(new SpaceModel());
        log = new PrintWriter(new FileOutputStream("jgraviton.log"));
    }
    
    public Space(final SpaceModel model)
    {
        super();
        this.model = model;
        particles = new HashSet();
    }
    
    public SpaceModel getModel()
    {
        return model;
    }
    
    public void addParticle(Particle p)
    {
        particles.add(p);
        model.addParticle(p.model);
    }

    public void setPoV(Particle p)
    {
        if (!particles.contains(p)) throw new IllegalArgumentException("The particle does not belong to this space");
        pov = p;
    }
            
    BufferedImage bi;
    VolatileImage vi;
    
    public void paint(Graphics g)
    {
        render(this.getWidth(), this.getHeight());
        
        do
        {
            int returnCode = vi.validate(getGraphicsConfiguration());
            if ((returnCode == VolatileImage.IMAGE_RESTORED) || (returnCode == VolatileImage.IMAGE_INCOMPATIBLE))
            {
                render(this.getWidth(), this.getHeight());
            }
            g.drawImage(vi, 0, 0, null);
        } while (vi.contentsLost());
        
        clear(this.getWidth(), this.getHeight());
    }
    
    static final double SCALE=1e-13*300;
    
    public void render(int width, int height)
    {
        Graphics2D g2d;
            
        double dt = 10000;
        for (int i=0;i<100;i++) model.step(dt, width, height);
        log.println(model.toString());
        
        do
        {
            if ((vi == null) || (vi.validate(getGraphicsConfiguration()) == VolatileImage.IMAGE_INCOMPATIBLE))
            {
                vi = this.createVolatileImage(this.getWidth(), this.getHeight());
                g2d = vi.createGraphics();
                g2d.setBackground(BG_COLOR);
                g2d.clearRect(0, 0, width, height);
            }
            else
            {
                g2d = vi.createGraphics();
            }
            
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.scale(SCALE,SCALE);
            g2d.translate(320/SCALE-pov.model.x, 240/SCALE-pov.model.y);
            
            for (Iterator i = particles.iterator(); i.hasNext(); )
            {
                Particle p = (Particle)i.next();
                ParticleModel pm = p.model;
                p.render(g2d);
            }
            
            g2d.dispose();
        }
        while (vi.contentsLost());
    }
    
    public void clear(int width, int height)
    {
        if (!vi.contentsLost() && (vi != null) && (vi.validate(getGraphicsConfiguration()) != VolatileImage.IMAGE_INCOMPATIBLE))
        {
            Graphics2D g2d = vi.createGraphics();
            g2d.scale(SCALE,SCALE);
            g2d.translate(320/SCALE-pov.model.x, 240/SCALE-pov.model.y);
            //for (Iterator i = particles.iterator(); i.hasNext(); ) ((Particle)i.next()).clear(g2d);
            g2d.dispose();
        }
        while (vi.contentsLost());
    }
    
    Thread runner = null;
    
    public void start()
    {
        if (runner != null)
        {
            stop();
        }
        runner = new Thread(this, "Space modelizer");
        //runner.setPriority(runner.getPriority());
        runner.start();
    }
    
    public void stop()
    {
        if (runner != null)
        {
            log.close();
            runner.interrupt();
        }
    }
    
    public void run()
    {
        while(true)
        {
            if (Thread.currentThread().interrupted())
            {
                break;
            }
            /*try
            {
                Thread.currentThread().sleep(1000/100);
            }
            catch (InterruptedException e)
            {
            }*/
            repaint();
        }
    }
}
