package com.magnus;


import com.magnus.raytracer.test;

public class ROList implements RObject {
    private RObject objects[];
    int size,maxSize;

    public ROList(int i){
        objects = new RObject[i];
        size = 0;
        maxSize = i;

    }
    public ROList(){
        objects = new RObject[5];
        size = 0;
        maxSize = 5;
        
    }

    public void add(RObject i){
        if(size>=maxSize){
            RObject newObjects[] = new RObject[maxSize*2];
            System.arraycopy(objects, 0, newObjects, 0, maxSize);
            maxSize=maxSize*2;
            objects = newObjects;

        }
        objects[size]= i;
        size = size+1;

        
    }
    public void listCopy(RObject i[],int size){
        objects = i;
        this.size = size;
        maxSize = size;
    }
    public RObject[] getList(){
        return objects;
    }
    public int size(){
        return size;
    }

    public RObject get(int i ){
        if(i>-1 && i<size){
            return objects[i];
        }
        throw new ArrayIndexOutOfBoundsException(); 

    }



    @Override
    public int intersection(Ray ray,double tmin,double tmax, Isect[] hit) {
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
