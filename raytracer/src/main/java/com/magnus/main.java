package com.magnus;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.locks.ReentrantLock;



class raytracer{

	public enum test{
		SPHERE,TRIANGLEMESH
	}

	
    public final static int MAX_RAY_DEPTH = 10	;
    public final static int IMAGE_HIGHT = 500;
    public final static int IMAGE_WIDTH = 500;
	public static byte[][] framebuffer = new byte[IMAGE_WIDTH*3][IMAGE_HIGHT];
	public static int THREAD_COUNT = 16;
    public final static int ISECTMAX = 10;
	public final static int PIXEL_SAMPLES = 100;
	public final static double pi = 3.1415926535897932385;
	public static AltCamera c;
	public static int line = 0;
	public static ReentrantLock lineLock = new ReentrantLock();
    public Comp modelroot;
	public static int items = 100;
	public static long intersectionTime[] = new long[THREAD_COUNT];
	public static long shadeTime[] = new long[THREAD_COUNT];
	public static long rayTime[] = new long[THREAD_COUNT];
	public static long reflectionTime[] = new long[THREAD_COUNT];
	public static long refractionTime[] = new long[THREAD_COUNT];
	public static long diffuseTime[] = new long[THREAD_COUNT];
	public static long intersections[] = new long[THREAD_COUNT];
	

    
    public static int maxlevel = 10; 
    public static double minweight = 0.001;
    public static double rayeps  = 0.0000001;
    //objects should for optimization reasons be sorted after closest proximity to camera point
    public static ROList objects;
	public static BVHNode topNode;
	public static ROList lights;
	//public static TriangleMesh[] trMesh = new TriangleMesh[10];
    //public static Sphere[] lightSources = new Sphere[1];
    
    
    



    public static void main(String args[]) throws IOException, InterruptedException {
		long start,stop;
		if(args.length >0){
			THREAD_COUNT = Integer.valueOf(args[0]);
			items = Integer.valueOf(args[1]);
		}
        
        c = new AltCamera(90,new Vec3(0,0,+1), new Vec3(0,2,10),new Vec3(1,0,0));
		start = System.currentTimeMillis();
        setupObjects();
		stop =  System.currentTimeMillis();
		System.out.println("Time: "+Long.toString(stop-start)+ " for Object construction");
		for(int i = 0;i<10;i++){
			alg(c,i);
			for(int j=0;j<IMAGE_WIDTH*3;j++){
				for(int k=0;k<IMAGE_HIGHT;k++){
					framebuffer[j][k]= 0;
				}

			}
		}
		

        
    }
    
    public static void setupObjects() {
        objects = new ROList();
		lights = new ROList();
		
		int size2 = items/2;
		int st = -size2;
		double shift = Math.random();
		for(int i = st; i<size2;i=i+2){
            for(int j = st; j < size2; j= j+2){
                objects.add(new Sphere(1,new Vec3(i+shift,0, j+shift),new Vec3(1.00, 0.32, 0.36), 0, 0,0,0,0,null));

            }
        }  
		
        objects.add( new Sphere(10000,new Vec3( 0, -10005, 0),
            new Vec3(0.2, 1, 0.2), 0, 0,0.0,0,0,null));
 /* 
		//lights.add(new Sphere(3,new Vec3( 0.0,     20, 0),
        //   new Vec3(0,0,0), 0.0, 0.0,0.0,0,0,new Vec3(10)));

		//lights.add(new Sphere(3,new Vec3( 10.0,     20.0, 10.0),
        //    new Vec3(0,0,0), 0.0, 0.0,0.0,0,0,new Vec3(10)));
		//red ball
        objects.add(new Sphere(4,new Vec3( 0.0,      0, -20),new Vec3(1.00, 0.32, 0.36), 0, 0,0,0,1.0,null));
		//objects.add(new Sphere(2,new Vec3( 5, -1, -15),new Vec3(0.90, 0.76, 0.46),0, 0,0,0,0.001,null));
		objects.add( new Sphere(9,new Vec3( 9, 5, -25),new Vec3(0.65, 0.77, 0.97),0, 0,0,0,0,null));
		objects.add( new Sphere(3,new Vec3( -5.5, 0, -15),new Vec3(0.90, 0.90, 0.90),0, 1,0,0,0,null));

		*/
		//objects.add(TriangleMesh.generatePolySphere(5, 20));
		
		//objects.add( new Sphere(100,new Vec3(0,-100.5,-1),new Vec3(0.8,0.8,0),0,0,0,0,0,null));
		//objects.add( new Sphere(0.5,new Vec3(0,0,-1),new Vec3(0.1,0.2,0.5),0,0,0,0,1,null));

        
        BVHNode b = new BVHNode(objects.getList(), 0, objects.size, 0, 100);
		topNode = b;

		//sortLights(objects);

	}
	public static void sortLights(ROList obj){
		lights = new ROList();
		for(int i=0;i<obj.size();i++){
			if(obj.get(i).getSurf().emission_colour != null){
				lights.add(obj.get(i));
			}
		}
	}

