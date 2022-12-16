package com.magnus;
import java.util.ArrayList;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.MaximizeAction;

public class TriangleMesh implements RObject{
	Vec2[] texCoordinates;
	Surf surf;
	Vec3[] P, N;
	Vec3 center;
	int[] trisIndex;
	int numTris = 0;
	public static  double kEpsilon = 1e-8; 
	
	public TriangleMesh(int nf, int[] vi,int[] fi, Vec3[] verts, Vec3[] normals, Vec2[] st, Surf surf) {
		int k = 0, maxVert  =0;
		for(int i = 0;i < nf;i++){
			numTris += fi[i]-2;
			for(int j = 0; j<fi[i];j++){
				if(vi[k+j] >maxVert){
					maxVert = vi[k+j];
				}
			}
			k += fi[i];
		}
		P = new Vec3[maxVert+1];
		
		for(int i = 0; i<maxVert+1;i++){
			P[i]= verts[i];

		}
		trisIndex = new int[numTris*3];
		N = new Vec3[numTris*3];
		
		int TIndexCounter = 0;
		k=0;
		for(int i = 0 ; i<nf;i++){
			 for(int j=0;j<fi[i]-2;j++){
				trisIndex[TIndexCounter]= vi[k];
				trisIndex[TIndexCounter+1]= vi[k+j+1];
				trisIndex[TIndexCounter+2]= vi[k+j+2];
				//N[TIndexCounter] = normals[k];
				//N[TIndexCounter+1] = normals[k+j +1];
				//N[TIndexCounter+2] = normals[k+j+2];
				TIndexCounter+=3;

			 }
			 k+= fi[i];
		}
		N = normals;
		this.surf = surf;
		texCoordinates = st;

		Vec3 center = new Vec3();

		for(Vec3 p : P){
			center.add(p);
		}
		center.div(P.length);
		this.center = center;









	}
	//møller trombore 
	public static boolean rayTriangleIntersect(Ray r,Vec3 v0,Vec3 v1,Vec3 v2, double[] u){
		Vec3 v0v1 = new Vec3(0),v0v2 = new Vec3(0),pvec = new Vec3(0);
		v1.sub(v0,v0v1);
		v2.sub(v0,v0v2);
		r.direction.cross(v0v2,pvec);
		double det = v0v1.dot(pvec);
		//paralell to triangle?
		if(Math.abs(det)<kEpsilon){
			return false;

		}
		double invDet = 1/det;
		Vec3 tvec = new Vec3(0);
		r.origin.sub(v0,tvec);
		u[0] = tvec.dot(pvec) * invDet;
		if ( u[0]<0 || u[0]>1){
			return false;
		}
		Vec3 qvec = new Vec3(0);
		tvec.cross(v0v1,qvec);
		u[1] = r.direction.dot(qvec)* invDet;
		if ( u[1]<0 || u[0]+u[1] >1){
			return false;

		}

		u[2] = v0v2.dot(qvec)*invDet;

		if(u[2]<raytracer.rayeps){
			return false;
		}

		

		return true;

	}
	//alt intersection algorithm following Jeff Arenberg's example
	public static boolean altRayTriangleIntersect(Ray r,Vec3 v0,Vec3 v1,Vec3 v2, double[] u){
		
		Vec3 p0 = new Vec3(),p1 = new Vec3(),p2 = new Vec3(),N = new Vec3(),temp = new Vec3();
		Vec3 bb[] = new Vec3[3];
		v0.sub(r.origin,p0);
		v0.sub(v1,p1); //v0-v1
		v0.sub(v2,p2); //v0-v2
		p1.cross(p2,N); // N

		bb[0] = p1.inv(p1);
		bb[1] = p2.inv(p2);
		bb[2] = N.inv(N);


		double den = r.direction.dot(bb[2]);
		//is this u?;
		u[0]= den;
		//paralell to triangle
		if(den == 0){
			return false;
		}
		double num = p0.sub(r.origin, temp).dot(bb[2]);
		u[1] = num;
		//if on or behind triangle
		double t = num/den;
		if (t <= 0){
			return false;
		}
		Vec3 p = new Vec3();
		r.direction.mult(t,p).add(r.origin,p).sub(p0);
		double a,b;
		a = p.dot(bb[0]);
		b = p.dot(bb[1]);
		//if not in triangle
		if (a < 0.0 || b < 0.0 || a + b > 1.0){
			return false;
		}
		u[2]= t;




		return true;
	}

