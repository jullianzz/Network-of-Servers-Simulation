// Abstract base class Server

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.ArrayList;
import java.util.Iterator;


public abstract class Server {

    // Constructor
    public Server(int serverId, int numProcessors) {
        this.serverId = serverId; 
        this.NUM_PROCESSORS = numProcessors; 
        this.processors = new Processor[NUM_PROCESSORS]; 
        for (int i = 0; i < NUM_PROCESSORS; i++) {
            processors[i] = new Processor(this, i+1); 
        }
        this.completedRequests = 0; 
        this.timeline = new Timeline(); 
        this.currentRequest = Request.dummy(serverId); 
    }
    

    // Meta Data
    int serverId;
    int completedRequests;                  // Number of completed requests seen at the Server
    Request currentRequest;                 // Current request processed at the Server
    Monitor monitor; 

    // Timeline
    Timeline timeline; 

    // Processors – Array of Processors
    int NUM_PROCESSORS; 
    Processor[] processors;

    // Statistics
    double Utilization; 
    double avgPopulation; 
    double avgResponseTime; 
    double runningPopulation;
    double runningResponseTime;
    double runningUtilization; 
    double avgServiceTime; 


    // Methods
    abstract double getServiceTime(); 

    void computeStatistics(double time) {
        // Compute Utilization and avgResponseTime
        runningResponseTime = 0.0; 
        runningUtilization = 0.0;  
        for (int i = 0; i < NUM_PROCESSORS; i++) {
            processors[i].computeStatistics(time);
            runningResponseTime += processors[i].runningResponseTime; 
            runningUtilization += processors[i].runningUtilization; 
        }
        Utilization = runningUtilization / time; 
        avgResponseTime = runningResponseTime / ((double) completedRequests); 

        // Compute avgPopulation
        runningPopulation = 0.0; 
        // Sort the timeline before monitoring population
        timeline.sortChronologically();
        int p = 0; 
        Event evt; 
        for (Iterator<Event> iter = timeline.queue.iterator(); iter.hasNext();) {
            evt = iter.next(); 
            switch (evt.type) {
                case ARR: 
                    p ++;
                    break;
                case DONE: 
                    p --;
                    break;
                case MONITOR:
                    runningPopulation += p; 
                    break;
                default:
                    break; 
            }
        }
        avgPopulation = runningPopulation / ((double) monitor.count); 
        avgServiceTime = runningUtilization / ((double) completedRequests);
    }


    // Call monitorSystem method of each processor
    void monitorSystem(double T, double monitorRate) {
        double lambdaA =  monitorRate; 
        monitor = new Monitor(lambdaA, this.serverId, T);
        while (monitor.monEvt.timeStamp < T) {
            timeline.addToTimeline(monitor.monEvt);
            monitor.setNextMonitor(lambdaA, T); 
        }
    }

    // Selects a processor to serve a request that arrives at arrTime
    int selectProcessor(double arrTime) {
        if (NUM_PROCESSORS == 1) {
            return 0;
        } else {
            int idx = 0; 
            List<Integer> list = new ArrayList<Integer>();
            double earliestDoneTime = processors[0].currentRequest.doneEvt.timeStamp;
            // Iterate through all processors
            for (int i = 0; i < NUM_PROCESSORS; i++) {
                // This if-statement finds the earliest done time among all processors
                if (earliestDoneTime > processors[i].currentRequest.doneEvt.timeStamp) {
                    earliestDoneTime = processors[i].currentRequest.doneEvt.timeStamp; 
                    idx = i; 
                }
                // This if-statement records the available processors at arrTime in a list
                if (arrTime >= processors[i].currentRequest.doneEvt.timeStamp) {
                    list.add(i);
                }
            }
            // If no processor is available at arrTime, select the processor with the earliest doneTime to service the request
            if (list.size() == 0) {
                return idx;
            } 
            // If there are available processors at arrTime, randomly select a processor
            else {
                Random randomizer = new Random();
                Integer random = list.get(randomizer.nextInt(list.size()));
                return random; 
            }
        }
        // double temp = processors[0].currentRequest.doneEvt.timeStamp;
        // int idx = 0; 
        // for (int i = 0; i < NUM_PROCESSORS; i++) {
        //     if (temp > processors[i].currentRequest.doneEvt.timeStamp) {
        //         temp = processors[i].currentRequest.doneEvt.timeStamp; 
        //         idx = i; 
        //     }
        // }
        // return idx;

    }

    // Handle a request that is finished at the primary server
    LinkedList<Request> handleIncomingRequest(double T, LinkedList<Request> queueIn, LinkedList<Request> queueOut) {
        queueIn = Timeline.sortRequests(queueIn);
        while (queueIn.size() != 0) {
            Request req = queueIn.remove(); 
            int eap = selectProcessor(req.arrEvt.timeStamp);

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
        timeline.addToTimeline(req.arrEvt);
        timeline.addToTimeline(req.startEvt);
        timeline.addToTimeline(req.fromEvt);
        timeline.addToTimeline(req.doneEvt);

        ArrEvent arrEvt = new ArrEvent(req.doneEvt.timeStamp, req.requestId, this.serverId, false);
        req.arrEvt = arrEvt; 
        req.startEvt = null; 
        req.fromEvt = null; 
        req.doneEvt = null;
        queueOut.add(req);

        return queueOut; 
    }
}
