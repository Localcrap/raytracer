package com.magnus;

import javax.lang.model.util.Elements.Origin;

public class AltCamera extends Camera {

    public AltCamera(Vec3 origin, int fov, Vec3 dir, int width, int height) {
        super(origin, fov, dir, width, height);
        Vec3 temp = new Vec3(0);
        origin.sub( horizontal.div(2,temp),lower_left_corner);
        lower_left_corner.sub(vertical.div(2,temp));
        lower_left_corner.sub(new Vec3(0, 0, focal_length));
        
        //TODO Auto-generated constructor stub
    }


    static final double aspect_ratio = 1;
    // Camera

    double viewport_height = 2.0;
    double viewport_width = aspect_ratio * viewport_height;
    double focal_length = 1.5;

    Vec3 origin = new Vec3(0, 0, 0);
    Vec3 horizontal = new Vec3(viewport_width, 0, 0);
    Vec3 vertical = new Vec3(0, -viewport_height, 0);
    Vec3 lower_left_corner = new Vec3(0);
    

    @Override
    public Ray computeRay(double j,double i, Ray r){
        //first we set the origin of the ray to be equal to that of this class
        r.origin = origin;
        double xx = (double)i / (raytracer.IMAGE_WIDTH-1);
        double yy = (double)j / (raytracer.IMAGE_HIGHT-1); 
        Vec3 temp = new Vec3(0);
        Vec3 ray = new Vec3(0);
        lower_left_corner.add(horizontal.mult(xx,temp),ray);
        ray.add(vertical.mult(yy,temp));
        ray.sub(origin);
        ray.normalize();
        r.direction = ray;
        
        
        
        // 
        return r;


    }
}
