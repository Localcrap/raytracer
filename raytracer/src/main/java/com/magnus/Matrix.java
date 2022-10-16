package com.magnus;
public class Matrix{
    int height,width;
    int[] matrix;

    public Matrix(int height,int width){
        this.height =  height;
        this.width = width;
        matrix = new int[height*width];


    }
    public int getHeight(){
        return height;
    }
    public int getWidth(){
        return width;
    }
}