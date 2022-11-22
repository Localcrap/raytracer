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
    public boolean boundingBox(double time0, double time1, BVH outputBox) {
        BVH tempBox = null;
        boolean firstBox = true;
        for(RObject object : objects){
            if(!object.boundingBox(time0, time1, tempBox)){ return false;}
            outputBox = firstBox ? tempBox : BVH.surrondingBox(outputBox, tempBox);
            firstBox = false;
        }
        

        return true;
    }
    
}
