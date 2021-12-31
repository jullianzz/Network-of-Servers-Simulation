
public class Processor {

    // Constructor
    public Processor(double P_Termination, Server s, int processorId) {
        // this.lambdaA = lambdaA; 
        this.P_Termination = P_Termination; 
        this.parentServer = s; 
        this.currentRequest = Request.dummy(s.serverId);
        this.completedRequests = 0; 
        this.timeline = new Timeline(); 
        this.processorId = processorId; 

        this.Utilization = 0;
        this.runningUtilization = 0; 
        this.runningResponseTime = 0; 
        // this.runningWaitTime = 0;
    }
    

    // Meta Data
    int completedRequests;                  // Number of completed requests seen by the processor
    Request currentRequest;                 // Current request processed at the processor
    double P_Termination;                   // Probability a request terminates at the parent Server
    int processorId;

    // Timeline
    Timeline timeline; 

    // Server – the server the processor belongs to
    Server parentServer;                          

    // Statistics
    double Utilization;
    double runningUtilization; 
    double runningResponseTime; 
    // double runningWaitTime; 
    // int monitorCount; 


    // Receive a req and use the arrEvt field to update the req start, done, and from events
    Request handleRequest(double T, Request req) {
        int previousServerId = req.arrEvt.serverId; 
        int Id = req.requestId; 
        double arrTime = req.arrEvt.timeStamp;
        double startTime = (arrTime > currentRequest.doneEvt.timeStamp) ? arrTime : currentRequest.doneEvt.timeStamp; 
        double doneTime = startTime + parentServer.getServiceTime();
        Request temp; 
        if (doneTime <= T) {
            this.completedRequests ++;   
            ArrEvent arrEvt = new ArrEvent(arrTime, Id, parentServer.serverId, false); 
            StartEvent startEvt = new StartEvent(startTime, Id, parentServer.serverId, true, this.processorId, parentServer);
            DoneEvent doneEvt = new DoneEvent(doneTime, Id, parentServer.serverId, true);
            FromEvent fromEvt = null;
            // Note: not every Request has a FROM event
            if (previousServerId != parentServer.serverId) {
                fromEvt = new FromEvent(arrTime, Id, previousServerId, true, parentServer.serverId); 
            } 
            temp = new Request(Id, ++req.serversVisited, arrEvt, startEvt, doneEvt, fromEvt); 
            currentRequest = new Request(Id, ++req.serversVisited, arrEvt, startEvt, doneEvt, fromEvt); 
            
            runningUtilization += (doneTime - startTime);         // Update total utilization time
            runningResponseTime += (doneTime - arrTime);          // Update total response time
            // runningWaitTime += (startTime - arrTime);             // Update total wait time

            return temp; 
        } 

        else {
            return null; 
        }
    }

    void computeStatistics(double time) {
        // Compute Utilization of Processor
        Utilization = runningUtilization / time;
    }     
}
