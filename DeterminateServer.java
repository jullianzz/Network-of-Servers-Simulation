// Server with Determinate Service time

public class DeterminateServer extends Server {

    public DeterminateServer(double lambdaS, double lambdaA, double P_Termination, int serverId, int numProcessors) 
    {
        super(lambdaA, P_Termination, serverId, numProcessors);
        // this.currentRequest = new Request(-1, 0, new ArrEvent(0, -1, -1, false), new StartEvent(0, -1, -1, false), new DoneEvent(0, -1, -1, false), null); 
        // Create dummy request
        this.lambdaS = lambdaS; 
        // for (int i = 0; i < NUM_PROCESSORS; i++) {
        //     processors[i] = new Processor(lambdaA, P_Termination, this, i+1); 
        // }
    }

    double lambdaS; 

    double getlambdaS() {
        return lambdaS; 
    }

}


