// Julia Zeng, BU ID: U48618445
// CS-350 HW 3


// Note: The secondary server does not generate any requests!!!

public class SecondaryServer extends Server {
    public SecondaryServer(double lambdaS) {
        this.serverId = 1; 
        this.requestCount = 0; 
        this.timeline = new Timeline(); 
        this.lambdaS = lambdaS; 
        this.currentRequest = new Request(0, 0, 0, -1, this);   // Create an empty initial Request that will not be passed to this.timeline
        this.runningUtilization = 0.0; 
        this.runningResponseTime = 0.0; 
        this.runningWaitTime = 0.0; 
        this.serverDown = false; 
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
            // System.out.printf("Done time minus strt time is is %f", doneTime-startTime);
            runningResponseTime += (doneTime - arrTime);          // Update total response time
            runningWaitTime += (startTime - arrTime); 
        } else {
            serverDown = true;  
        }


        // // If the time-bounds of the request are within the period of the Simulation then update System Statistics
        // if (arrTimestamp <= T && doneTimestamp <= T) {
        //     this.totalCompletedR += 1;                                      // Increment the total number of completed requests
        //     runningUtilization += (doneTimestamp - startTimestamp);         // Update total utilization time
        //     runningResponseTime += (doneTimestamp - arrTimestamp);          // Update total response time
        //     runningWaitTime += (startTimestamp - arrTimestamp);             // Update total wait time
        // }
    }


void monitorSystem(double T) {
    Utilization = runningUtilization / ((double) requestCount);

    // // Create Monitor Events
    // Monitor mon = new Monitor(lambdaA, serverId);   // what is lambdaA for the monitor?? Probably covered this in class
    // while (mon.monEvt.timeStamp < T) {
    //     timeline.addToTimeline(mon.monEvt);
    //     mon = mon.nextMonitor(lambdaA); 
    //     if (mon.monEvt.timeStamp < T) {
    //         break; 
    //     }
    // }
}

}