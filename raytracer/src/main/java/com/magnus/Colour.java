package com.magnus;

public class Colour extends Vec3 {
	
	public Colour(double x, double y, double z) {
		super(x, y, z);
		// TODO Auto-generated constructor stub
	}

	//public int r,g,b;
	
	public void setZero() {
		x = 0;
		y = 0; 
		z = 0;
	}
	@Override
	public Colour add(Vec3 v,Vec3 out) {
		return new Colour(x+v.x,y+v.y,z+v.z);
	}
	@Override
	public Colour copy() {
		return new Colour(x,y,z);
	}
	
}
