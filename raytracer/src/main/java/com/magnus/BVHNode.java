package com.magnus;

import com.magnus.raytracer.test;

public class BVHNode  implements RObject{
    RObject left;
    RObject right;
    BVH box;


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
        //TODO:fix return;
        v.outputBox = box;
        return true;
        

    }
    
}
