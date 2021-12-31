// Julia Zeng, BU ID: U48618445
// CS-350 HW 2

import java.lang.Math;

public class Exp {
    
    static double getExp(double lambda) {
        //returns interarrival time, a random value in exponential distribution with mean T = 1/lambda
        double Y = Math.random();                           // Y is selected from a uniformly random distribution
        double x = -1.0*Math.log(1.0-Y)/lambda;             // x is the r.v. for exponential distribution
        // double expPdf = lambda*Math.exp(-1.0*lambda*x);     // expPdf is the value of the exponential pdf at x  

        return x; 
    }

    public static void main(String[] args) { 
        double lambda = Double.parseDouble(args[0]);
        int N = Integer.parseInt(args[1]);

        for (int i = 0; i < N; i++) {
            System.out.println(getExp(lambda)); 
        }

    }
}


