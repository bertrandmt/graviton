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
import java.awt.event.KeyListener;
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
        
        Dimension ss = new Dimension(640,480);//Toolkit.getDefaultToolkit().getScreenSize();
        
        Mass p ;
        
        /* sun */
        p = new Mass(1.99e30, 6.9500e8,  0         ,  0         ,  0                               ,  0                               , Color.yellow);
        space.addMass(p);
        space.setPoV(p);
        /* mercury */
        p = new Mass(3.30e23, 2.4400e6,  5.79100e10,  0         ,  0                               , -2*Math.PI*5.79100e10/7.6006080e6, Color.gray);
        space.addMass(p);
        /* venus */
        p = new Mass(4.87e24, 6.0520e6,  0         ,  1.08200e11,  2*Math.PI*1.08200e11/1.9414080e7,  0                               , Color.cyan);
        space.addMass(p);
        /* earth */
        p = new Mass(5.97e24, 6.3780e6, -1.49600e11,  0         ,  0                               ,  2*Math.PI*1.49600e11/3.1558464e7, Color.blue);
        space.addMass(p);
        /* mars */
        p = new Mass(6.42e23, 3.3970e6,  0         , -2.27940e11, -2*Math.PI*2.27940e11/5.9355072e7,  0                               , Color.red);
        space.addMass(p);
        /* jupiter */
        p = new Mass(1.90e27, 7.1492e7,  7.78330e11,  0         ,  0                               , -2*Math.PI*7.78330e11/3.7434614e8, Color.orange);
        space.addMass(p);
        /* saturn */
        p = new Mass(5.68e26, 6.0268e7,  0         ,  1.42940e12,  2*Math.PI*1.42940e12/9.2962080e8,  0                               , Color.green);
        space.addMass(p);
        /* uranus */
        p = new Mass(8.68e25, 2.5559e7, -2.87099e12,  0         , 0                                ,  2*Math.PI*2.87099e12/2.6511840e9, Color.pink);
        space.addMass(p);
        /* neptune */
        p = new Mass(1.02e26, 2.4766e7,  0         , -4.50430e12, -2*Math.PI*4.50430e12/5.2004160e9, 0                                , Color.magenta);
        space.addMass(p);
        /* pluto */
        p = new Mass(1.27e22, 1.1370e6,  5.91352e12,  0         ,  0                               , -2*Math.PI*5.91352e12/7.8451200e9, Color.lightGray);
        space.addMass(p);
        /* wandering star */
        //p = new Mass(1.99e30, 6.9500e8, -6.0e13    , -4.05000e12,  6.000e4                         ,  0                               , Color.green);
        //p = new Mass(1.99e30, 6.9500e8, -6.0e13    , -4.48000e12,  6.000e4                         ,  0                               , Color.green);
        p = new Mass(1.99e30, 6.9500e8, -6.0e13    , -1.80000e12,  6.000e3                         ,  0                               , Color.green);
        space.addMass(p);
        
        w = new JFrame();
        w.setSize(ss);
        w.addMouseListener(new MouseAdapter()
        {
            public void mousePressed(MouseEvent evt)
            {
                shutdown();
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
        /*
        final Object monitor = new Object();
        Runnable r = new Runnable()
        {
            public void run()
            {
                //System.out.println("Task up and running");
                long past = new Date().getTime(), cur;
                while (true)
                {
                    try { synchronized(monitor) { monitor.wait(); } }
                    catch (InterruptedException e) {}
                    
                    cur = new Date().getTime();
                    System.out.println("Running after " + (cur - past) + " ms");
                    past = cur;
                }
            }
        };
        TimerTask tt = new TimerTask()
        {
            public void run()
            {
                //System.out.println("Timer task run at " + new Date().getTime());
                synchronized(monitor) { monitor.notify(); }
            }
        };
        Thread t1 = new Thread(r);
        Timer t2 = new Timer();
        
        t1.start();
        t2.scheduleAtFixedRate(tt, new Date(), 1000/70);
        */
    }
}
