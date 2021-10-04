public class Monitor {
    
    Event monEvt;   // current monitor event

    public Monitor(Event monEvt) {
        this.monEvt = monEvt; 
    }

    public Monitor(double lambdaA, int serverId) {
        double snapshotTime = Exp.getExp(lambdaA); 
        this.monEvt = new Event(Event.eventType.MONITOR, snapshotTime, -1, serverId);   // -1 for requestId
    }

    Monitor nextMonitor(double lambdaA) {
        double snapshotTime = this.monEvt.timeStamp + Exp.getExp(lambdaA);
        return new Monitor(new Event(Event.eventType.MONITOR, snapshotTime, -1, this.monEvt.serverId)); 
    }
}
