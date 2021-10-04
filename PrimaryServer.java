// Julia Zeng, BU ID: U48618445
// CS-350 HW 3


public class PrimaryServer extends Server {
    public PrimaryServer(SecondaryServer secondaryServer, double lambdaS, double lambdaA) {
        this.serverId = 0;
        this.requestCount = 0; 
        this.timeline = new Timeline(); 
        this.lambdaS = lambdaS; 
        this.secondaryServer = secondaryServer; 
        this.lambdaA = lambdaA;
        this.serverDown = false; 
        this.runningUtilization = 0.0; 
        this.runningResponseTime = 0.0; 
        this.runningWaitTime = 0.0; 
    }

    double lambdaA;                         // Arrival rate of requests
    SecondaryServer secondaryServer; 
    double runningUtilization;
    double runningResponseTime; 
    double runningWaitTime; 


    void serverUp(Request firstReq, double T) {
        currentRequest = firstReq;
        double arrTime; 
        double startTime;
        double doneTime; 
        while (currentRequest.doneEvt.timeStamp <= T && !secondaryServer.serverDown) {
            this.requestCount ++;                                           // Increment the total number of completed requests
            timeline.addToTimeline(currentRequest.arrEvt);
            timeline.addToTimeline(currentRequest.startEvt);
            // timeline.addToTimeline(currentRequest.doneEvt);
            arrTime = currentRequest.arrEvt.timeStamp; 
            startTime = currentRequest.startEvt.timeStamp; 
            doneTime = currentRequest.doneEvt.timeStamp; 
            runningUtilization += (doneTime - startTime);         // Update total utilization time
            runningResponseTime += (doneTime - arrTime);          // Update total response time
            runningWaitTime += (startTime - arrTime);             // Update total wait time
            this.handoffRequest(currentRequest.doneEvt, T);
            currentRequest = currentRequest.nextRequest(lambdaA, lambdaS, requestCount, this);
        }
        serverDown = true; 
    }

    void monitorSystem(double T) {
        // Compute Utilization for Primary Server
        this.Utilization = runningUtilization / ((double) requestCount);
        // Create Monitor Events
        // Monitor mon = new Monitor(lambdaA, serverId); 
        // while (mon.monEvt.timeStamp < T) {
        //     timeline.addToTimeline(mon.monEvt);
        //     mon = mon.nextMonitor(lambdaA); 
        //     if (mon.monEvt.timeStamp < T) {
        //         break; 
        //     }
        // }

        // timeline.iterateTimeline();
        // this.avgQueueLength = timeline.avgQueueLength;  
    }

    // Handle a request that is finished at this server
    void handoffRequest(Event doneEvt, double T) {
        secondaryServer.handleIncomingRequest(doneEvt, T);
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