// Julia Zeng, BU ID: U48618445
// CS-350 HW 3


// Note: The secondary server does not generate any requests!!!

public class SecondaryServer extends Server {
    public SecondaryServer(double lambdaS) {
        this.serverId = 1; 
        this.requestCount = 0; 
        this.timeline = new Timeline(); 
        this.lambdaS = lambdaS; 
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

    // Handle a request that is finished at the primary server
    void handleIncomingRequest(Event arrEvt) {
        double arrTime = arrEvt.timeStamp; 
        double startTime = (arrTime > currentRequest.doneEvt.timeStamp) ? arrTime : currentRequest.doneEvt.timeStamp;
        double doneTime = startTime + Exp.getExp(lambdaS); 
        Request req = new Request(arrTime, startTime, doneTime, arrEvt.requestId, this); 
        timeline.addToTimeline(req.arrEvt);
        timeline.addToTimeline(req.startEvt);
        timeline.addToTimeline(req.doneEvt);
        currentRequest = req; 
        this.requestCount ++; 

        // // If the time-bounds of the request are within the period of the Simulation then update System Statistics
        // if (arrTimestamp <= T && doneTimestamp <= T) {
        //     this.totalCompletedR += 1;                                      // Increment the total number of completed requests
        //     runningUtilization += (doneTimestamp - startTimestamp);         // Update total utilization time
        //     runningResponseTime += (doneTimestamp - arrTimestamp);          // Update total response time
        //     runningWaitTime += (startTimestamp - arrTimestamp);             // Update total wait time
        // }
    }
}