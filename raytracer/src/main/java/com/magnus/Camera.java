package com.magnus;
public class Camera {
    //posistion of the camera 
    public Vec3 origin;
    //feeld of view of the frame
    public int fov;
    // what direction the camera is pointing
    public Vec3 dir;
    public int width,height;
    public double aspectratio;
    public double angle;
    public double invWidth, invHeight;

    public Camera(Vec3 origin,int fov,Vec3 dir, int width,int height){
        this.origin= origin;
        this.fov = fov;
        this.dir = dir;
        this.width = width; 
        this.height = height;
        this.aspectratio = width/(height+0.);
        this.angle= Math.tan( this.fov * Math.PI*0.5 /180.);
        assert(this.aspectratio != 0);
        this.invHeight = 1./height;
        this.invWidth = 1./width;
    }

    public Ray computeRay(int j,int i, Ray r){
        //first we set the origin of the ray to be equal to that of this class
        r.origin = origin;
        double xx = (2 * ((i + 0.5) * invWidth) - 1) * angle * aspectratio; 
        double yy = (1 - 2 * ((j + 0.5) * invHeight)) * angle; 
        Vec3 ray = new Vec3(xx,yy,-1);
        ray.normalize();
        r.direction = ray;
        
        
        
        // 
        return r;


    }
}
