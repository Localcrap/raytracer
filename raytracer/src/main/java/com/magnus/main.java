package com.magnus;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.lang.*;

import javax.imageio.ImageIO;



class raytracer{

	public enum test{
		SPHERE,TRIANGLEMESH
	}

	
    public final static int MAX_RAY_DEPTH = 10;
    public final static int IMAGE_HIGHT = 250;
    public final static int IMAGE_WIDTH = 250;
	public static byte[][] framebuffer = new byte[IMAGE_WIDTH*3][IMAGE_HIGHT];
	public final static int THREAD_COUNT = 0;
    public final static int ISECTMAX = 100;
	public final static int PIXEL_SAMPLES = 1000;
	public static Camera c;
	public static int line = 0;
	public static ReentrantLock lineLock = new ReentrantLock();
    public Comp modelroot;
    
    public static int maxlevel = 10; 
    public static double minweight = 0.001;
    public static double rayeps  = 0.0000001;
    //objects should for optimization reasons be sorted after closest proximity to camera point
    public static ROList objects;
	public static BVHNode topNode;
	public static ROList lights;
	//public static TriangleMesh[] trMesh = new TriangleMesh[10];
    //public static Sphere[] lightSources = new Sphere[1];
    
    
    



    public static void main(String args[]) {
		long start,stop;
        //Matrix m = new Matrix(IMAGE_HIGHT, IMAGE_WIDTH);
        //Camera c;
        Vec3 pos = new Vec3(0,0,0), dir = new Vec3(0,0,0);
        
        c = new AltCamera(pos,60,dir,IMAGE_WIDTH,IMAGE_HIGHT);
		start = System.currentTimeMillis();
        setupObjects();
		stop =  System.currentTimeMillis();
		System.out.println("Time: "+Long.toString(stop-start)+ " for Object construction");
        try {
			alg(c);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
    }
    
    private static void setupObjects() {
        objects = new ROList();
		
        
        objects.add( new Sphere(10000,new Vec3( 0, -10004, -20),
            new Vec3(0.2, 0.2, 0.2), 0, 0,0.0,0,0,null));

		objects.add(new Sphere(3,new Vec3( 0.0,     20, 0),
            new Vec3(0,0,0), 0.0, 0.0,0.0,0,0,new Vec3(3)));

		objects.add(new Sphere(3,new Vec3( 10.0,     20.0, 10.0),
            new Vec3(0,0,0), 0.0, 0.0,0.0,0,0,new Vec3(3)));
		//red ball
        objects.add(new Sphere(4,new Vec3( 0.0,      0, -20),new Vec3(1.00, 0.32, 0.36), 0, 0,0,0,1.,null));
		objects.add(new Sphere(2,new Vec3( 5, -1, -15),new Vec3(0.90, 0.76, 0.46),0, 0,0,0,0.001,null));
		objects.add( new Sphere(3,new Vec3( 5, 0, -25),new Vec3(0.65, 0.77, 0.97),0, 1,0,0,0,null));
		objects.add( new Sphere(3,new Vec3( -5.5, 0, -15),new Vec3(0.90, 0.90, 0.90),0, 1,0,0,0,null));

		//objects.add(TriangleMesh.generatePolySphere(5, 20));

        
        BVHNode b = new BVHNode(objects.getList(), 0, objects.size, 0, 100);
		topNode = b;

		sortLights(objects);

	}
	public static void sortLights(ROList obj){
		lights = new ROList();
		for(int i=0;i<obj.size();i++){
			if(obj.get(i).getSurf().emission_colour != null){
				lights.add(obj.get(i));
			}
		}
	}

	public static void alg(Camera c) throws IOException{
		long start,stop;

        //BufferedImage image = new BufferedImage(IMAGE_WIDTH, IMAGE_HIGHT, BufferedImage.TYPE_INT_RGB); 



			
		if(THREAD_COUNT == 0){
			Thread t= new Thread(new RTread(0,0,0,0,0));
			t.start();
			try {
				t.join();

			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			/* 
			start = System.currentTimeMillis();
			int curLine = 0;
			while(raytracer.line < raytracer.IMAGE_HIGHT){
				//raytracer.lineLock.lock();
				if(raytracer.line < raytracer.IMAGE_HIGHT){
					curLine = raytracer.line;
					raytracer.line = raytracer.line+1;
					System.out.println(Integer.toString(curLine));
					//raytracer.lineLock.unlock();
					raytracer.seqTrace(0, raytracer.IMAGE_WIDTH, curLine, curLine+1);
				}
			}
			stop = System.currentTimeMillis();
			System.out.println("Time: "+Long.toString(stop-start)+ " for thread tracing");
			*/
		}
		else{

		
		//make threads pairwize for now
		int threads = THREAD_COUNT;
		if(THREAD_COUNT % 2 == 1){
			threads =threads+1;
		}
		int widthCut,hightCut;
		start = System.currentTimeMillis();
		if(threads == 0){
			return ;
		}
		
		widthCut = IMAGE_WIDTH/threads*2;
		hightCut = IMAGE_HIGHT/2;
		Thread[] rt = new Thread[threads];

		for(int i=0;i<(threads/2)-1;i++){
			rt[i*2]= new Thread(new RTread(i*widthCut,(i+1)*widthCut,0,hightCut,i*2));
			rt[i*2+1]= new Thread( new RTread(i*widthCut,(i+1)*widthCut,hightCut,IMAGE_HIGHT,i*2+1));

		}
		rt[threads-2]= new Thread( new RTread((threads/2-1)*widthCut,IMAGE_WIDTH,0,hightCut,threads-2));
		rt[threads-1]= new Thread( new RTread((threads/2-1)*widthCut,IMAGE_WIDTH,hightCut,IMAGE_HIGHT,threads-1));
		stop = System.currentTimeMillis();
		System.out.println("Time: "+Long.toString(stop-start)+ " for Thread construction");
		start = System.currentTimeMillis();
		for(int i = 0;i<threads;i++){
			rt[i].start();
		}

		for(int i = 0;i<threads;i++){
			try {
				rt[i].join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			};
		}
		
		stop = System.currentTimeMillis();
		System.out.println("Time: "+Long.toString(stop-start)+ " for thread tracing");

		}
		try {
			File picture = new File("image.ppm");
			if(picture.createNewFile()){

			}
			else{
				picture.delete();
				picture.createNewFile();
			}
			FileWriter pic = new FileWriter("image.ppm");
			pic.write("P3\n"+Integer.toString(IMAGE_WIDTH)+" "+Integer.toString(IMAGE_HIGHT)+"\n255\n");
					
			start = System.currentTimeMillis();
			
		//String s= "";
        for(int j = 0; j< IMAGE_HIGHT;j++){
			
            for(int i = 0; i< IMAGE_WIDTH*3;i++){
				pic.write(Integer.toString(Byte.toUnsignedInt(framebuffer[i][j]))+" ");
				
            }
			//s = s+"\n";
			pic.write("\n");
			
        }
		stop = System.currentTimeMillis();
		System.out.println("Time: "+Long.toString(stop-start)+ " for creating string");
		start = System.currentTimeMillis();
		//pic.write(s);
		stop = System.currentTimeMillis();
		System.out.println("Time: "+Long.toString(stop-start)+ " for writing to file");

		pic.close();
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
        //File outputFile = new File("output.bmp");
        //ImageIO.write(image, "bmp", outputFile);
    }
	/* 
	public static int rgbgen(double r,double g,double b) {
		int x,y,z;
		x =(int) Math.min(r*255.0, 255);
		y =(int) Math.min(g*255.0, 255);
		z =(int) Math.min(b*255.0, 255);
		
		//return ((x)<<16)|((y)<<8)|(z);
	}
	*/
	public static void seqTrace(int widthStart, int widthStop, int hightStart,int hightStop){
		//long start, stop;
		//start = System.currentTimeMillis();
		//int test= 0;
    	//RayAlg ra = new RayAlg();
        Ray ray = new Ray();
        Vec3 col = new Vec3(0,0,0);
		//System.out.println("iterations= " + Integer.toString((hightStop-hightStart)*(widthStop-widthStart)));
        for(int j = hightStart; j< hightStop;j++){
            for(int i = widthStart; i< widthStop;i++){
				
				Vec3 tempcol = new Vec3(0);
				for(int s =0; s<raytracer.PIXEL_SAMPLES;s++){
					col.setZero();
					raytracer.c.computeRay(Double.valueOf(j) +Math.random(),Double.valueOf(i)+Math.random(),ray);
					RayAlg.bvhTrace(0,1,ray,col,-1000000,10000000);
					tempcol.add(col);
					//int rgb = rgbgen(col.x,col.y,col.z);

				}
				tempcol.div(raytracer.PIXEL_SAMPLES, col);
				int r,g,b;
				r =(int) Math.min(col.x*255.0, 255);
				g =(int) Math.min(col.y*255.0, 255);
				b =(int) Math.min(col.z*255.0, 255);
				raytracer.framebuffer[i*3][j] = (byte)r;
				raytracer.framebuffer[i*3+1][j] = (byte)b;
				raytracer.framebuffer[i*3+2][j] = (byte)g;

            }
        }
		//stop = System.currentTimeMillis();
		//System.out.println("Time: "+Long.toString(stop-start)+ " for induvidual thread tracing");

	}
    
    public static Vec3 rayPoint(Ray ray,double t) {
		Vec3 temp = new Vec3(0);
    	return ray.direction.addS(t, ray.origin,temp);
    	
    }

    public static double getRandomNumber(double min, double max) {
        return ((Math.random() * (max - min)) + min);
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
class RTread implements Runnable{

	int id;

	int widthStart,widthStop;
	int hightStart,hightStop;

	public RTread(int wstt,int wstp, int hstt,int hstp,int id){
		this.id = id;
		widthStart = wstt;
		widthStop = wstp;
		hightStart = hstt;
		hightStop  =hstp;

	}

	@Override
	public void run() {
		int curLine = 0;
		while(raytracer.line < raytracer.IMAGE_HIGHT){
			//raytracer.lineLock.lock();
			if(raytracer.line < raytracer.IMAGE_HIGHT){
				curLine = raytracer.line;
				raytracer.line = raytracer.line+1;
				System.out.println(Integer.toString(curLine)+" on thread "+ Integer.toString(id));
				//raytracer.lineLock.unlock();
				raytracer.seqTrace(0, raytracer.IMAGE_WIDTH, curLine, curLine+1);
			}
		}
			
		
		
	}
	
	
	
}

interface RObject{
	//public Surf surf = null;
	public int intersection(Ray ray,double tmin, double tmax ,Isect[] hit);
	public Vec3 normal(Vec3 p,int triIndex);
	public boolean read();
	public raytracer.test name();
	public Surf getSurf();
	public boolean boundingBox(BVHValues v);
	public Vec3 getCenter();

	
	//might need to add a world to object transform matrix
	
	
	 
	
	
}