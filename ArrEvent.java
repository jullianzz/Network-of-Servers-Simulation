public class ArrEvent extends Event {

    public ArrEvent(double timeStamp, int tag, int serverId, boolean print) {
        super(Event.eventType.ARR, timeStamp, tag, serverId, print);
    }

    void print() {
        if (print) {
            System.out.printf("R%d ARR: %f", requestId, timeStamp); 
            System.out.println(); 
        }
    }

    ArrEvent deepCopy() {
        return new ArrEvent(this.timeStamp, this.requestId, this.serverId, this.print);
    }
    
}
