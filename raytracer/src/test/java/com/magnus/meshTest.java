package com.magnus;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class  meshTest 
{
    /**
     * Rigorous Test :-) 
     */
    @Test
    public void testInterection()
    {
        TriangleMesh tm =  TriangleMesh.generatePolySphere(2, 4);
        
        Vec3 origin = new Vec3(-20 ,0,0);
        Vec3 direction = new Vec3(1,0,0);
        Ray r = new Ray(origin, direction);
        Isect i[] = new Isect[2]; 
        assertTrue( tm.intersection(r, i)>0);

    }
    @Test
    public void testInterection2()
    {
        TriangleMesh tm =  TriangleMesh.generatePolySphere(2, 4);
        
        Vec3 origin = new Vec3(0 ,-20,0);
        Vec3 direction = new Vec3(0,1,0);
        Ray r = new Ray(origin, direction);
        Isect i[] = new Isect[2]; 
        assertTrue( tm.intersection(r, i)>0);

    }
    @Test
    public void testTriangleInterection()
    {
        //TriangleMesh tm =  TriangleMesh.generatePolySphere(2, 4);
        
        Vec3 origin = new Vec3(-20 ,0,0);
        Vec3 direction = new Vec3(1,0,0);
        Ray r = new Ray(origin, direction);
        //Isect i[] = new Isect[2]; 
        double[] xd = new double[3];
        assertTrue( TriangleMesh.rayTriangleIntersect(r, new Vec3(0,1,1),new Vec3(0, 1, -1), new Vec3(0,-1,0),xd));

    }
    @Test
    public void testTriangleSphere(){
        TriangleMesh tm =  TriangleMesh.generatePolySphere(2, 5);
        assertTrue(Arrays.stream(tm.trisIndex).max().getAsInt()> 20);
    }
    
    
}