 //cycle through all triangles and test intersection
	public int intersection(Ray ray,double tmin,double tmax,Isect[] hit){
		int j = 0;
		int isect = 0;
		Isect h1;// = new Isect(this, surf);
		h1 = new Isect(this, surf);
		h1.t = Double.MAX_VALUE;
		
		for (int i = 0; i<numTris;i++){
			Vec3 v0 = P[trisIndex[j]];
			Vec3 v1 = P[trisIndex[j+1]];
			Vec3 v2 = P[trisIndex[j+2]];
			
			double[] u = new double[3];
			u[2]= Double.MAX_VALUE;
			if(rayTriangleIntersect(ray, v0, v1, v2,  u)){
				if( u[2] < h1.t){
					//TODO: test array;
					h1.t = u[2];
					h1.uvTriangle.x = u[0];
					h1.uvTriangle.y = u[1];
					h1.indexTriangle = i;
					isect = 1;
				}



			}
			j+=3;
		}
		if(isect==1){
			if(hit[0]!=null){
				if(hit[0].t < h1.t){
					return isect;
				}
			}
			hit[0]= h1;
		}
		return isect;
	}
	public int altIntersection(Ray ray,double tmin,double tmax,Isect[] hit, int v){
		boolean test = false;
		int j = 0;
		int isect = 0;
		Isect h1;// = new Isect(this, surf);
		h1 = new Isect(this, surf);
		h1.t = Double.MAX_VALUE;
		
		for (int i = 0; i<numTris;i++){
			Vec3 v0 = P[trisIndex[j]];
			Vec3 v1 = P[trisIndex[j+1]];
			Vec3 v2 = P[trisIndex[j+2]];
			
			double[] u = new double[3];
			u[2]= Double.MAX_VALUE;
			if(v == 0){
				test =altRayTriangleIntersect(ray, v0, v1, v2,  u);

			}
			else{
				test = insideOutsideTest(ray, v0, v1, v2, u);
			}
			if(test){
				if( u[2] < h1.t){
					//TODO: test array;
					h1.t = u[2];
					h1.uvTriangle.x = u[0];
					h1.uvTriangle.y = u[1];
					h1.indexTriangle = i;
					isect = 1;
				}



			}
			j+=3;
		}
		if(isect==1){
			if(hit[0]!=null){
				if(hit[0].t < h1.t){
					return isect;
				}
			}
			hit[0]= h1;
		}
		return isect;
	}

	public static TriangleMesh generatePolySphere(double rad,int divs){
		
		int numVertices = (divs-1)*divs+2;

		Vec3[] P  =new Vec3[numVertices];
		Vec3[] N  =new Vec3[numVertices];
		Vec2[] st  =new Vec2[numVertices];
		//Vec3[] P = new Vec3[numVertices];
		//Vec3[] N = new Vec3[numVertices];
		//Vec2[] St = new Vec2[numVertices];

		double u = -Math.PI/2;
		double v =  -Math.PI;
		double du = Math.PI / divs;
		double dv = 2* Math.PI /divs;
		int pIndex = 0,stIndex = 0;
		P[pIndex] = new Vec3(0,-rad,0);
		N[pIndex] = new Vec3(0,-rad,0);
		pIndex++;
		int k = 1; 
		for(int i = 0; i<divs -1; i++){
			u += du;
			v = -Math.PI;
			for(int j = 0; j<divs; j++){
				double x = rad*Math.cos(u)*Math.cos(v);
				double y = rad* Math.sin(u);
				double z  = rad*Math.cos(u)*Math.sin(v);
				P[pIndex] = new Vec3(x,y,z);
				N[pIndex] = new Vec3(x,y,z);
				st[stIndex++] = (new Vec2(u / Math.PI +0.5, v * 0.5 / Math.PI +0.5));
				pIndex++;
				v += dv; 
				k++;

				
			}
		}
		P[pIndex] = (new Vec3(0,-rad,0));
		N[pIndex] = (new Vec3(0,-rad,0));
		pIndex++;
		int npolys = divs * divs;
		int[] faceIndex = new int[npolys];
		int[] vertsIndex = new int[((6+ (divs-1) * 4)*divs)];
		int vid = 1, numV = 0, l = 0;
		k = 0;
		for(int i = 0; i< divs; i++){
			for(int j = 0; j<divs; j++){
				if(i == 0){
					faceIndex[k++] = 3;
					vertsIndex[l] = 0;
					vertsIndex[l+1] = j + vid;
					vertsIndex[l+2] = (j == (divs -1)) ? vid : j + vid +1;
					l+= 3;


				}
				else if (i == (divs - 1)) { 
					faceIndex[k++] = 3; 
					vertsIndex[l] = j + vid + 1 - divs; 
					vertsIndex[l + 1] = vid + 1; 
					vertsIndex[l + 2] = (j == (divs - 1)) ? vid + 1 - divs : j + vid + 2 - divs; 
					l += 3; 
				} 
				else { 
					faceIndex[k++] = 4; 
					vertsIndex[l] = j + vid + 1 - divs; 
					vertsIndex[l + 1] = j + vid + 1; 
					vertsIndex[l + 2] = (j == (divs - 1)) ? vid + 1 : j + vid + 2; 
					vertsIndex[l + 3] = (j == (divs - 1)) ? vid + 1 - divs : j + vid + 2 - divs; 
					l += 4; 
				}
				numV++; 
			}
			vid = numV;
		}
		Surf surf = new Surf();
		surf.ktlucence  =0;
		surf.kspec = 1;
		surf.fuzz = 0;
		surf.colour = new Vec3(1, 1, 1);


		return new TriangleMesh(npolys,vertsIndex, faceIndex, P, N, st, surf);
	}

