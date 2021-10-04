// Julia Zeng, BU ID: U48618445
// CS-350 HW 3

public class Event {

    // NULL events are not passed to the timeline and are used to denote 
    // dummy Request objects
    public enum eventType { ARR, START, DONE, MONITOR, NEXT, NULL }

    public Event(eventType type, double timeStamp, int tag, int serverId) {
        this.type = type; 
        this.timeStamp = timeStamp; 
        this.requestId = tag; 
        this.serverId = serverId; 
    }

    eventType type; 

    double timeStamp; 

    // class member: int requestId; this is the tag of the request in the
    // order that it arrives at the server. e.g. for the first request, requestId = 0.
    int requestId; 

    int serverId; 

}


/* My notes:
Each server has a START X and DONE X
Primary Server Events: ARR, START 0, DONE 0/NEXT 1
Secondary Server Events: NEXT 1, START 1, DONE 1
*/