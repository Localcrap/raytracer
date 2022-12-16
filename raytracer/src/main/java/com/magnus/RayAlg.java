package com.magnus;
import java.util.ArrayList;

public class RayAlg {

	public static void bvhTrace(int level,double weight ,Ray ray, Vec3 col,double tmin,double tmax,int id) {
		long start,stop;
		if(level >= raytracer.maxlevel){
			return;
		}
    	double tnear = Double.MAX_VALUE;
    	RObject robject = null;
		TriangleMesh tm;
    	Isect[] hit = new Isect[2];
		Isect xd =null;
		start = System.currentTimeMillis();
		raytracer.topNode.intersection(ray, tmin, tmax, hit);
		stop = System.currentTimeMillis();
		raytracer.intersectionTime[id] = raytracer.intersectionTime[id] + (stop-start);
		if(hit[0]!= null){
			robject = hit[0].prim;
			tnear = hit[0].t;
			
		}
		
    	if (robject == null) {
    		shadeBackground(ray,col);
    		return;
    	}
		start = System.currentTimeMillis();
    	shade(level,weight,ray,tnear,tmin,tmax,robject,hit,xd,col,id);
    	stop = System.currentTimeMillis();
		raytracer.shadeTime[id] += (stop-start);
    	
    	//test stuff
    	Vec3 I  = ray.direction;
    	
    	Surf surf;
    	Ray tray = new Ray();
		Vec3 P = raytracer.rayPoint(ray,hit[0].t);
		Vec3 N = robject.normal(P,0);
		Vec3 tcol = new Vec3(0,0,0);
    	
    	if(level+1 < raytracer.maxlevel) {
    		tray.origin = P.copy();
    		
    		surf = hit[0].prim.getSurf();
    		/*
    		if(surf.kspec*weight > raytracer.minweight) {
    			tray.direction = specularDirection(I,N);
    			trace(level+1,surf.kspec*weight,tray,tcol);
    			col.setValuesV(tcol.addS(surf.kspec, col));
    			
    		}
    		
    		if(surf.ktran*weight> raytracer.minweight) {
    			if(TransmissionDirection(hit,I,N,tray)) {
    				altTrace(level+1,surf.ktran*weight,tray,tcol);
    				col.setValuesV( tcol.addS(surf.ktran, col));
    				
    			}
    		}
    		*/
    		
    	}
    	
    	
    	
    	
    	
    	
    }

    public static void altTrace(int level,double weight ,Ray ray, Vec3 col,double tmin,double tmax,int id) {
    	double tnear = Double.MAX_VALUE;
    	RObject robject = null;
		TriangleMesh tm;
    	Isect[] hit = new Isect[2];
		Isect xd =null;
    	//TODO: fix repeated code
    	for(int i = 0; i<raytracer.objects.size();i++) {
			switch(raytracer.objects.get(i).name()){
				case SPHERE:
					if(raytracer.objects.get(i).intersection(ray,tmin,tmax, hit)>0) {
						//if(hit[0].t<0){hit[0] = hit[1];}
						if(hit[0].t < tnear) {
							xd = hit[0];
							tnear = hit[0].t;
							robject = (Sphere)raytracer.objects.get(i);
						}
					}
					break;


				case TRIANGLEMESH:
					if(raytracer.objects.get(i).intersection(ray,tmin,tmax, hit)>0) {
						//if(hit[0].t<0){hit[0] = hit[1];}
						if(hit[0].t < tnear) {
							xd = hit[0];
							tnear = hit[0].t;
							robject = (TriangleMesh)raytracer.objects.get(i);
						}
					}
					break;
			}
    		
    	}

    	if (robject == null) {
    		shadeBackground(ray,col);
    		return;
    	}
    	shade(level,weight,ray,tnear,tmin,tmax,robject,hit,xd,col,id);
    	
    	
    	//test stuff
		/* 
    	Vec3 I  = ray.direction;
    	
    	Surf surf;
    	Ray tray = new Ray();
		Vec3 P = raytracer.rayPoint(ray,hit[0].t);
		Vec3 N = robject.normal(P,0);
		Vec3 tcol = new Vec3(0,0,0);
    	
    	if(level+1 < raytracer.maxlevel) {
    		tray.origin = P.copy();
    		
    		surf = hit[0].prim.getSurf();
    		/*
    		if(surf.kspec*weight > raytracer.minweight) {
    			tray.direction = specularDirection(I,N);
    			trace(level+1,surf.kspec*weight,tray,tcol);
    			col.setValuesV(tcol.addS(surf.kspec, col));
    			
    		}
    		
    		if(surf.ktran*weight> raytracer.minweight) {
    			if(TransmissionDirection(hit,I,N,tray)) {
    				altTrace(level+1,surf.ktran*weight,tray,tcol);
    				col.setValuesV( tcol.addS(surf.ktran, col));
    				
    			}
    		}
    		*/
    		
    	//}
		
    	
    	
    	
    	
    	
    	
    }
    private static double mix(double a, double b, double mix) {
    	return b * mix + a* (1-mix);
    }
    
