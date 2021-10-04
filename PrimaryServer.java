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
    }

    double lambdaA;                         // Arrival rate of requests
    SecondaryServer secondaryServer; 

    void serverUp(Request firstReq, double T) {
        currentRequest = firstReq;
        this.requestCount ++; 
        while (currentRequest.doneEvt.timeStamp <= T) {
            this.handoffRequest(currentRequest.doneEvt);
            timeline.addToTimeline(currentRequest.arrEvt);
            timeline.addToTimeline(currentRequest.startEvt);
            timeline.addToTimeline(currentRequest.doneEvt);
            currentRequest = currentRequest.nextRequest(lambdaA, lambdaS, requestCount, this);
            this.requestCount ++; 
        }
        // // If the time-bounds of the request are within the period of the Simulation then update System Statistics
        // if (arrTimestamp <= T && doneTimestamp <= T) {
        //     this.totalCompletedR += 1;                                      // Increment the total number of completed requests
        //     runningUtilization += (doneTimestamp - startTimestamp);         // Update total utilization time
        //     runningResponseTime += (doneTimestamp - arrTimestamp);          // Update total response time
        //     runningWaitTime += (startTimestamp - arrTimestamp);             // Update total wait time
        // }
    }

    // Handle a request that is finished at this server
    void handoffRequest(Event doneEvt) {
        secondaryServer.handleIncomingRequest(doneEvt);
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