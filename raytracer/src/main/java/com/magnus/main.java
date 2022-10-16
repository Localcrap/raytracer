package com.magnus;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;



class raytracer{

	public enum test{
		SPHERE,TRIANGLEMESH
	}


    public final static int MAX_RAY_DEPTH = 99;
    public final static int IMAGE_HIGHT = 500;
    public final static int IMAGE_WIDTH = 500;
    public final static int ISECTMAX = 100;
    public Comp modelroot;
    
    public static int maxlevel = 20; 
    public static double minweight = 0.001;
    public static double rayeps  = 0.0000001;
    //objects should for optimization reasons be sorted after closest proximity to camera point
    public static RObject[] objects  = new RObject[3];
	//public static TriangleMesh[] trMesh = new TriangleMesh[10];
    //public static Sphere[] lightSources = new Sphere[1];
    
    
    



    public static void main(String args[]) {
        //Matrix m = new Matrix(IMAGE_HIGHT, IMAGE_WIDTH);
        Camera c;
        Vec3 pos = new Vec3(0,0,20), dir = new Vec3(0,0,0);
        
        c = new Camera(pos,60,dir,IMAGE_WIDTH,IMAGE_HIGHT);
        setupObjects();
        try {
			alg(c);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
    }
    
    private static void setupObjects() {
    	objects[0] = new Sphere(10000,new Vec3( 0, -10004, -20),new Vec3(0.2, 0.2, 0.2), 0, 0,0.0,0,0,null);
		//objects[1] = new Sphere(4,new Vec3( 0.0,      0, -20),new Vec3(1.00, 0.32, 0.36), 0, 1,0,0,0.5,null);
		//objects[2] = new Sphere(2,new Vec3( 5, -1, -15),new Vec3(0.90, 0.76, 0.46),0, 0,0,0,0.001,null);
		//objects[3] = new Sphere(3,new Vec3( 5, 0, -25),new Vec3(0.65, 0.77, 0.97),0, 1,0,0,0,null);
		//objects[4] = new Sphere(3,new Vec3( -5.5, 0, -15),new Vec3(0.90, 0.90, 0.90),0, 1,0,0,0,null);
		
		
		objects[1]= new Sphere(3,new Vec3( 0.0,     20, -30),new Vec3(0,0,0), 0.0, 0.0,0.0,0,0,new Vec3(3));
		objects[2] = TriangleMesh.generatePolySphere(5, 5);
		
		

	}

	public static void alg(Camera c) throws IOException{
		int test= 0;
    	RayAlg ra = new RayAlg();
        Ray ray = new Ray();
        Vec3 col = new Vec3(0,0,0);
        BufferedImage image = new BufferedImage(IMAGE_WIDTH, IMAGE_HIGHT, BufferedImage.TYPE_INT_RGB); 

        for(int j = 0; j< IMAGE_HIGHT;j++){
            for(int i = 0; i< IMAGE_WIDTH;i++){
            	col.setZero();
            	c.computeRay(j,i,ray);
            	RayAlg.altTrace(0,1,ray,col);
            	int rgb = rgbgen(col.x,col.y,col.z);
            	if(col.x !=2) {
            		test++;
            	}
            	
            	image.setRGB(i, j, rgb);
            	
            	


            }
        }
        File outputFile = new File("output.bmp");
        ImageIO.write(image, "bmp", outputFile);
    }
	public static int rgbgen(double r,double g,double b) {
		int x,y,z;
		x =(int) Math.min(r*255.0, 255);
		y =(int) Math.min(g*255.0, 255);
		z =(int) Math.min(b*255.0, 255);
		int rgb = x;
		rgb = (rgb << 8) + y;
		rgb = (rgb << 8) + z;
		return rgb;
		//return ((x)<<16)|((y)<<8)|(z);
	}
	
    
    public static Vec3 rayPoint(Ray ray,double t) {
    	return ray.direction.addS(t, ray.origin);
    	
    }
    /*
    public static void sortObjects() {
    	if (objects.length ==2) {
    		objects[0].
    	}
    	for(int i = 0;i<objects.length;i++) {
    		
    	}
    }*/

    



}
interface RObject{
	//public Surf surf = null;
	public int intersection(Ray ray,Isect[] hit);
	public Vec3 normal(Vec3 p);
	public boolean read();
	public raytracer.test name();
	public Surf getSurf();
	
	//might need to add a world to object transform matrix
	
	
	 
	
	
}