    private static void shadeBackground(Ray ray, Vec3 col) {
		col.setValues(5, 5,5 );
		
	}
	public static void shade(int level,double weight,Ray ray,double tnear,double tmin,double tmax,RObject robject ,Isect[]  hit,Isect xd,Vec3 col,int id ) {
		long start,stop;
		col.setZero();
		
		Vec3 temp = new Vec3(0);
		Vec3 temp2 = new Vec3(0);
		//Vec3 temp3 = new Vec3(0);
		Vec3 uv = ray.direction.unitVector();
    	Vec3 phit = new Vec3(0);
		Vec3 P = raytracer.rayPoint(ray,hit[0].t);
		Vec3 N = robject.normal(P,hit[0].indexTriangle);
		ray.origin.add(ray.direction.mult(tnear,temp),phit);
		Vec3 nhit = null;
		nhit= robject.normal(phit,hit[0].indexTriangle);
    	
    	//nhit.normalize();
		Surf surf =  robject.getSurf();
    	
    	
    	double bias =  0.0001;
    	boolean inside = false;
    	if (ray.direction.dot(nhit)>0) {
    		nhit.negate();
    		inside = true;
    		
    		
    	}
    	//if not defuse
		
    	if( ((surf.ktlucence> 0) || (surf.kspec> 0)) &&  level < raytracer.maxlevel) {
			
		//if(surf.kspec> 0) {
    		Vec3 reflection = new Vec3(0);
    		Vec3 refraction = new Vec3(0);
    			
    		
    		
    		double facingratio = -ray.direction.dot(nhit);
    		double fresneleffect = mix(Math.pow(1-facingratio, 3),1,0.1);
    		
    		//do reflection
    		if(surf.kspec> 0) {
				start = System.currentTimeMillis();
				
				
				Vec3  refldir = new Vec3(0);
        		//ray.direction.sub(nhit.mult(2,temp).mult(ray.direction.dot(nhit),temp2),refldir);


				ray.direction.sub(N.mult(2*ray.direction.dot(N),temp), refldir);
				refldir.add(Vec3.randomInUnitSphere().mult(robject.getSurf().fuzz,temp));

        		
        		refldir.normalize();
        		 //colour;

        		bvhTrace(level+1,weight,new Ray(phit.add(nhit.mult(bias,temp2),temp),refldir),reflection,tmin,tmax,id);
				stop = System.currentTimeMillis();
				raytracer.reflectionTime[id] += (stop-start);
    		}

    		
    		
    		//do refraction
			
    		else if(surf.ktlucence>0) {
				start = System.currentTimeMillis();
				
    			//double ior = 1.1, eta = (inside) ? ior : 1 / ior; // are we inside or outside the surface? 

    			double cosTheta = Math.min(N.dot(uv.negate(temp)),1); 
				Vec3 rOutPerp = new Vec3();
				double refractionRatio = (hit[0].enter ==1) ? surf.ktlucence : (1/surf.ktlucence); 
				uv.add(N.mult(cosTheta, temp), rOutPerp).mult(refractionRatio);
				Vec3 rOutParalell = new Vec3();
				N.mult(Math.sqrt(Math.abs(1.0-rOutPerp.length2())), rOutParalell);
				rOutParalell.add(rOutPerp);
				Ray refRay = new Ray(P,rOutParalell);
				bvhTrace(level+1,weight,refRay,refraction,tmin,tmax,id);

				
				
				//refraction.mult(refractionRatio);


				/*
    			double  k = 1 - eta * eta * (1 - cosi * cosi); 
    			Vec3 refrdir = new Vec3(0);
				ray.direction.mult(eta,refrdir).add(nhit.mult(eta*cosi-Math.sqrt(k),temp),refrdir);
    			refrdir.normalize();
    			bvhTrace(level+1,weight,new Ray(phit.sub(nhit.mult(bias,temp),temp2),refrdir),refraction,tmin,tmax,id);
				stop = System.currentTimeMillis();
				raytracer.refractionTime[id] += stop-start;

				 */

    			
    			
    		}
			
    		
			refraction.mult(1-fresneleffect);
			//TODO:fresnel breaks lamberian for some reason
    		reflection.mult(fresneleffect,temp);
    		temp.add(refraction);
    		temp.mult(surf.colour);
    		col.setValuesV(temp);
    		
    		
    	}
    	else {
			
			start = System.currentTimeMillis();
			lambertianReflectance(level,weight,tmin,tmax,ray,hit,robject,col,id);
			/* 
			

			//
			
    		for(int i = 0;i< raytracer.objects.size;i++) {
    			if(raytracer.objects.get(i) != robject && raytracer.objects.get(i).getSurf().emission_colour != null) {
    				Vec3 transmission = new Vec3(1);
    				Vec3 lightDirection = new Vec3(0);
					(raytracer.objects.get(i)).getCenter().sub(phit,lightDirection);
    				lightDirection.normalize();
    				for(int j = 0;j<raytracer.objects.size();j++) {
    					if(raytracer.objects.get(j) != raytracer.objects.get(i)) {
    						if(raytracer.objects.get(j).intersection(new Ray(phit.add(nhit.mult(bias,temp2),temp).add(N,temp).add(Vec3.randomUnitVector(),temp),lightDirection),tmin,tmax, hit)>0) {
    							transmission.setZero();;
    							break;
    						}
    					}
    				}
    				col.add(surf.colour.mult(transmission,temp2).mult
    						(Math.max(0.,nhit.dot(lightDirection)),temp).mult(raytracer.objects.get(i).getSurf().emission_colour,temp3 ) );
    				
    			}
    		}
			*/
			stop = System.currentTimeMillis();
			raytracer.diffuseTime[id]+= stop-start;
			
			
    	}
    	//if(surf.emission_colour != null) {
    	//	col.add(surf.emission_colour);
    	//}
		/*
		switch (robject.name()) {
			case TRIANGLEMESH:
				col.setValuesV(new Vec3(0.2,0.2,0.2));
				break;
		
			default:
				break;
		}
		 */
		
		

    }
	public static void altShade(int level,double weight,Ray ray,double tnear,double tmin,double tmax,RObject robject ,Isect[]  hit,Isect xd,Vec3 col,int id ) {
		long start,stop;
		col.setZero();
		
		Vec3 temp = new Vec3(0);
		Vec3 temp2 = new Vec3(0);
		//Vec3 temp3 = new Vec3(0);
		Vec3 uv = ray.direction.unitVector();
    	Vec3 phit = new Vec3(0);
		Vec3 P = raytracer.rayPoint(ray,hit[0].t);
		Vec3 N = robject.normal(P,hit[0].indexTriangle);
		ray.origin.add(ray.direction.mult(tnear,temp),phit);
		Vec3 nhit = null;
		nhit= robject.normal(phit,hit[0].indexTriangle);
    	
    	//nhit.normalize();
		Surf surf =  robject.getSurf();
    	
    	
    	double bias =  0.0001;
    	boolean inside = false;
    	if (ray.direction.dot(nhit)>0) {
    		nhit.negate();
    		inside = true;
    		
    		
    	}
    	//if not defuse
		
    	if( ((surf.ktlucence> 0) || (surf.kspec> 0)) &&  level < raytracer.maxlevel) {
			
		//if(surf.kspec> 0) {
    		Vec3 reflection = new Vec3(0);
    		Vec3 refraction = new Vec3(0);
    			
    		
    		
    		double facingratio = -ray.direction.dot(nhit);
    		double fresneleffect = mix(Math.pow(1-facingratio, 3),1,0.1);
    		
    		//do reflection
    		if(surf.kspec> 0) {
				start = System.currentTimeMillis();
				
				
				Vec3  refldir = new Vec3(0);
        		//ray.direction.sub(nhit.mult(2,temp).mult(ray.direction.dot(nhit),temp2),refldir);


				ray.direction.sub(N.mult(2*ray.direction.dot(N),temp), refldir);
				refldir.add(Vec3.randomInUnitSphere().mult(robject.getSurf().fuzz,temp));

        		
        		refldir.normalize();
        		 //colour;

        		bvhTrace(level+1,weight,new Ray(phit.add(nhit.mult(bias,temp2),temp),refldir),reflection,tmin,tmax,id);
				stop = System.currentTimeMillis();
				raytracer.reflectionTime[id] += (stop-start);
    		}

    		
    		
    		//do refraction
			
    		else if(surf.ktlucence>0) {
				start = System.currentTimeMillis();
    			double cosTheta = Math.min(N.dot(uv.negate(temp)),1); 
				Vec3 rOutPerp = new Vec3();
				double refractionRatio = (hit[0].enter ==1) ? surf.ktlucence : (1/surf.ktlucence); 
				uv.add(N.mult(cosTheta, temp), rOutPerp).mult(refractionRatio);
				Vec3 rOutParalell = new Vec3();
				N.mult(Math.sqrt(Math.abs(1.0-rOutPerp.length2())), rOutParalell);
				rOutParalell.add(rOutPerp);
				Ray refRay = new Ray(P,rOutParalell);
				bvhTrace(level+1,weight,refRay,refraction,tmin,tmax,id);

    		}
			
    		
			refraction.mult(1-fresneleffect);
    		reflection.mult(fresneleffect,temp);
    		temp.add(refraction);
    		temp.mult(surf.colour);
    		col.setValuesV(temp);
    		
    		
    	}
    	else {
			
			start = System.currentTimeMillis();
			//lambertianReflectance(level,weight,tmin,tmax,ray,hit,robject,col,id);
			
			

			//
			
    		for(int i = 0;i< raytracer.objects.size;i++) {
    			if(raytracer.objects.get(i) != robject && raytracer.objects.get(i).getSurf().emission_colour != null) {
    				Vec3 transmission = new Vec3(1);
    				Vec3 lightDirection = new Vec3(0);
					(raytracer.objects.get(i)).getCenter().sub(phit,lightDirection);
    				lightDirection.normalize();
    				for(int j = 0;j<raytracer.objects.size();j++) {
    					if(raytracer.objects.get(j) != raytracer.objects.get(i)) {
    						if(raytracer.objects.get(j).intersection(new Ray(phit.add(nhit.mult(bias,temp2),temp).add(N,temp).add(Vec3.randomUnitVector(),temp),lightDirection),tmin,tmax, hit)>0) {
    							transmission.setZero();;
    							break;
    						}
    					}
    				}
    				col.add(surf.colour.mult(transmission,temp2).mult
    						(Math.max(0.,nhit.dot(lightDirection)),temp).mult(raytracer.objects.get(i).getSurf().emission_colour,temp2 ) );
    				
    			}
    		}
			
			stop = System.currentTimeMillis();
			raytracer.diffuseTime[id]+= stop-start;
			
			
    	}

    }

