
// Server with NON-Determinate Service time

import java.lang.Math; 

public class NondeterminateServer extends Server {

    public NondeterminateServer(double lambdaA, double P_Termination, int serverId, int numProcessors,
        double t1, double p1, double t2, double p2, double t3, double p3) 
    {
        super(lambdaA, P_Termination, serverId, numProcessors);
        // this.currentRequest = new Request(-1, 0, new ArrEvent(0, -1, -1, false), new StartEvent(0, -1, -1, false), new DoneEvent(0, -1, -1, false), null); // Create dummy request
        this.t1 = t1;
        this.p1 = p1;
        this.t2 = t2;
        this.p2 = p2;
        this.t3 = t3;
        this.p3 = p3; 
        // for (int i = 0; i < NUM_PROCESSORS; i++) {
        //     processors[i] = new Processor(lambdaA, P_Termination, this, i+1); 
        // }
    }

    double t1;
    double p1;
    double t2;
    double p2;
    double t3;
    double p3; 

    double getlambdaS() {
        double prob = Math.random(); 
        if (prob <= p1) {
            return 1.0/t1; 
        } else if (prob > p1 && prob <= p1 + p2) {
            return 1.0/t2;
        } else {
            return 1.0/t3; 
        }
    }

}