	public boolean insideOutsideTest(Ray r,Vec3 v0,Vec3 v1,Vec3 v2, double[] u){
		Vec3 temp = new Vec3();
		Vec3  v0v1 =  new Vec3();
		Vec3  v0v2 =  new Vec3();
		Vec3  N =  new Vec3();

		v1.sub(v0, v0v1);
		v2.sub(v0,v0v2);
		v0v1.cross(v0v2,N);


		double area2 = N.length();

		double NdotRayDirection = N.dot(r.direction);
		//check if ray and plane are parallel.
		if(NdotRayDirection < kEpsilon){
			return false;
		}
		double d = -N.dot(v0);

		double t = - (N.dot(r.origin)+d)/ NdotRayDirection;

		if(t<0){
			return false;
		}
		Vec3 P =  new Vec3();
		r.origin.add(r.direction.mult(t, temp), P);

		Vec3 c = new Vec3();
		
		Vec3 edge0 = new Vec3();
		Vec3 vp0 = new Vec3();

		v1.sub(v0,edge0);
		P.sub(v0,vp0);

		edge0.cross(vp0,c);
		if(N.dot(c)<0){
			return false;
		}
		Vec3 edge1 = new Vec3();
		Vec3 vp1 = new Vec3();

		v2.sub(v1,edge1);
		P.sub(v1,vp1);

		edge1.cross(vp1,c);
		if(N.dot(c)<0){
			return false;
		}
		Vec3 edge2 = new Vec3();
		Vec3 vp2 = new Vec3();

		v0.sub(v2,edge2);
		P.sub(v2,vp2);

		edge2.cross(vp2,c);
		if(N.dot(c)<0){
			return false;
		}
		u[1]=t;
		u[0]=d;
	
		return true;
	}

	
	@Override
	public Vec3 normal(Vec3 p, int triIndex) {

		/* 
		Vec3 n0 = N[triIndex * 3]; 
        Vec3 n1 = N[triIndex * 3 + 1]; 
        Vec3 n2 = N[triIndex * 3 + 2]; 
        Vec3 hitNormal2 =  (1 - uv.x - uv.y) * n0 + uv.x * n1 + uv.y * n2;  
		*/
		Vec3 temp = new Vec3();
		Vec3 v0 = P[trisIndex[triIndex * 3]]; 
        Vec3 v1 = P[trisIndex[triIndex * 3 + 1]]; 
        Vec3 v2 = P[trisIndex[triIndex * 3 + 2]]; 
        Vec3 hitNormal = new Vec3();
		 (v1.sub(v0,hitNormal)).cross(v2.sub(v0,temp));//(v1 - v0).crossProduct(v2 - v0); 
        hitNormal.normalize(); 

		return hitNormal;
	}
	@Override
	public boolean read() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public raytracer.test name() {
		return raytracer.test.TRIANGLEMESH;
	}
	@Override
	public Surf getSurf() {
		return surf;
	}
	@Override
	public boolean boundingBox(BVHValues v) {
		double xmin = P[0].x,xmax = P[0].x, ymin = P[0].y ,ymax= P[0].y, zmin = P[0].z,zmax = P[0].z;
		for(int i = 1;i<P.length;i++){
			if(P[i].x <xmin){
				xmin  = P[i].x;
			}
			else if(P[i].x >xmax){
				xmax = P[i].x;
			}
			if(P[i].x <ymin){
				ymin  = P[i].y;
			}
			else if(P[i].x >ymax){
				ymax = P[i].y;
			}
			if(P[i].x <zmin){
				zmin  = P[i].z;
			}
			else if(P[i].x >zmax){
				zmax = P[i].z;
			}
		}
		v.outputBox = new BVH(new Vec3(xmin,ymin,zmin),new Vec3(xmax, ymax, zmax));

		return false;
	}
	@Override
	public Vec3 getCenter() {
		return center;
	}

}

