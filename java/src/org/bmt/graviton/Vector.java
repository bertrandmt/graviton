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
