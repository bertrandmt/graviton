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
 * JGraviton.java
 *
 * Created on 9 fevrier 2002, 11:19
 */
package org.bmt.graviton;

import java.util.*;

import java.util.Random;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.VolatileImage;

import javax.swing.JFrame;

/**
 *
 * @author  bmt
 * @version
 */
public class JGraviton
{
    class SpaceContainer extends Container {
        VolatileImage vi;

        void renderOffscreen(boolean do_clear) {
            do {
                if (vi.validate(getGraphicsConfiguration()) == VolatileImage.IMAGE_INCOMPATIBLE) {
                    vi = createVolatileImage(getWidth(), getHeight());
                    do_clear = true;
                }

                Graphics2D g2d = vi.createGraphics();

                if (do_clear) {
                    g2d.setColor(Color.black);
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                }

                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.scale(SCALE,SCALE);
                g2d.translate(getWidth()/(2*SCALE), getHeight()/(2*SCALE));

                space.render(g2d);

                g2d.dispose();
            } while (vi.contentsLost());
        }

        public void paint(Graphics g)
        {
            if (vi == null) {
                vi = createVolatileImage(getWidth(), getHeight());
            }
       
            do {
                int returnCode = vi.validate(getGraphicsConfiguration());
                if (returnCode == VolatileImage.IMAGE_RESTORED) {
                    // Contents need to be restored
                    renderOffscreen(true);      // restore contents
                } else if (returnCode == VolatileImage.IMAGE_INCOMPATIBLE) {
                    // old vi doesn't work with new GraphicsConfig; re-create it
                    vi = createVolatileImage(getWidth(), getHeight());
                    renderOffscreen(true);
                }
                g.drawImage(vi, 0, 0, this);
            } while (vi.contentsLost());
        }
    };
 
     Space space;
    JFrame w;
    SpaceContainer spaceContainer;

    boolean do_forward = true;
    double iterations_per_step = 200;
    double step_dt = 1000;
    Thread runner = null;

    static final double SCALE=1e-13*300;

