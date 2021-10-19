
// Server with NON-Determinate Service time

import java.lang.Math; 

public class NondeterminateServer extends Server {

    public NondeterminateServer(int serverId, int numProcessors,
        double t1, double p1, double t2, double p2, double t3, double p3) 
    {
        super(serverId, numProcessors);
        this.t1 = t1;
        this.p1 = p1;
        this.t2 = t2;
        this.p2 = p2;
        this.t3 = t3;
        this.p3 = p3; 
    }

    double t1;
    double p1;
    double t2;
    double p2;
    double t3;
    double p3; 

    // This function returns the service time of handling an instance of a request
    double getServiceTime() {
        double prob = Math.random(); 
        if (prob < p1) {
            return t1; 
        } else if (prob > p1 && prob < p1 + p2) {
            return t2;
        } else {
            return t3; 
        }
    }

}