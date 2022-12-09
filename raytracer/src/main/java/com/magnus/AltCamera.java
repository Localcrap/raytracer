package com.magnus;

import javax.lang.model.util.Elements.Origin;

public class AltCamera extends Camera {

    public AltCamera(Vec3 origin, int fov, Vec3 dir, int width, int height,Vec3 lookAt, Vec3 LookFrom, Vec3 vup) {
        super(origin, fov, dir, width, height);
        Vec3 temp = new Vec3(0);
        theta = raytracer.degrees_to_radians(fov);
        h = Math.tan(theta/2);
        viewport_height = 2.0*h;
        viewport_width = aspect_ratio*viewport_height;
        w = LookFrom.sub(lookAt,temp).unitVector();
        u = vup.cross(w, temp).unitVector();
        v = w.cross(vup, temp).copy();
        origin = LookFrom;
        u.mult(viewport_width, horizontal);
        v.mult(viewport_height,vertical);
        vertical.negate();
        origin.sub(horizontal.div(2, temp),lower_left_corner)
            .sub(vertical.div(2,temp),lower_left_corner)
            .sub(w);


        
        //TODO Auto-generated constructor stub
    }


    static final double aspect_ratio = 2/1;
    // Camera

    double viewport_height;// =1.0;
    double viewport_width;//= aspect_ratio * viewport_height;
    double focal_length = 1;
    double vfov, theta,h;
    Vec3 w,u,v;





    Vec3 origin =new Vec3();; //= new Vec3(0, 0, 0);
    Vec3 horizontal =new Vec3();;//= new Vec3(viewport_width, 0, 0);
    Vec3 vertical =new Vec3();//= new Vec3(0, -viewport_height, 0);
    Vec3 lower_left_corner = new Vec3(0);
    

    @Override
    public Ray computeRay(double j,double i, Ray r){
        //first we set the origin of the ray to be equal to that of this class
        r.origin = origin;
        //double xx = (double)i / (raytracer.IMAGE_WIDTH-1);
        //double yy = (double)j / (raytracer.IMAGE_HIGHT-1); 
        Vec3 temp = new Vec3(0);
        Vec3 ray = new Vec3(0);
        lower_left_corner.add(horizontal.mult(j,temp),ray).add(vertical.mult(i,temp),ray).sub(w);
       // ray.normalize();
        r.direction = ray;
        
        
        
        // 
        return r;


    }
}
