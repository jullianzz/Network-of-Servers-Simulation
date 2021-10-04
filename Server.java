

public abstract class Server {

    int serverId;
    int requestCount;       //  number of COMPLETED requests this server has seen
    Timeline timeline; 
    double lambdaS;         // rate of service
    Request currentRequest;
    double Utilization; 
    double avgQueueLength; 
    boolean serverDown; 

    // abstract void handleIncomingRequest(Event arrEvt); 

    abstract Event.eventType getArrType(); 
    abstract Event.eventType getStartType();
    abstract Event.eventType getDoneType(); 

}
