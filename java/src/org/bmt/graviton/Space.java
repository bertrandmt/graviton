/*
 * Window.java
 *
 * Created on 9 fevrier 2002, 11:22
 */

package org.bmt.graviton;

import org.bmt.graviton.Mass;
import org.bmt.graviton.SpaceModel;

import java.io.PrintWriter;
import java.io.FileOutputStream;

import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;

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
    SpaceModel model;
    Set<Mass> particles;
    Iterator<Mass> povIterator;
    Mass pov;

    PrintWriter log;

    boolean do_clear;

    public Space()
    throws
        Exception
    {
        this(new SpaceModel());
    }

    public Space(final SpaceModel model)
    throws
        Exception
    {
        super();
        this.model = model;
        particles = new HashSet<Mass>();
        povIterator = particles.iterator();
        pov = null;
        log = new PrintWriter(new FileOutputStream("jgraviton.log"));
        do_clear = true;
    }

    public void add(Mass p)
    {
        particles.add(p);
        povIterator = particles.iterator();
        pov = povIterator.next();
        model.add(p.model);
    }

    public boolean remove(Mass p) {
        boolean removed = particles.remove(p);
        if (removed) {
            povIterator = particles.iterator();
            if (povIterator.hasNext()) {
                pov = povIterator.next();
            }
            boolean inner_removed = model.remove(p.model);
            assert inner_removed : "Model does not contain particle that space contains";
        }
        return removed;
    }

    public void nextPOV() {
        if (povIterator.hasNext()) {
            pov = povIterator.next();
        }
        else {
            povIterator = particles.iterator();
            if (povIterator.hasNext()) {
                pov = povIterator.next();
            }
            else pov = null;
        }
        do_clear = true;;
    }

    public void prevPOV() {
        /* don't have a backwards iterator in this case */
        do_clear = true;
    }

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

        if (do_clear) do_clear = false;
    }

    static final double SCALE=1e-13*300;

    private Graphics2D getG2D() {
        Graphics2D g2d;

        if ((vi == null) || (vi.validate(getGraphicsConfiguration()) == VolatileImage.IMAGE_INCOMPATIBLE))
        {
            vi = this.createVolatileImage(this.getWidth(), this.getHeight());
            g2d = vi.createGraphics();
            g2d.setColor(Color.black);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
        else
        {
            g2d = vi.createGraphics();
        }

        return g2d;
    }

    public void render(int width, int height)
    {
        if (do_clear) {
            clear();
        }
        else {
            Graphics2D g2d = getG2D();

            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.scale(SCALE,SCALE);
            if (pov != null) {
                g2d.translate(width/(2*SCALE)-pov.model.pos.x, height/(2*SCALE)-pov.model.pos.y);
            }
            else {
                g2d.translate(width/(2*SCALE), height/(2*SCALE));
            }

            particles.forEach(p -> p.render(g2d));

            g2d.dispose();
        }
    }

    private void clear() {
        Graphics2D g2d = getG2D();

        g2d.setColor(Color.black);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        g2d.dispose();
    }

    Thread runner = null;

    public void start()
    {
        if (runner != null)
        {
            stop();
        }
        runner = new Thread(this, "Space modelizer");
        runner.start();
    }

    public void stop()
    {
        if (runner != null)
        {
            log.close();
            runner.interrupt();
            runner = null;
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

            double dt = 1000;
            for (int i=0;i<200;i++) model.step(dt);
            log.println(model.toString());

            repaint();
        }
    }
}
