package com.magnus;

public class BVHValues{
    public BVHValues(double time0,double time1){
        this.time0 = time0;
        this.time1 = time1;
    }
    public BVHValues(double time0,double time1,BVH outputBox){
        this.time0 = time0;
        this.time1 = time1;
        this.outputBox = outputBox;
    }
    public double time0,time1;
    public BVH outputBox;

}
