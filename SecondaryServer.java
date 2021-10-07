// Julia Zeng, BU ID: U48618445
// CS-350 HW 3

import java.util.Iterator;
import java.lang.Math; 
import java.util.LinkedList;

// Note: The secondary server does not generate any requests!!!

public class SecondaryServer extends Server {
    public SecondaryServer(double lambdaS, double lambdaA, double P_rerequest) {
        this.serverId = 1; 
        this.requestCount = 0; 
        this.timeline = new Timeline(); 
        this.lambdaS = lambdaS; 
        this.currentRequest = new Request(0, 0, 0, -1, this, -1);   // Create an empty initial Request that will not be passed to this.timeline
        this.runningUtilization = 0.0; 
        this.runningResponseTime = 0.0; 
        this.runningWaitTime = 0.0; 
        this.serverDown = false; 
        this.lambdaA = lambdaA;
        this.avgQueueLength = 0.0;
        this.P_rerequest = P_rerequest; 
    }

    Event.eventType getArrType() {
        return Event.eventType.NEXT; 
    }

    Event.eventType getStartType() {
        return Event.eventType.START; 
    }

    Event.eventType getDoneType() {
        return Event.eventType.DONE; 
    }

    double lambdaA; 
    double runningUtilization; 
    double runningResponseTime; 
    double runningWaitTime; 
    double P_rerequest; 
    PrimaryServer primaryServer; 

    // Handle a request that is finished at the primary server
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

    // Types for the Secondary Server: ARR, START, DONE/NEXT
    void computeStatistics(double time) {
        timeline.sortChronologically();
        int runningQueueLength = 0; 
        int runningPopulationLength = 0; 
        int monitorCount = 0;
        Event evt; 
        for (Iterator<Event> iter = timeline.queue.iterator(); iter.hasNext();) {
            evt = iter.next(); 
            switch (evt.type) {
                case NEXT: 
                    if (evt.serverId == 1) {    // This is the ARR event for the Secondary Server
                        runningQueueLength ++;
                        runningPopulationLength ++;
                    }
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


    void monitorSystem(double T) {
        // Create Monitor Events
        Monitor mon = new Monitor(lambdaA, serverId);   // what is lambdaA for the monitor?? Ans: Ohhh, it must be upper bounded by the arrival rate of the Primary Server
        while (mon.monEvt.timeStamp < T) {
            timeline.addToTimeline(mon.monEvt);
            mon = mon.nextMonitor(lambdaA); 
            if (mon.monEvt.timeStamp > T) {
                break; 
            }
        }
    }

    LinkedList<Request> handoffRequest(Request req, LinkedList<Request> queueOut) {
        double prob = Math.random(); 
        req.arrEvt.print = true;
        req.startEvt.print = true;
        timeline.addToTimeline(req.arrEvt);
        timeline.addToTimeline(req.startEvt);
        // int tag = req.arrEvt.requestId; 
        if (prob <= this.P_rerequest) {     // Re-request, aka send to Server 0
            req.doneEvt.print = false;
            timeline.addToTimeline(new Event(Event.eventType.NEXT, req.doneEvt.timeStamp, req.doneEvt.requestId, 0, true)); 
            queueOut.add(new Request(req.doneEvt.timeStamp, -1, -1, -1, this, req.runCount)); 
        } else {    // else, don't handoff to primary server
            req.doneEvt.print = true;
        }
        timeline.addToTimeline(req.doneEvt);
        return queueOut; 
    }

    // public static void main(String[] args) {
    //     double T = Double.parseDouble(args[0]); 
    //     double lambdaA = Double.parseDouble(args[1]);
    //     double lambdaS = Double.parseDouble(args[2]); 
    //     SecondaryServer ss = new SecondaryServer(lambdaS, lambdaA); 
    //     ss.monitorSystem(T); 
    //     ss.timeline.printTimeline();
    // }

}