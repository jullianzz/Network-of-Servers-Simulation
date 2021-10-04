

public abstract class Server {

    int serverId;
    int requestCount;       //  number of requests at this server has seen
    Timeline timeline; 
    double lambdaS;         // rate of service
    Request currentRequest;

    // abstract void handleIncomingRequest(Event arrEvt); 

    abstract Event.eventType getArrType(); 
    abstract Event.eventType getStartType();
    abstract Event.eventType getDoneType(); 

}
