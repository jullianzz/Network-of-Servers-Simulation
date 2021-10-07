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

    
    double Utilization;
    double avgQueueLength;
    double avgPopulationOfSystem; 

    abstract Event.eventType getArrType(); 
    abstract Event.eventType getStartType();
    abstract Event.eventType getDoneType(); 
    abstract void computeStatistics(double time);
    abstract LinkedList<Request> handleIncomingRequest(double T, LinkedList<Request> queueIn, LinkedList<Request> queueOut);  
    abstract LinkedList<Request> handoffRequest(Request req, LinkedList<Request> queueOut);   

}
