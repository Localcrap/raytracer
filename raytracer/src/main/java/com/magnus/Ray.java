package com.magnus;
public class Ray {
    public Vec3 origin;
    public Vec3 direction;

    //Vec3 v;
    public Ray(Vec3 origin,Vec3 direction) {
    	this.origin = origin;
    	this.direction = direction;
    }
    public Ray() {
    	this.origin = null;
    	this.direction = null;
    }
    public Ray copy() {
    	return new Ray(origin,direction);
    }
    public void setOrigin(Vec3 origin) {
    	this.origin=origin;
    	
    }
    public void setDirection(Vec3 direction) {
    	this.direction = direction;
    }
}
