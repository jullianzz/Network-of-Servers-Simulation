

public class Request {

    ArrEvent arrEvt;
    StartEvent startEvt;
    DoneEvent doneEvt; 
    FromEvent fromEvt; 
    int serversVisited;       // serversVisited is the number of servers visited by this Request
    int requestId; 


    public Request(int requestId, int serversVisited, ArrEvent arrEvt, StartEvent startEvt, DoneEvent doneEvt, FromEvent fromEvt) 
    {
        this.requestId = requestId; 
        this.serversVisited = serversVisited; 
        this.arrEvt = arrEvt; 
        this.startEvt = startEvt;
        this.doneEvt = doneEvt;
        this.fromEvt = fromEvt; 
        this.serversVisited = 0; 
    }

    static Request dummy(int serverId) {
        ArrEvent arrEvt = new ArrEvent(0, -1, serverId, false); 
        DoneEvent doneEvt = new DoneEvent(0, -1, serverId, false); 
        StartEvent startEvt = new StartEvent(0, -1, serverId, false); 
        return new Request(-1, 0, arrEvt, startEvt, doneEvt, null);
    }

    // This function is for generating next requests with dummy start and done Events
    Request nextRequest(double lambdaA, int requestId, Server s, boolean print, int serversVisited) {
        double arrTime = this.arrEvt.timeStamp + Exp.getExp(lambdaA); 
        ArrEvent arrEvt = new ArrEvent(arrTime, requestId, s.serverId, print);
        Request req = new Request(requestId, serversVisited, arrEvt, null, null, null);
        return req; 
    }

    public Request deepCopy() {
        ArrEvent ae = null;
        StartEvent se = null;
        DoneEvent de = null;
        FromEvent fe = null;
        if (arrEvt != null) {
            ae = arrEvt.deepCopy();
        }
        if (startEvt != null) { 
            se = startEvt.deepCopy();
        }
        if (doneEvt != null) {
            de = doneEvt.deepCopy();
        }
        if (fromEvt != null) {
            fe = fromEvt.deepCopy(); 
        }
        return new Request(this.requestId, this.serversVisited, ae, se, de, fe);
    }
}
