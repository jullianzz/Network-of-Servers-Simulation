
import java.util.Iterator;

public class MMKServer extends DeterminateServer {

    public MMKServer(double lambdaS, double lambdaA, double P_Termination, int serverId, int numProcessors, int K) {
        super(lambdaS, lambdaA, P_Termination, serverId, numProcessors);
        this.K = K; 
        this.numDroppedRequests = 0; 
    }

    final int K; 
    int numDroppedRequests; 

    double getlambdaS() {
        return lambdaS; 
    }

    // handleRejectedRequest is called when the queue to the M/M/K Server is full
    void handleRejectedRequest() {
        numDroppedRequests ++; 
    }

    // 'peekTime' is the time instance where the population of the server is measured
    int getCurrentPopulation(double peekTime) {
        timeline.sortChronologically();
        int p = 0; 
        Event evt; 
        for (Iterator<Event> iter = timeline.queue.iterator(); iter.hasNext();) {
            evt = iter.next(); 
            if (evt.timeStamp <= peekTime) {
                switch (evt.type) {
                    case ARR: 
                        p ++;
                        break;
                    case DONE: 
                        p --;
                        break;
                    default:
                        break; 
                }
            } else {
                break;
            }
        }
        return p; 
    }
}
