package com.magnus;
/*
public class oldCode {
    public static void trace(int level,double weight ,Ray ray, Vec3 col) {
    	RObject prim;
    	Vec3 P,N;
    	//Isect[] hit = new Isect[raytracer.ISECTMAX]; faster way, but using arraylist for testing
    	Isect[] hit = new Isect[raytracer.ISECTMAX];
    	Isect[] trueHit = new Isect[raytracer.ISECTMAX];
    	double bigt = Double.MAX_VALUE;
    	// we assume the array of objects is sorted by distance from camera 
    	boolean b = true;
    	for(int i = 0;i<raytracer.objects.length;++i) {
    		if(raytracer.objects[i].intersection(ray, i, trueHit) >=1) {
    			
    			//if the ray intersects with an object, do nessesary calculations, then shade it
    			if(trueHit[0].t< bigt) {
    				bigt = trueHit[0].t;
    				hit[0]= trueHit[0];
    				hit[1]= trueHit[1];
        			b = false;
        			assert(hit[1].t>hit[0].t && hit[0].t>0);
    			}
    			
    			


    			//return null;
    			
    		}


    	}
    	assert(hit[1].t>hit[0].t && hit[0].t>0);
    	//System.out.println("xd");
    	if(b) {
    		shadeBackground(ray,col);
    	}
    	else {
			prim = hit[0].prim;
			
			P = raytracer.rayPoint(ray,hit[0].t);
			N = prim.normal(P);
			if(ray.direction.dot(N)<0.) {
				N = N.negate();
			}
			shade(level, weight, P, N, ray.direction, hit, col);
    	}
    	
    	//return null;
    }
    
    
    
    //trace
     * 		col.setZero();
    	Ray tray = new Ray();
    	Vec3 tcol = new Vec3(0,0,0);
    	Surf surf;
    	Vec3 L;
    	
    	//for loop for all light sources '
    	
    	for( int i = 0; i<raytracer.objects.length;i++) {
    		if(raytracer.objects[i].getSurf().emission_colour != null) {
        		L = P.sub(((Sphere)raytracer.objects[i]).center);
        		double distance = P.distance(((Sphere)raytracer.objects[i]).center)-((Sphere)raytracer.objects[i]).radius;
        		L.normalize();
        		if(N.dot(L)>0 && shadow(new Ray(P,L),distance)>0) {
        			//col.setValuesV( col.add(((Sphere)raytracer.objects[i]).surf.emission_colour.mult(N.dot(L))));
        			
        			//working ver
        			col.setValuesV( col.add( hit[0].prim.getSurf().colour.mult(N.dot(L)).mult(((Sphere)raytracer.objects[i]).surf.emission_colour) ));
        		}
    		}

    	}
    	
    	
    	
    	if(level+1 < raytracer.maxlevel) {
    		tray.origin = P.copy();
    		
    		surf = hit[0].prim.getSurf();
    		
    		if(surf.kspec*weight > raytracer.minweight) {
    			tray.direction = specularDirection(I,N);
    			altTrace(level+1,surf.kspec*weight,tray,tcol);
    			col.setValuesV(tcol.addS(surf.kspec, col));
    			
    		}
    		
    		if(surf.ktran*weight> raytracer.minweight) {
    			if(TransmissionDirection(hit,I,N,tray)) {
    				altTrace(level+1,surf.ktran*weight,tray,tcol);
    				col.setValuesV( tcol.addS(surf.ktran, col));
    				
    			}
    		}
    		
    	}
    	
/*
	public double intersection2(Ray r,double distance) {
		Vec3 oc,t2,t3;
		oc = center.sub(r.origin);
		double l2oc = 0;
		l2oc = oc.dot(oc);
		
		//checking if start of ray is inside object
		if(l2oc<center2) {
			//inside of circle, we could assume object is hollow, and trace the inside;
			//but for now just report back -1
			return -1;
			//assert(false);
		}
		//TODO: add margin 0.00001 for distance to circle for float pression 
		//closest aproach
		double ca;
		ca = oc.dot(r.direction);
		if(ca<0) {
			//camera points points away from the circe, making it safe to ignore
			return -1;
					
		}
		double t2hc=0;
		t2hc = radius*radius -l2oc +ca*ca;
		
		if(t2hc<=0) {
			// sphere is not hit by ray
			return -1;
		}
		double d = ca - Math.sqrt(t2hc);
		
		
		
		return d;
	}
	
	
	public    	
    	
    	
    	
     */
