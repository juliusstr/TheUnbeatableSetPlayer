package com.company;

import java.util.Arrays;

public class Matrix {

    double[][] data;
    int row;
    int colum;


    public Matrix(int row, int colum){
        data = new double [row][colum];
        this.colum = colum;
        this.row = row;
        for (int i = 0; i < this.row; i++) {
            for (int j = 0; j < this.colum; j++) {
                data[i][j] = 0;
            }
        }
    }

    double get (int x, int y){
        return data [x][y];
    }

    void replaceNumber (int x, int y, double data){
        this.data[x][y] = data;
    }

    void random(){
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < colum; j++) {
                this.data[i][j] = (Math.random()*2-1);
            }
        }
    }

    void print(){
        System.out.println("ROWS: " + row + " COLUMS: " + colum);
        System.out.println(Arrays.deepToString(this.data));
    }

    static Matrix addMatrix (Matrix a, Matrix b){
        if (a.row != b.row || a.colum != b.colum) {
            System.out.println("Matrix skal have samme størlse");
            return null;
        }
        Matrix resultat = new Matrix(a.row,a.colum);
        for (int i = 0; i < a.row; i++) {
            for (int j = 0; j < a.colum; j++) {
                resultat.data[i][j] = a.data[i][j] + b.data[i][j];
            }
        }
        return resultat;
    }

    void addMatrixSelv (Matrix a){
        if (a.row != this.row || a.colum != this.colum) {
            System.out.println("MAtrix skal have samme størlse");
        } else {
            for (int i = 0; i < this.row; i++) {
                for (int j = 0; j < this.colum; j++) {
                    this.data[i][j] = a.data[i][j] + this.data[i][j];
                }
            }
        }
    }

    void scaleSelv (double S){
        for (int i = 0; i < this.row; i++) {
            for (int j = 0; j < this.colum; j++) {
                this.data[i][j] = this.data[i][j]*S;
            }
        }
    }

    static Matrix scale (Matrix a,double S){
        Matrix resultat = new Matrix(a.row,a.colum);
        for (int i = 0; i < a.row; i++) {
            for (int j = 0; j < a.colum; j++) {
                resultat.data[i][j] = a.data[i][j]*S;
            }
        }
        return resultat;
    }

    void addNumberSelv (double a){
        for (int i = 0; i < this.row; i++) {
            for (int j = 0; j < this.colum; j++) {
                this.data[i][j] = a + this.data[i][j];
            }
        }
    }

    static Matrix addNumber (Matrix b, double val){
        Matrix resultat = new Matrix(b.row,b.colum);
        for (int i = 0; i < b.row; i++) {
            for (int j = 0; j < b.colum; j++) {
                resultat.data[i][j] = val + b.data[i][j];
            }
        }
        return resultat;
    }

    void multiplySelv (Matrix a){
        if (a.row != this.row || a.colum != this.colum) {
            System.out.println("MAtrix skal have samme størlse");
        } else {
            for (int i = 0; i < this.row; i++) {
                for (int j = 0; j < this.colum; j++) {
                    this.data[i][j] = a.data[i][j] * this.data[i][j];
                }
            }
        }
    }

    static Matrix multiplyElement (Matrix a, Matrix b){
        if (a.row != b.row || a.colum != b.colum) {
            System.out.println("MAtrix skal have samme størlse");
            return null;
        } else {
            Matrix resultat = new Matrix(b.row,b.colum);
            for (int i = 0; i < b.row; i++) {
                for (int j = 0; j < b.colum; j++) {
                    resultat.data[i][j] = a.data[i][j] * b.data[i][j];
                }
            }
            return resultat;
        }
    }

    static Matrix MatrixMultiply (Matrix a, Matrix b){
        if (a.colum != b.row) {
            System.out.println("Matrix a skal have samme colum som b har row");
            return null;
        } else {
            Matrix resultat = new Matrix(a.row,b.colum);
            double sum;
            for (int i = 0; i < resultat.row; i++) {
                for (int j = 0; j < resultat.colum; j++) {
                    sum = 0;
                    for (int k = 0; k < a.colum; k++) {
                        sum += a.data[i][k] * b.data[k][j];
                    }
                    resultat.data[i][j] = sum;
                }
            }
            return resultat;
        }
    }

    void  MatrixMultiplySelv (Matrix b){
        if (this.colum != b.row) {
            System.out.println("Matrix a skal have samme colum som b har row");
        } else {
            Matrix resultat = new Matrix(this.row,b.colum);
            double sum;
            for (int i = 0; i < resultat.row; i++) {
                for (int j = 0; j < resultat.colum; j++) {
                    sum = 0;
                    for (int k = 0; k < this.colum; k++) {
                        sum += this.data[i][k] * b.data[k][j];
                    }
                    resultat.data[i][j] = sum;
                }
            }
            this.data = resultat.data;
            this.row = resultat.row;
            this.colum = resultat.colum;
        }
    }

    void transposeSelv (){
        Matrix resultat = new Matrix(this.colum,this.row);
        for (int i = 0; i < this.row; i++) {
            for (int j = 0; j < this.colum; j++) {
                resultat.data[j][i] = this.data[i][j];
            }
        }
        this.data = resultat.data;
        this.row = resultat.row;
        this.colum = resultat.colum;
    }

    static Matrix transpose (Matrix a){
        Matrix resultat = new Matrix(a.colum,a.row);
        for (int i = 0; i < a.row; i++) {
            for (int j = 0; j < a.colum; j++) {
                resultat.data[j][i] = a.data[i][j];
            }
        }
        return resultat;
    }

    static Matrix mapSigmoid (Matrix a){
        double x;
        Matrix resultat = new Matrix(a.row,a.colum);
        for (int i = 0; i < a.row; i++) {
            for (int j = 0; j < a.colum; j++) {
                x = a.data[i][j];
                resultat.data[i][j] = sigmoid(x);
            }
        }
        return resultat;
    }

    //todo
    // lav en ny aktiverings funktion og den D af den

    public static double sigmoid(double x) {
        return (1/( 1 + Math.pow(Math.E,(-1*x))));//orginal
        /*if (x>0){
            return x;
        } else {
            return 0;
        }*/
    }

    public static double dSigmoid(double x) { //er ikke dsigmoid
        return x*(1-x);//orginal
        /*if (x<0){
            return 0;
        } else if (x>0){
            return 1;
        } else {
            return 0.5;
        }*/
    }

    static Matrix mapDSigmoid (Matrix a){
        double x;
        Matrix resultat = new Matrix(a.row,a.colum);
        for (int i = 0; i < a.row; i++) {
            for (int j = 0; j < a.colum; j++) {
                x = a.data[i][j];
                resultat.data[i][j] = dSigmoid(x);
            }
        }
        return resultat;
    }

    static Matrix arrToMatrix(double[] input){
        int len = input.length;
        Matrix ma = new Matrix(len,1);
        for (int i = 0; i < ma.row; i++) {
            ma.data[i][0] = input[i];
        }
        return ma;
    }

    static double[] MatrixToArr(Matrix input){
        int len = input.row;
        double[] arr = new double[len];
        for (int i = 0; i < len; i++) {
            arr[i] = input.data[i][0];
        }
        return arr;
    }

    static Matrix subtractElement(Matrix a, Matrix b){
        if (a.row != b.row || a.colum != b.colum) {
            System.out.println("Matrix skal have samme størlse");
            return null;
        } else {
            Matrix resultat = new Matrix(b.row,b.colum);
            for (int i = 0; i < a.row; i++) {
                for (int j = 0; j < a.colum; j++) {
                    resultat.data[i][j] = a.data[i][j] - b.data[i][j];
                }
            }
            return resultat;
        }
    }
}
