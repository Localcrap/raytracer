package com.magnus;

import com.magnus.raytracer.test;

public class BVHNode  implements RObject{
    //ROList list;
    RObject left;
    RObject right;
    BVH box;


    @Override
    public int intersection(Ray ray,double tmin,double tmax, Isect[] hit) {
        boolean test = !box.intersection(ray,tmin,tmax, hit);
        if(test){
            //TODO:re-enable test;
            return 0;
        }
        int hitLeft = left.intersection(ray,tmin,tmax, hit);
        int hitRight = right.intersection(ray, tmin,(hitLeft ==1) ? hit[0].t : tmax, hit);

        if(hitLeft == 1 || hitRight == 1){
            return 1;
        }
        return 0;
    }
    

    @Override
    public Vec3 normal(Vec3 p,int tri) {
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
        v.outputBox = box;
        return true;


    }
    public BVHNode(RObject srcObjects[],int start,int end, double time0, double time1){
        int objectSpan = end- start;
        //list = new ROList();

       

        RObject objects[] = new RObject[objectSpan];
        System.arraycopy(srcObjects, start, objects, 0, objectSpan);

        int axis = getRandomNumber(0,2);
        if (objectSpan == 1){
            left = right = objects[0];

        }
        else if( objectSpan == 2){
            boolean comp =false;
            comp = GFG.box_compare(objects[0],objects[1],axis);
            if(comp){
                left = objects[0];
                right = objects[1];

            }
            else{
                left = objects[1];
                right = objects[0];
            }

        }
        else{
            //GFG.quickSort(objects,0,objectSpan-1,axis);
            GFG.shitSort(objects,objectSpan-1, axis);
            int mid = objectSpan/2;
            left = new BVHNode(objects,0,mid,time0,time1);
            right = new BVHNode(objects,mid,objectSpan,time0,time1);
        }
        BVHValues boxLeft = new BVHValues(time0, time1);
        BVHValues boxRight = new BVHValues(time0,time1);
        left.boundingBox(boxLeft);
        right.boundingBox(boxRight);


        box = BVH.surrondingBox(boxLeft.outputBox, boxRight.outputBox);
        //list.listCopy(objects, objects.length);

    }

    public static int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }


    @Override
    public Vec3 getCenter() {
        // TODO Auto-generated method stub
        return null;
    }
    
}
