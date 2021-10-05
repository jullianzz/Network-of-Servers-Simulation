// Julia Zeng, BU ID: U48618445
// CS-350 HW 3

import java.util.Iterator;

// Note: The secondary server does not generate any requests!!!

public class SecondaryServer extends Server {
    public SecondaryServer(double lambdaS, double lambdaA) {
        this.serverId = 1; 
        this.requestCount = 0; 
        this.timeline = new Timeline(); 
        this.lambdaS = lambdaS; 
        this.currentRequest = new Request(0, 0, 0, -1, this);   // Create an empty initial Request that will not be passed to this.timeline
        this.runningUtilization = 0.0; 
        this.runningResponseTime = 0.0; 
        this.runningWaitTime = 0.0; 
        this.serverDown = false; 
        this.lambdaA = lambdaA;
        this.avgQueueLength = 0.0;
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

    // Handle a request that is finished at the primary server
    void handleIncomingRequest(Event arrEvt, double T) {
        double arrTime = arrEvt.timeStamp; 
        double startTime = (arrTime > currentRequest.doneEvt.timeStamp) ? arrTime : currentRequest.doneEvt.timeStamp;
        double doneTime = startTime + Exp.getExp(lambdaS); 
        if (doneTime <= T) {
            Request req = new Request(arrTime, startTime, doneTime, arrEvt.requestId, this); 
            timeline.addToTimeline(req.arrEvt);
            timeline.addToTimeline(req.startEvt);
            timeline.addToTimeline(req.doneEvt);
            currentRequest = req; 
            this.requestCount ++; 
            runningUtilization += (doneTime - startTime); 
            runningResponseTime += (doneTime - arrTime);          // Update total response time
            runningWaitTime += (startTime - arrTime); 
        } else {
            serverDown = true;  
        }
    }

    void computeStatistics(double time) {
        timeline.sortChronologically();
        int runningQueueLength = 0; 
        int runningPopulationLength = 0; 
        int monitorCount = 0;
        Event evt; 
        for (Iterator<Event> iter = timeline.queue.iterator(); iter.hasNext();) {
            evt = iter.next(); 
            switch (evt.type) {
                // case ARR: 
                //     runningQueueLength ++;
                //     runningPopulationLength ++;
                //     break;
                case START:
                    runningQueueLength --; 
                    break;
                case DONE: 
                    runningPopulationLength --;
                    break;
                case NEXT:
                    runningQueueLength ++;
                    runningPopulationLength ++;
                    break;
                case MONITOR:
                    avgQueueLength += runningQueueLength; 
                    // System.out.println(runningQueueLength); 
                    avgPopulationOfSystem += runningPopulationLength; 
                    // System.out.println(avgQueueLength);
                    monitorCount ++; 
                    break;
                default:
                    break; 
            }
        }
        Utilization = runningUtilization / time;
        avgQueueLength = avgQueueLength / ((double) monitorCount); 
        avgPopulationOfSystem = avgPopulationOfSystem / ((double) monitorCount); 
        // System.out.printf("Server 2 monitor count is %d\n", monitorCount);
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

}