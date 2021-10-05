// Julia Zeng, BU ID: U48618445
// CS-350 HW 3

public abstract class Server {

    int serverId;
    int requestCount;                       //  number of COMPLETED requests this server has seen
    Timeline timeline; 
    double lambdaS;                         // rate of service
    Request currentRequest;
    boolean serverDown; 

    // Statistics Class memeber
    // Statistics statistics = new Statistics(); 
    double Utilization;
    double avgQueueLength;
    double avgPopulationOfSystem; 

    abstract Event.eventType getArrType(); 
    abstract Event.eventType getStartType();
    abstract Event.eventType getDoneType(); 
    abstract void computeStatistics(double time);

}
