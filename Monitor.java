public class Monitor {

    public class MonitorEvent extends Event {

        public MonitorEvent(double timeStamp, int tag, int serverId) {
            super(Event.eventType.MONITOR, timeStamp, tag, serverId, false);
        }
    
        void print() {
        }
    }
    
    MonitorEvent monEvt;   // current monitor event

    public Monitor(double lambdaA, int serverId) {
        double snapshotTime = Exp.getExp(lambdaA); 
        this.monEvt = new MonitorEvent(snapshotTime, -1, serverId); 
    }

    void setNextMonitor(double lambdaA) {
        double snapshotTime = monEvt.timeStamp + Exp.getExp(lambdaA);
        this.monEvt = new MonitorEvent(snapshotTime, -1, monEvt.serverId); 
    }
}
