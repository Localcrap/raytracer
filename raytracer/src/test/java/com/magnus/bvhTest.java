package com.magnus;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

public class bvhTest {
    @Test
    public void bvhConstructionTest(){
        ROList objects = new ROList();
        
        objects.add( new Sphere(10000,new Vec3( 0, -10004, -20),
            new Vec3(0.2, 0.2, 0.2), 0, 0,0.0,0,0,null));
        objects.add(new Sphere(3,new Vec3( 0.0,     20, 0),
            new Vec3(0,0,0), 0.0, 0.0,0.0,0,0,new Vec3(3)));

        BVHNode b = new BVHNode(objects.getList(), 0, 1, 0, 100);
        assertTrue("b Left full", b.left != null);
        assertTrue("b right full", b.right != null);

        System.out.println("no crash");
        

    }
    @Test
    public void greaterThan2BVH(){
        ROList objects = new ROList();
        
        objects.add( new Sphere(10000,new Vec3( 0, -10004, -20),
            new Vec3(0.2, 0.2, 0.2), 0, 0,0.0,0,0,null));
        objects.add(new Sphere(3,new Vec3( 0.0,     20, 0),
            new Vec3(0,0,0), 0.0, 0.0,0.0,0,0,new Vec3(3)));

        objects.add(new Sphere(4,new Vec3( 0.0,      0, -20),new Vec3(1.00, 0.32, 0.36), 0, 1,0,0,0.5,null));
		objects.add(new Sphere(2,new Vec3( 5, -1, -15),new Vec3(0.90, 0.76, 0.46),0, 0,0,0,0.001,null));
		objects.add( new Sphere(3,new Vec3( 5, 0, -25),new Vec3(0.65, 0.77, 0.97),0, 1,0,0,0,null));
		objects.add( new Sphere(3,new Vec3( -5.5, 0, -15),new Vec3(0.90, 0.90, 0.90),0, 1,0,0,0,null));

        
        BVHNode b = new BVHNode(objects.getList(), 0, objects.size, 0, 100);
        assertTrue("b Left full", b.left != null);
        assertTrue("b right full", b.right != null);


    }


}
