/*
 * JGraviton.java
 *
 * Created on 9 fevrier 2002, 11:19
 */
package org.bmt.graviton;

import java.util.*;

import java.util.Random;

import java.awt.Color;
import java.awt.Toolkit;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;

/**
 *
 * @author  bmt
 * @version
 */
public class JGraviton
{
    Space space;
    JFrame w;

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
                switch(evt.getKeyCode()) {
                    case 'D': space.nextPOV(); break;
                    case 'A': space.prevPOV(); break;
                }
            }
        });
        w.setContentPane(space);
        w.setVisible(true);

        space.start();
    }

    public void shutdown()
    {
        space.stop();
        w.dispose();
        System.exit(0);
    }

    public static void main(String[] args)
    throws
    Exception
    {
        new JGraviton();
    }
}
