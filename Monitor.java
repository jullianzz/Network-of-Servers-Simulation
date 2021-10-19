public class Monitor {

    public class MonitorEvent extends Event {

        public MonitorEvent(double timeStamp, int tag, int serverId) {
            super(Event.eventType.MONITOR, timeStamp, tag, serverId, false);
        }
    
        void print() {
        }
    }

    int count = 0; 
    
    MonitorEvent monEvt;   // current monitor event

    public Monitor(double lambdaA, int serverId, double time) {
        double snapshotTime = Exp.getExp(lambdaA); 
        this.monEvt = new MonitorEvent(snapshotTime, -1, serverId); 
        if (monEvt.timeStamp < time) {
            count ++; 
        }
    }

    void setNextMonitor(double lambdaA, double time) {
        double snapshotTime = monEvt.timeStamp + Exp.getExp(lambdaA);
        this.monEvt = new MonitorEvent(snapshotTime, -1, monEvt.serverId); 
        if (monEvt.timeStamp < time) {
            count ++; 
        }
    }
}
