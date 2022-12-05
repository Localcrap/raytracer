package com.magnus;
public class Vec3 {
	
	public double x,y,z;
	public Vec3() {
		this.x=0;
		this.y=0;
		this.z=0;
	}
	public Vec3(double x,double y,double z) {
		this.x=x;
		this.y=y;
		this.z=z;
	}
	public Vec3(double x) {
		this.x=x;
		this.y=x;
		this.z=x;
	}
	
	
	public double length() {
		return Math.sqrt(length2());
	}
	public double length2() {
		return x*x+y*y+z*z;
	}
	public void normalize() {
		double nor2 = length2();
		if (nor2 > 0) {
			double invNor = 1/ Math.sqrt(nor2);
			x *= invNor;
			y *= invNor;
			z *= invNor;
			
		}
		else {
			assert(false);
		}
	}
	public double dot(Vec3 v) {
		return  x*v.x+y*v.y+z*v.z;

	}
	public void add(Vec3 v) {
		x = x+v.x;
		y = v.y+y;
		z = v.z+z;
		
	}
	public Vec3 add(Vec3 v, Vec3 out) {
		out.setValues(x+v.x,y+v.y,z+v.z);
		return out;
		
	}
	public void sub(Vec3 v) {
		x = x-v.x;
		y = y-v.y;
		z =  z-v.z;
		
	}
	public Vec3 sub(Vec3 v, Vec3 out) {
		out.setValues(x-v.x,y-v.y,z-v.z);
		return out;
		
	}

	public  void addS(double t,Vec3 b) {
		x = x*t+b.x;
		y = y*t+b.y;
		z = z*t+b.z;
		
	}
	public  Vec3 addS(double t,Vec3 b,Vec3 out) {
		out.setValues(x*t+b.x,y*t+b.y,z*t+b.z);
		return out;
	}


	public  void negate() {
		x = x*-1;
		y = y*-1;
		z = z*-1;
	}

	public  Vec3 negate(Vec3 out) {
		out.setValues(x*-1,y*-1,z*-1);
		return out;
	}
	public Vec3 copy() {
		return new Vec3(x,y,z);
	}
	public double distance(Vec3 p) {
		
		return (x-p.x)*(x-p.x) +(y-p.y)*(y-p.y)+(z-p.z)*(z-p.z) ;
	}
	public void mult(double t) {
		x = x*t;
		y = y*t;
		z = z*t;
		
	}
	public Vec3 mult(double t,Vec3 out) {
		out.setValues(x*t,y*t,z*t);
		return out;
		
	}
	
	public void mult(Vec3 t) {
		x = x*t.x;
		y = y*t.y;
		z = z*t.z;
		
	}
	public Vec3 mult(Vec3 t, Vec3 out) {
		out.setValues(x*t.x,y*t.y,z*t.z);
		return out;
		
	}
	

	public void setZero() {
		x = 0;
		y = 0; 
		z = 0;
	}
	public void comb(double a,Vec3 B,double b ) {
		x = a*x+b*B.x;
		y = a*y+b*B.y;
		z = a*z+b*B.z;
	}
	public Vec3 comb(double a,Vec3 B,double b ,Vec3 out) {
		out.setValues(a*x+b*B.x,a*y+b*B.y,a*z+b*B.z);
		return out;
	}

	public void setValues(double x,double y,double z) {
		this.x= x; 
		this.y=y;
		this.z= z;
	}
	public void setValuesV(Vec3 v) {
		this.x= v.x; 
		this.y=v.y;
		this.z= v.z;
	}
	public void cross(Vec3 v){
		x = (y*v.z)-(z*v.y);
		y = (z*v.x)-(x*v.z);
		z = (x*v.y)-(y*v.x);
	}

	public Vec3 cross(Vec3 v, Vec3 out){
		out.setValues((y*v.z)-(z*v.y),(z*v.x)-(x*v.z),(x*v.y)-(y*v.x));
		return out;
	}
	public void inv(){
		x = -x;
		y = -y;
		z = -z;

		//return new Vec3(-x,-y,-z);
	}
	public Vec3 inv(Vec3 out){
		out.setValues(-x,-y,-z);
		return out;
	}
	public void div(double i){
		x = x/i;
		y = y/i;
		z = z/i;
	}
	public Vec3 div(double i, Vec3 out){
		out.setValues(x/i, y/i, z/i);
		return out;
	}

	public double itterate(int i){
        switch (i) {
            case 0:
                return x;
			
			case 1:
                return y;

			case 2: 
				return z;
        
            default:
				throw new  NullPointerException("out of bounds");
        }
    }
	public static Vec3 random(){
		return new Vec3(Math.random(),Math.random(),Math.random());
	}
	public static Vec3 random(int min,int max ){
		return new Vec3(raytracer.getRandomNumber(min, max),
			raytracer.getRandomNumber(min, max),raytracer.getRandomNumber(min, max));
	}
	public Vec3 unitVector(){
		Vec3 v = copy();
		return v.div(length(),v);
	}
	public static Vec3 randomInUnitSphere(){
		Vec3 v;
		while(true){
			v = random(-1,1);
			if(v.length2()<1){
				return v;
			}

		}
	}

	public static Vec3 randomUnitVector(){
		return randomInUnitSphere().unitVector();
	}
	
}