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
package org.bmt.graviton;

public class Vector {
    double x;
    double y;

    public Vector() {
        this(0, 0);
    }

    public Vector(double x, double y) {
        this.x = x;
        this.y = y;
    }

    double magnitude() {
        return Math.sqrt(x*x + y*y);
    }

    double magnitude_sq() {
        return x*x + y*y;
    }

    Vector mul(double scalar) {
        return new Vector(x * scalar, y * scalar);
    }

    Vector setMagnitude(double magnitude) {
        return mul(magnitude / this.magnitude());
    }

    Vector setAngle(double theta) {
        this.x = Math.cos(theta);
        this.y = Math.sin(theta);
        return this;
    }

    Vector orthogonalLeft() {
        return new Vector(this.y, -this.x);
    }

    Vector sub(Vector other) {
        return new Vector(this.x - other.x, this.y - other.y);
    }

    Vector add(Vector other) {
        this.x += other.x;
        this.y += other.y;
        return this;
    }
}