	public static boolean alg(AltCamera c,int loop) throws IOException, InterruptedException{
		long start,stop;

        //BufferedImage image = new BufferedImage(IMAGE_WIDTH, IMAGE_HIGHT, BufferedImage.TYPE_INT_RGB); 



			


		
		//make threads pairwize for now
		int threads = THREAD_COUNT;
		//int widthCut,hightCut;
		start = System.currentTimeMillis();
		if(threads == 0){
			return false;
		}
		
		Thread[] rt = new Thread[threads];

		for(int i=0;i<(threads);i++){
			rt[i]= new Thread(new RTread(i));

		}
		stop = System.currentTimeMillis();
		System.out.println("Time: "+Long.toString(stop-start)+ " for Thread construction");
		start = System.currentTimeMillis();
		for(int i = 0;i<threads;i++){
			rt[i].start();
		}

		for(int i = 0;i<threads;i++){

				rt[i].join();
		}
		
		stop = System.currentTimeMillis();
		System.out.println("Time: "+Long.toString(stop-start)+ " for thread tracing");
		createFile("threadstats.csv");
		FileWriter w = createWriter("threadstats.csv");
		for(int i = 0; i< threads;i++){
			/* 
			System.out.println("thread "+ i);
			System.out.println(" intersection time= "+intersectionTime[i]);
			System.out.println(" shade time= "+shadeTime[i]);
			System.out.println(" ray time= "+rayTime[i]);
			System.out.println(" reflection time= "+reflectionTime[i]);
			System.out.println(" refraction time= "+refractionTime[i]);
			System.out.println(" diffuse time= "+diffuseTime[i]);
			*/
			writeToFile(w,i+","+intersectionTime[i]+","+shadeTime[i]+","+rayTime[i]+"," +reflectionTime[i]+","+
				refractionTime[i]+","+diffuseTime[i]+"\n");

		}
		w.close();

		try {
			File picture = new File("image"+loop+".ppm");
			if(picture.createNewFile()){

			}
			else{
				picture.delete();
				picture.createNewFile();
			}
			FileWriter pic = new FileWriter("image"+loop+".ppm");
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
		return true;
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
	public static void seqTrace(int widthStart, int widthStop, int hightStart,int hightStop,int id){
		long start, stop;
		int max = Integer.MAX_VALUE;
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
					
					start = System.currentTimeMillis();
					
					raytracer.c.computeRay((Math.random()+j)/(IMAGE_HIGHT),(Math.random()+i)/IMAGE_WIDTH,ray);
					ray.direction.normalize();
					stop = System.currentTimeMillis();
					rayTime[id] += (stop-start);
					
					RayAlg.bvhTrace(0,1,ray,col,-max,max,id);
					tempcol.add(col);
					//int rgb = rgbgen(col.x,col.y,col.z);

				}
				
				//tempcol.div(raytracer.PIXEL_SAMPLES, col);
				double r,g,b;
				
				r = tempcol.x;
				b = tempcol.y;
				g = tempcol.z;
				
				double scale = 0.2 / PIXEL_SAMPLES;
				r = Math.sqrt(scale*r);
				b = Math.sqrt(scale*b);
				g = Math.sqrt(scale*g);
				

				r =(int) Math.min(r*255.0, 255);
				b =(int) Math.min(b*255.0, 255);
				g =(int) Math.min(g*255.0, 255);
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

	public static double degrees_to_radians(double degrees) {
		return degrees * pi / 180.0;
	}


    /*
    public static void sortObjects() {
    	if (objects.length ==2) {
    		objects[0].
    	}
    	for(int i = 0;i<objects.length;i++) {
    		
    	}
    }*/

	public static void createFile(String s) throws IOException{
		File f = new File(s);
		if(f.createNewFile()){

		}
		else{
			f.delete();
			f.createNewFile();
		}
				
	}
	public static FileWriter createWriter(String s) throws IOException{
		return new FileWriter(s);
	}

	public static void writeToFile(FileWriter w,String s) throws IOException{
			w.write(s);

	}

    



}
class RTread implements Runnable{

	int id;

	public RTread(int id){
		this.id = id;


	}

	@Override
	public void run() {
		int curLine = 0;
		while(raytracer.line < raytracer.IMAGE_HIGHT){
			//raytracer.lineLock.lock();
			if(raytracer.line < raytracer.IMAGE_HIGHT){
				curLine = raytracer.line;
				raytracer.line = raytracer.line+5;
				//System.out.println(Integer.toString(curLine)+" on thread "+ Integer.toString(id));
				//raytracer.lineLock.unlock();
				raytracer.seqTrace(0, raytracer.IMAGE_WIDTH, curLine, curLine+5,id);
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