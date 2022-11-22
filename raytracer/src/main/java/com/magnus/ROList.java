package com.magnus;

import java.util.ArrayList;

import com.magnus.raytracer.test;

public class ROList implements RObject {
    RObject objects[];



    @Override
    public int intersection(Ray ray, Isect[] hit) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Vec3 normal(Vec3 p) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean read() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public test name() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Surf getSurf() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean boundingBox(BVHValues v) {
        BVH tempBox = null;
        boolean firstBox = true;
        for(RObject object : objects){
            BVHValues v2 =  new BVHValues(v.time0, v.time1, tempBox);
            if(!object.boundingBox(v2)){ return false;}
            //v2.outputBox is tempbox
            v.outputBox = firstBox ? v2.outputBox : BVH.surrondingBox(v.outputBox, v2.outputBox);
            firstBox = false;
        }
        

        return true;
    }
    
}
