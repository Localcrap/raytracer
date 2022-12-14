package com.magnus;

import java.io.IOException;
import java.util.function.BiConsumer;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileWriter;

import org.junit.Test;

import junit.framework.Assert;

public class datacollectionTest {
    
    @Test
    public void bvhTraversalTest() throws IOException{
        int runs[] = {1,5,25,125};
        long startbvh[][] = new long[10][runs.length];
        long stopbvh[][] = new long[10][runs.length];
        long startseq[][] = new long[10][runs.length];
        long stopseq[][] = new long[10][runs.length];

		long bvhtime= 0,seqTime= 0;
        int rv = 0;
        createFile("BVHTraversalTime.csv");
        FileWriter w = createWriter("BVHTraversalTime.csv");
        for (int size2 : runs){
            int st = -size2;
            ROList objects = new ROList(size2*2);


            System.out.println("creating objects");
            for(int i = st; i<size2;i++){
                for(int j = st; j < size2; j++){
                    objects.add(new Sphere(1,new Vec3(i,0, j),new Vec3(1.00, 0.32, 0.36), 0, 0,0,0,0,null));

                }
            }   
            System.out.println("making bvh");     
            BVHNode top = new BVHNode(objects.getList(), 0, objects.size(), -1000000, 1000000);
            AltCamera c = new AltCamera(90,new Vec3(0,0,+1), new Vec3(0,0,10),new Vec3(1,0,0));
            Isect hit[] = new Isect[2];
            System.out.println("running intersections");
            for(int x = 0; x<10;x++){
                startbvh[x][rv] = System.currentTimeMillis();
                for(int i = 0; i<size2;i++){
                    for(int j = 0; j < size2; j++){
                        Ray r = new Ray();
                        c.computeRay(j, i, r);
                        top.intersection(r, -100000, 100000, hit);
        
                    }
                }
                stopbvh[x][rv] = System.currentTimeMillis();
                startseq[x][rv] = System.currentTimeMillis();
                for(int i = 0; i<size2;i++){
                    for(int j = 0; j < size2; j++){
                        Ray r = new Ray();
                        c.computeRay(j, i, r);
                        for(int k = 0; k<objects.size();k++){
                            objects.get(k).
                                intersection(r, -100000, 100000, hit);
                        }
                    
        
                    }
                }
                stopseq[x][rv] = System.currentTimeMillis();
            }
            rv++;

        }
        

        for(int j = 0; j< runs.length ;j++){
            for( int i = 1; i<5;i++){
                bvhtime += stopbvh[i][j]-startbvh[i][j];
                seqTime += stopseq[i][j]-startseq[i][j];
            }
            bvhtime /=4;
            seqTime /=4;
            writeToFile(w,runs[j]*2+","+bvhtime+","+seqTime+"\n");
            bvhtime = 0;
            seqTime = 0;

            

            
        }
        w.close();

        
        System.out.println("Bvh traversal time ="+bvhtime);
        System.out.println("Seq traversal time ="+seqTime);

        //bvhtime =  bvhtime >> 4;
            
        
    }
    @Test
    public void bvhtimeTest() throws IOException{
        int runs[] = {1,5,25,125};
        
        long BVHintersec=0,seqintersec=0;
        long runvseq[] = new long[runs.length];
        long runvBVH[] = new long[runs.length];

        int rv =0;
        for(int size2 : runs){
            int st = -size2;
            ROList objects = new ROList(size2*2);


            System.out.println("creating objects");
            for(int i = st; i<size2;i++){
                for(int j = st; j < size2; j++){
                    objects.add(new Sphere(1,new Vec3(i,0, j),new Vec3(1.00, 0.32, 0.36), 0, 0,0,0,0,null));

                }
            }   
            System.out.println("making bvh");     
            BVHCollector top = new BVHCollector(objects.getList(), 0, objects.size(), -1000000, 1000000);
            AltCamera c = new AltCamera(90,new Vec3(0,0,+1), new Vec3(0,0,10),new Vec3(1,0,0));

            Isect hit[] = new Isect[2];
            System.out.println("running intersections");

            for(int i = 0; i<size2;i++){
                for(int j = 0; j < size2; j++){
                    Ray r = new Ray();
                    c.computeRay(j, i, r);
                    BVHintersec += BVHCollector.makePositive(top.intersection(r, -100000, 100000, hit));

                }
            }
            for(int i = 0; i<size2;i++){
                for(int j = 0; j < size2; j++){
                    Ray r = new Ray();
                    c.computeRay(j, i, r);
                    for(int k = 0; k<objects.size();k++){
                        objects.get(k).
                            intersection(r, -100000, 100000, hit);
                        seqintersec++;
                    }
                    

                }
            }
            runvseq[rv] = seqintersec;
            runvBVH[rv] = BVHintersec;
            rv++;
        }
        createFile("BVHTraversalnumber.csv");
        FileWriter w = createWriter("BVHTraversalnumber.csv");
        writeToFile(w, ",Sequential,BVH\n");
        for(int i = 0; i<runs.length;i++){
            writeToFile(w,runs[i]+","+runvseq[i]+","+runvBVH[i]+"\n");
        }
        w.close();

        
        System.out.println("Sequential intersection tests "+ seqintersec);
        System.out.println("BVH intersection tests "+ BVHintersec);
        
    }
    @Test
    public void paralleldataTest() throws IOException, InterruptedException{
        long start,stop;
        int cores[] = {1,4,8,16};
        int items[] = {5,10,20,50};
        int runs = 4;
        int margin = 1;
        long results[][][] =new long[runs][items.length][cores.length];
        String args[] = new String[2];

        for(int i = 0;i<runs;i++){
            for(int j=0; j<items.length;j++){
                //args[1]= Integer.toString(items[j]);
                raytracer.items = items[j];
                int t = 0;
                for(int c: cores){
                    //args[0] = Integer.toString(c);
                    raytracer.THREAD_COUNT = c;
                    AltCamera cam = new AltCamera(90,new Vec3(0,0,+1), new Vec3(0,2,10),new Vec3(1,0,0));
                    raytracer.c =cam;
                    raytracer.setupObjects();
                    raytracer.line = 0;
                    start = System.currentTimeMillis();
                    raytracer.alg(cam,0);

                    stop = System.currentTimeMillis();
                    results[i][j][t] = stop-start;
                    t++;

                }
            }
            
        }
        createFile("sceneRuntime.csv");
        FileWriter w = createWriter("sceneRuntime.csv");
        for(int h = 0;h<items.length;h++){
            writeToFile(w, items[h]*items[h]+"");
            for(int i =0;i<cores.length;i++){
                writeToFile(w, ",");

                long value= 0;
                for(int j=margin;j<runs;j++){
                     value = results[j][h][i];
                }
                value/=(runs-margin);
                writeToFile(w, ""+value);
    
    
            }
            writeToFile(w, "\n");


        }
        w.close();

        


        

        
    }
    public void createFile(String s) throws IOException{
        File f = new File(s);
        if(f.createNewFile()){

        }
        else{
            f.delete();
            f.createNewFile();
        }
                
    }
    public FileWriter createWriter(String s) throws IOException{
        return new FileWriter(s);
    }

    public void writeToFile(FileWriter w,String s) throws IOException{
			w.write(s);

    }

}
