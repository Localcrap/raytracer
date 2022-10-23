package com.magnus;
import java.util.ArrayList;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.MaximizeAction;

public class TriangleMesh implements RObject{
	Vec2[] texCoordinates;
	Surf surf;
	Vec3[] P, N;
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









	}
	public static boolean rayTriangleIntersect(Ray r,Vec3 v0,Vec3 v1,Vec3 v2, double[] u){
		Vec3 v0v1 = v1.sub(v0);
		Vec3 v0v2 = v2.sub(v0);
		Vec3 pvec = r.direction.cross(v0v2);
		double det = v0v1.dot(pvec);
		if(Math.abs(det)<kEpsilon){
			return false;

		}
		double invDet = 1/det;
		Vec3 tvec = r.origin.sub(v0);
		u[0] = tvec.dot(pvec) * invDet;
		if ( u[0]<0 || u[0]>1){
			return false;
		}
		Vec3 qvec = tvec.cross(v0v1);
		u[1] = r.direction.dot(qvec)* invDet;
		if ( u[1]<0 || u[0]+u[1] >1){
			return false;

		}

		u[2] = v0v2.dot(qvec)*invDet;

		

		return true;

	}


	public int intersection(Ray ray,Isect[] hit){
		int j = 0;
		int isect = 0;
		
		hit[0] = new Isect(this, surf);
		hit[0].t = Double.MAX_VALUE;
		
		for (int i = 0; i<numTris;i++){
			Vec3 v0 = P[trisIndex[j]];
			Vec3 v1 = P[trisIndex[j+1]];
			Vec3 v2 = P[trisIndex[j+2]];
			
			double[] u = new double[3];
			u[2]= Double.MAX_VALUE;
			if(rayTriangleIntersect(ray, v0, v1, v2,  u)){
				if( u[2] < hit[0].t){
					//TODO: test array;
					hit[0].t = u[2];
					hit[0].uvTriangle.x = u[0];
					hit[0].uvTriangle.y = u[1];
					hit[0].indexTriangle = i;
					isect = 1;
				}



			}
			j+=3;
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
		surf.ktlucence  =1;
		surf.colour = new Vec3(1, 1, 1);


		return new TriangleMesh(npolys,vertsIndex, faceIndex, P, N, st, surf);
	}

	@Override
	public Vec3 normal(Vec3 p) {
		// TODO Auto-generated method stub
		return null;
	}
	public Vec3 normal(Vec3 p, int triIndex) {
		Vec3 v0 = P[trisIndex[triIndex * 3]]; 
        Vec3 v1 = P[trisIndex[triIndex * 3 + 1]]; 
        Vec3 v2 = P[trisIndex[triIndex * 3 + 2]]; 
        Vec3 hitNormal = (v1.sub(v0)).cross(v2.sub(v0));//(v1 - v0).crossProduct(v2 - v0); 
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
		// TODO Auto-generated method stub
		return raytracer.test.TRIANGLEMESH;
	}
	@Override
	public Surf getSurf() {
		// TODO Auto-generated method stub
		return surf;
	}

}

