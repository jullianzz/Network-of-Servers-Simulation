// Julia Zeng, BU ID: U48618445
// CS-350 HW 3

import java.util.LinkedList;
import java.util.Iterator;
import java.lang.Math;

// Types of events for primary server: arr, start, done
public class PrimaryServer extends Server {
    public PrimaryServer(double lambdaS, double lambdaA, double P_exit) {
        this.serverId = 0;
        this.requestCount = 0; 
        this.timeline = new Timeline(); 
        this.lambdaS = lambdaS; 
        this.lambdaA = lambdaA;
        this.serverDown = false; 
        this.runningUtilization = 0.0; 
        this.runningResponseTime = 0.0; 
        this.runningWaitTime = 0.0; 
        this.P_exit = P_exit; 
        this.currentRequest = new Request(0, 0, 0, -1, this, -1);   // Create an empty initial Request that will not be passed to this.timeline
    }

    double lambdaA;                         // Arrival rate of requests
    double runningUtilization;
    double runningResponseTime; 
    double runningWaitTime; 
    double P_exit;


    void monitorSystem(double T) {
        // Create Monitor Events
        Monitor mon = new Monitor(lambdaA, serverId); 
        while (mon.monEvt.timeStamp < T) {
            timeline.addToTimeline(mon.monEvt);
            mon = mon.nextMonitor(lambdaA); 
            if (mon.monEvt.timeStamp > T) {
                break; 
            }
        }
    }


    // Compute the average queue and system population length
    void computeStatistics(double time) {
        timeline.sortChronologically();
        int runningQueueLength = 0; 
        int runningPopulationLength = 0; 
        int monitorCount = 0;
        Event evt; 
        for (Iterator<Event> iter = timeline.queue.iterator(); iter.hasNext();) {
            evt = iter.next(); 
            switch (evt.type) {
                case ARR: 
                    runningQueueLength ++;
                    runningPopulationLength ++;
                    break;
                case START:
                    runningQueueLength --; 
                    break;
                case DONE: 
                    runningPopulationLength --;
                    break;
                case MONITOR:
                    avgQueueLength += runningQueueLength; 
                    avgPopulationOfSystem += runningPopulationLength; 
                    monitorCount ++; 
                    break;
                default:
                    break; 
            }
        }
        Utilization = runningUtilization / time;
        avgQueueLength = avgQueueLength / ((double) monitorCount); 
        avgPopulationOfSystem = avgPopulationOfSystem / ((double) monitorCount); 
    }

    LinkedList<Request> handleIncomingRequest(double T, LinkedList<Request> queueIn, LinkedList<Request> queueOut) {
        while (queueIn.size() != 0) {
            Request req = queueIn.remove(); 
            double arrTime = req.arrEvt.timeStamp;
            double startTime = (arrTime > currentRequest.doneEvt.timeStamp) ? arrTime : currentRequest.doneEvt.timeStamp; 
            double doneTime = startTime + Exp.getExp(lambdaS); 
            int tag = req.arrEvt.requestId;  
            if (doneTime <= T) {
                this.requestCount ++;     
                currentRequest = new Request(arrTime, startTime, doneTime, tag, this, ++req.runCount);
                runningUtilization += (doneTime - startTime);         // Update total utilization time
                runningResponseTime += (doneTime - arrTime);          // Update total response time
                runningWaitTime += (startTime - arrTime);             // Update total wait time
                queueOut = handoffRequest(currentRequest, queueOut);
            } 
        }
        return queueOut; 
    }

    // Handle a request that is finished at this server
    LinkedList<Request> handoffRequest(Request req, LinkedList<Request> queueOut) {
        double prob = Math.random(); 
        req.arrEvt.print = true;
        req.startEvt.print = true; 
        timeline.addToTimeline(req.arrEvt);
        // System.out.printf("%d\n", req.arrEvt.requestId); 
        timeline.addToTimeline(req.startEvt);
        int tag = req.arrEvt.requestId; 
        if (prob > this.P_exit) {   // Don't exit the request
            req.doneEvt.print = false; 
            queueOut.add(new Request(req.doneEvt.timeStamp, -1, -1, tag, this, req.runCount)); 
        } else {    // Exit the request. Request sinks.
            req.doneEvt.print = true; 
            sinkRequest(req);
        }
        timeline.addToTimeline(req.doneEvt);
        return queueOut; 
    }

    Event.eventType getArrType() {
        return Event.eventType.ARR; 
    }

    Event.eventType getStartType() {
        return Event.eventType.START; 
    }

    Event.eventType getDoneType() {
        return Event.eventType.DONE; 
    }
}