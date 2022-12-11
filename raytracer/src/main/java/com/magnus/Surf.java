package com.magnus;

public class Surf {
	public double kdiff = 0; // diffuse reflection coefficient
	public double kspec = 0; // specular reflection coefficient 
	public double ktran = 0; // transmission coefissent
	public Vec3 colour = new Vec3(255,255,255);
	public double refrindex = 0; // refraction index
	public double ktlucence = 0;      // translucency coefficient
	public Vec3 emission_colour = null;
	public double fuzz = 0.5;
}
