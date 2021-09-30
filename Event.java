// Julia Zeng, BU ID: U48618445
// CS-350 HW 2

public class Event {
    // enumeration goes here 
    public enum eventType { ARR, START, DONE }

    // Event constructor
    public Event(eventType type, double timeStamp, int tag) {
        this.type = type; 
        this.timeStamp = timeStamp; 
        this.requestId = tag; 
    }

    // class member: eventType type;
    eventType type; 

    // class member: double timeStamp;  
    double timeStamp; 

    // class member: int requestId; this is the tag of the request in the
    // order that it arrives at the server
    // e.g. for the first request, requestId = 0.
    int requestId; 

}