package com.magnus;

import java.util.function.BiConsumer;

import org.junit.Test;

public class datacollectionTest {
    
    @Test
    public void bvhTraversalTest(){
        long startbvh[] = new long[10];
        long stopbvh[] = new long[10];
        long startseq[] = new long[10];
        long stopseq[] = new long[10];

		long bvhtime= 0,seqTime= 0;


        int size2 = 100;
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
            startbvh[x] = System.currentTimeMillis();
            for(int i = 0; i<size2;i++){
                for(int j = 0; j < size2; j++){
                    Ray r = new Ray();
                    c.computeRay(j, i, r);
                    top.intersection(r, -100000, 100000, hit);
    
                }
            }
            stopbvh[x] = System.currentTimeMillis();
            startseq[x] = System.currentTimeMillis();
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
            stopseq[x] = System.currentTimeMillis();
        }
        for(int x = 0; x< 10 ;x++){
            bvhtime += stopbvh[x]-startbvh[x];
            seqTime += stopseq[x]-startseq[x];
        }
        bvhtime /=9;
        seqTime /=9;

        System.out.println("Bvh traversal time ="+bvhtime);
        System.out.println("Seq traversal time ="+seqTime);

        //bvhtime =  bvhtime >> 4;
            
        
    }
}
