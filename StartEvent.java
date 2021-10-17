public class StartEvent extends Event {
    
    // Constructor 1
    public StartEvent(double timeStamp, int tag, int serverId, boolean print, int processorId, Server s) {
        super(Event.eventType.START, timeStamp, tag, serverId, print); 
        this.processorId = processorId; 
        this.s = s; 
    }

    // Constructor 2
    public StartEvent(double timeStamp, int tag, int serverId, boolean print) {
        super(Event.eventType.START, timeStamp, tag, serverId, print); 
    }

    int processorId = -1; 
    Server s;

    void print() {
        if (print) {
            switch (s.NUM_PROCESSORS) {
                case 1: // The is no need to print the processorId
                    System.out.printf("R%d START S%d: %f", requestId, serverId, timeStamp); 
                    System.out.println();
                    break; 
                default:
                    System.out.printf("R%d START S%d,%d: %f", requestId, serverId, processorId, timeStamp); 
                    System.out.println();
                    break; 
            }
        }
    }

    StartEvent deepCopy() {
        return new StartEvent(this.timeStamp, this.requestId, this.serverId, this.print, this.processorId, this.s);
    }
}
