// Abstract base class Server

import java.util.LinkedList;

public abstract class Server implements Cloneable {

    // Constructor
    public Server(double lambdaA, double P_Termination, int serverId, int numProcessors) {
        this.lambdaA = lambdaA; 
        this.P_Termination = P_Termination; 
        this.serverId = serverId; 
        this.NUM_PROCESSORS = numProcessors; 
        this.processors = new Processor[NUM_PROCESSORS]; 
        for (int i = 0; i < NUM_PROCESSORS; i++) {
            processors[i] = new Processor(lambdaA, P_Termination, this, i+1); 
        }
        this.completedRequests = 0; 
        this.timeline = new Timeline(); 
        this.currentRequest = Request.dummy(serverId); 
    }
    

    // Meta Data
    int serverId;
    int completedRequests;                       // Number of completed requests seen at the Server
    double lambdaA;                         // Rate of arrival of requests at the Server
    Request currentRequest;                 // Current request processed at the Server
    double P_Termination;                   // Probability a request terminates at the Server
    int monitorCount; 

    // Timeline
    Timeline timeline; 

    // Processors – Array of Processors
    final int NUM_PROCESSORS; 
    Processor[] processors;

    // Statistics
    double avgPopulation; 
    double avgResponseTime; 
    double runningPopulation; 
    double runningResponseTime; 

    // Methods
    abstract double getlambdaS(); 

    void computeStatistics(double time) {
        runningPopulation = 0.0; 
        runningResponseTime = 0.0; 
        for (int i = 0; i < NUM_PROCESSORS; i++) {
            processors[i].computeStatistics(time);
            runningPopulation += processors[i].runningPopulation; 
            runningResponseTime += processors[i].runningResponseTime; 
        }
        avgPopulation = runningPopulation / ((double) monitorCount); 
        avgResponseTime = runningResponseTime / ((double) completedRequests); 
    }


    // Call monitorSystem method of each processor
    void monitorSystem(double T) {
        for (int i = 0; i < NUM_PROCESSORS; i++) {
            processors[i].monitorSystem(T);
            monitorCount += processors[i].monitorCount; 
        }
    }

    // Finds the earliest available processor to serve a request that arrives at arrTime
    int earliestAvailableProcessor() {
        double temp = processors[0].currentRequest.doneEvt.timeStamp;
        int idx = 0; 
        for (int i = 0; i < NUM_PROCESSORS; i++) {
            if (temp > processors[i].currentRequest.doneEvt.timeStamp) {
                temp = processors[i].currentRequest.doneEvt.timeStamp; 
                idx = i; 
            }
        }
        return idx; 
    }

    // Handle a request that is finished at the primary server
    LinkedList<Request> handleIncomingRequest(double T, LinkedList<Request> queueIn, LinkedList<Request> queueOut) {
        queueIn = Timeline.sortRequests(queueIn);
        while (queueIn.size() != 0) {
            Request req = queueIn.remove(); 
            int eap = earliestAvailableProcessor();
            // Processor services request
            Request handledRequest = processors[eap].handleRequest(T, req); 
            if (handledRequest != null) {
                this.completedRequests ++;  
                currentRequest = handledRequest;  
                queueOut = handoffRequest(handledRequest.deepCopy(), queueOut);
            }
        }

        return queueOut; 
    }

    LinkedList<Request> handoffRequest(Request req, LinkedList<Request> queueOut) {
        double prob = Math.random(); 
        timeline.addToTimeline(req.arrEvt);
        timeline.addToTimeline(req.startEvt);
        timeline.addToTimeline(req.fromEvt);
        timeline.addToTimeline(req.doneEvt);

        if (prob > this.P_Termination) {
            ArrEvent arrEvt = new ArrEvent(req.doneEvt.timeStamp, req.requestId, this.serverId, false);
            req.arrEvt = arrEvt; 
            req.startEvt = null; 
            req.fromEvt = null; 
            req.doneEvt = null;
            queueOut.add(req);
        }
        return queueOut; 
    }
}
