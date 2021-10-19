// Server with Determinate Service time

public class DeterminateServer extends Server {

    public DeterminateServer(double lambdaS, int serverId, int numProcessors) 
    {
        super(serverId, numProcessors);
        this.lambdaS = lambdaS; 
    }

    double lambdaS; 

    double getServiceTime() {
        return Exp.getExp(lambdaS); 
    }

}


