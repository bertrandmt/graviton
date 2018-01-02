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
 * Window.java
 *
 * Created on 9 fevrier 2002, 11:22
 */

package org.bmt.graviton;

import org.bmt.graviton.Mass;
import org.bmt.graviton.SpaceModel;

import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 *
 * @author  bmt
 * @version
 */
public class Space
{
    SpaceModel model;
    Set<Mass> particles;
    Iterator<Mass> povIterator;
    Mass pov;

    public Space(final SpaceModel model) {
        this.model = model;
        particles = new HashSet<Mass>();
        povIterator = particles.iterator();
        pov = null;
    }

    public void add(Mass p) {
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
    }

    public void prevPOV() {
        /* don't have a backwards iterator in this case */
    }

    public void render(Graphics2D g2d) {
        if (pov != null) {
            g2d.translate(-pov.model.pos.x, -pov.model.pos.y);
        }

        particles.forEach(p -> p.render(g2d));
    }
}