   /** Creates new JGraviton */
    public JGraviton()
    throws
    Exception
    {
        space = new Space();

        Dimension ss = Toolkit.getDefaultToolkit().getScreenSize();

        double r;
        Vector pos;
        Vector v;
        Mass p ;
        MassModel bar;

        /* sun */
        p = new Mass(new MassModel(1.99e30), 6.9500e8, Color.yellow);
        space.add(p);
        bar = space.model.barycenter;

        /* mercury */
        r = 5.79100e10;
        pos = new Vector().setAngle(0).setMagnitude(r).add(bar.pos);
        v = pos.orthogonalLeft().setMagnitude(Math.sqrt(MassModel.GRAVITATION_CONSTANT * space.model.totalMass / r)).add(bar.v);
        p = new Mass(new MassModel(3.30e23,  pos, v), 2.4400e6, Color.gray);
        space.add(p);
        bar = space.model.barycenter;

        /* venus */
        r = 1.08200e11;
        pos = new Vector().setAngle(-Math.PI/8).setMagnitude(r).add(bar.pos);
        v = pos.orthogonalLeft().setMagnitude(Math.sqrt(MassModel.GRAVITATION_CONSTANT * space.model.totalMass / r)).add(bar.v);
        p = new Mass(new MassModel(4.87e24, pos, v), 6.0520e6, Color.cyan);
        space.add(p);
        bar = space.model.barycenter;

        /* earth */
        r = 1.49600e11;
        pos = new Vector().setAngle(-Math.PI/4).setMagnitude(r).add(bar.pos);
        v = pos.orthogonalLeft().setMagnitude(Math.sqrt(MassModel.GRAVITATION_CONSTANT * space.model.totalMass / r)).add(bar.v);
        p = new Mass(new MassModel(5.97e24, pos, v), 6.3780e6, Color.blue);
        space.add(p);
        bar = space.model.barycenter;

        /* mars */
        r = 2.27940e11;
        pos = new Vector().setAngle(-3*Math.PI/8).setMagnitude(r).add(bar.pos);
        v = pos.orthogonalLeft().setMagnitude(Math.sqrt(MassModel.GRAVITATION_CONSTANT * space.model.totalMass / r)).add(bar.v);
        p = new Mass(new MassModel(6.42e23, pos, v), 3.3970e6, Color.red);
        space.add(p);
        bar = space.model.barycenter;

        /* jupiter */
        r = 7.78330e11;
        pos = new Vector().setAngle(-Math.PI/2).setMagnitude(r).add(bar.pos);
        v = pos.orthogonalLeft().setMagnitude(Math.sqrt(MassModel.GRAVITATION_CONSTANT * space.model.totalMass / r)).add(bar.v);
        p = new Mass(new MassModel(1.90e27, pos, v), 7.1492e7, Color.orange);
        space.add(p);
        bar = space.model.barycenter;

        /* saturn */
        r = 1.42940e12;
        pos = new Vector().setAngle(-5*Math.PI/8).setMagnitude(r).add(bar.pos);
        v = pos.orthogonalLeft().setMagnitude(Math.sqrt(MassModel.GRAVITATION_CONSTANT * space.model.totalMass / r)).add(bar.v);
        p = new Mass(new MassModel(5.68e26, pos, v), 6.0268e7, Color.green);
        space.add(p);
        bar = space.model.barycenter;

        /* uranus */
        r = 2.87099e12;
        pos = new Vector().setAngle(-3*Math.PI/4).setMagnitude(r).add(bar.pos);
        v = pos.orthogonalLeft().setMagnitude(Math.sqrt(MassModel.GRAVITATION_CONSTANT * space.model.totalMass / r)).add(bar.v);
        p = new Mass(new MassModel(8.68e25, pos, v), 2.5559e7, Color.pink);
        space.add(p);
        bar = space.model.barycenter;

        /* neptune */
        r = 4.50430e12;
        pos = new Vector().setAngle(-7*Math.PI/8).setMagnitude(r).add(bar.pos);
        v = pos.orthogonalLeft().setMagnitude(Math.sqrt(MassModel.GRAVITATION_CONSTANT * space.model.totalMass / r)).add(bar.v);
        p = new Mass(new MassModel(1.02e26, pos, v), 2.4766e7, Color.magenta);
        space.add(p);
        bar = space.model.barycenter;

        /* pluto */
        r = 7.3752e+12; /* furthest point from the sun */
        pos = new Vector().setAngle(-Math.PI).setMagnitude(r).add(bar.pos);
        v = pos.orthogonalLeft().setMagnitude(0.865 * Math.sqrt(MassModel.GRAVITATION_CONSTANT * space.model.totalMass / r)).add(bar.v);
        p = new Mass(new MassModel(1.27e22, pos, v), 1.1370e6, Color.lightGray);
        space.add(p);
        bar = space.model.barycenter;

        /* wandering star */
        //p = new Mass(new MassModel(1.99e30, new Vector(-6.0e13, -4.05000e12), new Vector(6.000e4, 0)), 6.9500e8, Color.green);
        //p = new Mass(new MassModel)1.99e30, new Vector(-6.0e13, -4.48000e12), new Vector(6.000e4, 0)), 6.9500e8, Color.green);
        p = new Mass(new MassModel(1.99e30, new Vector(-6.0e13, -1.80000e12), new Vector(6.000e3, 0)), 6.9500e8, Color.green);
        space.add(p);

        space.model.recenter();

        w = new JFrame();
        w.setSize(ss);
        w.addMouseListener(new MouseAdapter()
        {
            public void mousePressed(MouseEvent evt)
            {
                shutdown();
            }
        });
        w.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent evt)
            {
                System.out.println(evt);
                switch(evt.getKeyCode()) {
                    case 'D': space.nextPOV(); spaceContainer.renderOffscreen(true); break;
                    case 'A': space.prevPOV(); spaceContainer.renderOffscreen(true); break;
                    case 39: stop(); step(); break;
                    case 32: if (runner == null) start(); else stop(); break;
                }
            }
        });
        spaceContainer = new SpaceContainer();
        w.setContentPane(spaceContainer);
        w.setVisible(true);

        start();
    }

    public void step() {
        for (int i = 0; i < iterations_per_step; i++) {
            space.model.step(do_forward ? step_dt : -step_dt);
        }
        space.log.println(space.model.toString());
        spaceContainer.renderOffscreen(false);
        spaceContainer.repaint();
    }

    public void shutdown()
    {
        stop();
        space.log.close();
        w.dispose();
        System.exit(0);
    }

    public void start() {
        if (runner != null) {
            stop();
        }
        runner = new Thread(new Runnable() {
            public void run() {
                while(true) {
                    if (Thread.currentThread() != runner) {
                        break;
                    }
        
                    step();
                }
            }
        });
        runner.start();
    }

    public void stop() {
        if (runner != null) {
            runner = null;
        }
    }

    public static void main(String[] args)
    throws
    Exception
    {
        new JGraviton();
    }
}
