
public abstract class Event {

    public enum eventType { ARR, START, DONE, MONITOR, NEXT, FROM, NULL }

    public Event(eventType type, double timeStamp, int tag, int serverId) {
        this.type = type; 
        this.timeStamp = timeStamp; 
        this.requestId = tag; 
        this.serverId = serverId; 
    }

    public Event(eventType type, double timeStamp, int tag, int serverId, boolean print) {
        this.type = type; 
        this.timeStamp = timeStamp; 
        this.requestId = tag; 
        this.serverId = serverId; 
        this.print = print;
    }

    eventType type; 

    double timeStamp; 

    int requestId; 

    int serverId; 

    boolean print;     // If true, print this event in Timeline

    abstract void print(); 

    // abstract void deepCopy(); 

}