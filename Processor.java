import java.util.Iterator;

public class Processor {

    // Constructor
    public Processor(double lambdaA, double P_Termination, Server s, int processorId) {
        this.lambdaA = lambdaA; 
        this.P_Termination = P_Termination; 
        this.parentServer = s; 
        this.currentRequest = Request.dummy(s.serverId);
        this.completedRequests = 0; 
        this.timeline = new Timeline(); 
        this.processorId = processorId; 
    }
    

    // Meta Data
    int completedRequests;                  // Number of completed requests seen by the processor
    double lambdaA;                         // Rate of arrival of requests at the parent Server
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
    double runningWaitTime; 
    int runningQueueLength; 
    int runningPopulation; 
    int monitorCount; 


    // Create Monitor Events
    void monitorSystem(double T) {
        monitorCount = 0; 
        Monitor monitor = new Monitor(lambdaA, parentServer.serverId);
        // MonitorEvent monEvt = new MonitorEvent(double timeStamp, int tag, int serverId); 
        while (monitor.monEvt.timeStamp < T) {
            timeline.addToTimeline(monitor.monEvt);
            monitorCount ++;
            monitor.setNextMonitor(lambdaA); 
            if (monitor.monEvt.timeStamp > T) {
                break; 
            }
        }
    }

    // Receive a req and use the arrEvt field to update the req start, done, and from events
    Request handleRequest(double T, Request req) {
        int previousServerId = req.arrEvt.serverId; 
        int Id = req.requestId; 
        double arrTime = req.arrEvt.timeStamp;
        double startTime = (arrTime > currentRequest.doneEvt.timeStamp) ? arrTime : currentRequest.doneEvt.timeStamp; 
        double doneTime = startTime + Exp.getExp(parentServer.getlambdaS());
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
            runningWaitTime += (startTime - arrTime);             // Update total wait time

            return temp; 
        } 

        else {
            return null; 
        }
    }

    void computeStatistics(double time) {
        // Compute Utilization of Processor
        Utilization = runningUtilization / time;

        // Track Queue Population Length and Queue Length 
        timeline.sortChronologically();
        int q = 0; 
        int p = 0; 
        Event evt; 
        for (Iterator<Event> iter = timeline.queue.iterator(); iter.hasNext();) {
            evt = iter.next(); 
            switch (evt.type) {
                case FROM: 
                    q ++;
                    p ++;
                    break;
                case START:
                    q --; 
                    break;
                case DONE: 
                    p --;
                    break;
                case MONITOR:
                    runningQueueLength += q; 
                    runningPopulation += p; 
                    monitorCount ++; 
                    break;
                default:
                    break; 
            }
        }
    }     
}