	public static void lambertianReflectance(int level,double weight,double tmin,double tmax,Ray ray,Isect hit[],RObject robject,Vec3 col,int id){
		Vec3 temp = new Vec3();
		Vec3 bounce = new Vec3(0);
		Vec3 P = raytracer.rayPoint(ray,hit[0].t);
		Vec3 N = robject.normal(P,hit[0].indexTriangle);
		Vec3 transmission = new Vec3(1);
		Vec3 dir = new Vec3(0);
		ray.direction.add(N,dir).add(Vec3.randomUnitVector());
		Ray target = new Ray(P, dir);
		bvhTrace(level+1, weight, target, bounce, tmin, tmax,id);
		/* 
		for(int i = 0;i< raytracer.lights.size();i++) {
			raytracer.lights.get(i).getCenter().sub(P,dir);
			target.direction = dir;
			target.origin = P ;
			
			
			if(raytracer.lights.get(i).intersection(target, tmin, tmax, hit)>0){
				transmission.setZero();

			}
		}
		*/
		col.add(bounce.mult(0.5,temp),col).mult(hit[0].prim.getSurf().colour);
		//col.mult(transmission);

		 
	}
	public static void dioptics(){

	}


	public static void altShade(Vec3 col){

	}
    //based on Heckbert-Hanranhan84
	/* 
    private static boolean TransmissionDirection(Isect[] hit, Vec3 I, Vec3 N, Ray tray) {
		double n1, n2, eta, c1, cs2;
		n1 = hit[0] != null && hit[0].medium != null ? hit[0].medium.refrindex : 1.;
		n2 = hit[1] != null && hit[1].medium != null ? hit[1].medium.refrindex : 1.;
		eta = n1/n2;
		
		c1 = -I.dot(N);
		cs2 = 1.-eta*eta*(1.-c1*c1);
		if(cs2<0.) return false;
		tray.direction = I.comb(eta, N, eta*c1-Math.sqrt(cs2));
		
		
		return false;
	}
	*/
	private static Vec3 specularDirection(Vec3 N, Vec3 I) {
		Vec3 sd = N.copy();
		sd.addS(-2.*I.dot(N), I);
		return sd;
	}
	public static boolean shadow(Ray ray,double tmin,double tmax) {
		int nhit;
		Isect[] hit = new Isect[raytracer.ISECTMAX];
		int hitpos = 0;
		
		for( int i = 0; i<raytracer.lights.size();i++) {
			if(raytracer.lights.get(i).intersection(ray,tmin,tmax, hit)== 0) {
				return true;
			}
			if( hit[0].t > raytracer.rayeps) {
				return true;
			}

		}
    	return false;
    }
}

