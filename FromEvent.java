public class FromEvent extends Event {

    public FromEvent(double timeStamp, int tag, int serverId, boolean print, int nextServerId) {
        super(Event.eventType.FROM, timeStamp, tag, serverId, print);
        this.nextServerId = nextServerId; 
    }

    int nextServerId = -1; 
    
    int getNextServerId() {
        return nextServerId; 
    }

    void print() {
        if (print) {
            System.out.printf("R%d FROM S%d TO S%d: %f", requestId, serverId, nextServerId, timeStamp); 
            System.out.println(); 
        }
    }

    FromEvent deepCopy() {
        return new FromEvent(this.timeStamp, this.requestId, this.serverId, this.print, this.nextServerId);
    }
}
