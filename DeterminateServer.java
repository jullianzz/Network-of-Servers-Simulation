// Server with Determinate Service time

public class DeterminateServer extends Server {

    public DeterminateServer(double lambdaS, double P_Termination, int serverId, int numProcessors) 
    {
        super(P_Termination, serverId, numProcessors);
        this.lambdaS = lambdaS; 
    }

    double lambdaS; 

    double getServiceTime() {
        return Exp.getExp(lambdaS); 
    }

}


