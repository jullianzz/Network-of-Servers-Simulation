// Julia Zeng, BU ID: U48618445
// CS-350 HW 3


public class Request {

    Event arrEvt;
    Event startEvt;
    Event doneEvt; 
    Event nextEvt; 
    int runCount;       // runCount is the number of servers visited by this Request

    public Request(double arrTime, double startTime, double doneTime, int tag, Server s, int runCount) {    // Server s is where the request is currently serviced
        this.arrEvt = new Event(s.getArrType(), arrTime, tag, s.serverId);
        this.startEvt = new Event(s.getStartType(), startTime, tag, s.serverId);
        this.doneEvt = new Event(s.getDoneType(), doneTime, tag, s.serverId);
        this.runCount = runCount; 
    }

    Request nextRequest(double lambdaA, double lambdaS, int tag, Server s, int runCount) {
        double arrTime = this.arrEvt.timeStamp + Exp.getExp(lambdaA); 
        double startTime = (arrTime > this.doneEvt.timeStamp) ? arrTime : this.doneEvt.timeStamp; 
        double doneTime = startTime + Exp.getExp(lambdaS); 
        Request req = new Request(arrTime, startTime, doneTime, tag, s, runCount); 
        return req; 
    }

    // This function is for generating next requests with dummy start and done Events
    Request nextRequest(double lambdaA, int tag, Server s, int runCount) {
        double arrTime = this.arrEvt.timeStamp + Exp.getExp(lambdaA); 
        Request req = new Request(arrTime, -1, -1, tag, s, runCount); 
        return req; 
    }
}
