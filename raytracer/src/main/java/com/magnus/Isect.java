package com.magnus;

public class Isect {
	public double t;
	public RObject prim;
	public int enter; // 0 entering, 1 leaving;
	public Surf medium;
	public int indexTriangle = 0;
	public Vec2 uvTriangle = new Vec2(0);
	//public double tMin,tMax;
	
	public Isect(double t, RObject prim,int enter,Surf medium) {
		this.t=t;
		this.prim=prim;
		this.enter=enter;
		this.medium=medium;
		//this.tMin = tmin;
		//this.tMax = tmax;
	}
	public Isect(RObject prim,Surf medium){
		t= 0;
		this.prim = prim;
		this.medium = medium;
		enter  =0 ;
		

	}
	
	
}


