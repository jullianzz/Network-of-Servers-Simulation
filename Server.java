// Julia Zeng, BU ID: U48618445
// CS-350 HW 3

import java.util.LinkedList;

public abstract class Server {

    int serverId;
    int requestCount;                       //  number of COMPLETED requests this server has seen
    Timeline timeline; 
    double lambdaS;                         // rate of service
    Request currentRequest;
    boolean serverDown; 
    int numRequestDeathsSeenAtServer = 0; 
    int runningNumOfServersVisited = 0;   // running sum of number of servers visited across all dead Requests seen at this server. 

    
    double Utilization;
    double avgQueueLength;
    double avgPopulationOfSystem; 

    abstract Event.eventType getArrType(); 
    abstract Event.eventType getStartType();
    abstract Event.eventType getDoneType(); 
    abstract void computeStatistics(double time);
    abstract LinkedList<Request> handleIncomingRequest(double T, LinkedList<Request> queueIn, LinkedList<Request> queueOut);  
    abstract LinkedList<Request> handoffRequest(Request req, LinkedList<Request> queueOut);   


    void sinkRequest(Request req) {         // Handle a request when it terminates at a server. Aka does not get sent to another server. 
        numRequestDeathsSeenAtServer ++; 
        runningNumOfServersVisited += req.runCount; 
    }
}
