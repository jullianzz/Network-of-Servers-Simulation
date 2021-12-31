public class DoneEvent extends Event {
    
    public DoneEvent(double timeStamp, int tag, int serverId, boolean print) {
        super(Event.eventType.DONE, timeStamp, tag, serverId, print);
    }

    void print() {
        if (print) {
            System.out.printf("R%d DONE S%d: %f", requestId, serverId, timeStamp); 
            System.out.println(); 
        }
    }

    DoneEvent deepCopy() {
        return new DoneEvent(this.timeStamp, this.requestId, this.serverId, this.print);
    }
}
