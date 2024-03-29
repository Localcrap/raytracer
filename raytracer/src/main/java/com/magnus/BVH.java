package com.magnus;

public class BVH {
    private Vec3 minimun,maximum;
    public BVH(Vec3 a, Vec3 b){
        minimun = a;
        maximum = b;
    }
    public boolean slowIntersection(Ray r, Double tmin, double tmax, Isect[] hit){

        for(int a=0;a<3;a++){
            double t0 = Math.min((minimun.itterate(a) -r.origin.itterate(a)) / r.direction.itterate(a),
            (maximum.itterate(a) -r.origin.itterate(a)) / r.direction.itterate(a));
            double t1 = Math.max((minimun.itterate(a) -r.origin.itterate(a)) / r.direction.itterate(a),
            (maximum.itterate(a) -r.origin.itterate(a)) / r.direction.itterate(a));

            tmin = Math.max(t0, tmin);
            tmax = Math.min(t1,tmax);
            if(tmax <= tmin){
                return false;
            }
        }
        return true;
    }
    
    //AABB intersection test based on Andrew Kenslers approach
    public boolean intersection(Ray r,Double tmin,double tmax, Isect[] hit ){

        //return slowIntersection(r, tmin, tmax, hit);
         
        //double min,max; 

        for(int a=0;a<3;a++){
            double invD = 1.0f / r.direction.itterate(a);
            double t0 = ((minimun.itterate(a) - r.origin.itterate(a))*invD);
            double t1 = ((maximum.itterate(a)- (r.origin.itterate(a)))* invD);
            if(invD < 0.0f){
                double temp = t0;
                t0 =t1;
                t1 = temp;

            }
            tmin = t0>tmin ? t0 : tmin;
            tmax = t1 < tmax ? t1 : tmax;
            if(tmax <= tmin){
                return false;

            }
        }
        return true;
        
    }

    public Vec3 min(){return minimun;}
    public Vec3 max(){return maximum;}


    public static BVH surrondingBox(BVH box0, BVH box1){
        Vec3 small = new Vec3(Math.min(box0.min().x, box1.min().x),
                              Math.min(box0.min().y, box1.min().y),
                              Math.min(box0.min().z,box1.min().z));
        Vec3 big = new Vec3(Math.max(box0.max().x, box1.max().x),
                            Math.max(box0.max().y, box1.max().y),
                            Math.max(box0.max().z,box1.max().z));

        return new BVH(small,big);

    }

    
    
}


