// Julia Zeng, BU ID: U48618445
// CS-350 HW 3


public class Request {

    Event arrEvt;
    Event startEvt;
    Event doneEvt; 


    public Request(double arrTime, double startTime, double doneTime, int tag, Server s) {
        this.arrEvt = new Event(s.getArrType(), arrTime, tag, s.serverId);
        this.startEvt = new Event(s.getStartType(), startTime, tag, s.serverId);
        this.doneEvt = new Event(s.getDoneType(), doneTime, tag, s.serverId);
    }

    Request nextRequest(double lambdaA, double lambdaS, int tag, Server s) {
        double arrTime = this.arrEvt.timeStamp + Exp.getExp(lambdaA); 
        double startTime = (arrTime > this.doneEvt.timeStamp) ? arrTime : this.doneEvt.timeStamp; 
        double doneTime = startTime + Exp.getExp(lambdaS); 
        Request req = new Request(arrTime, startTime, doneTime, tag, s); 
        return req; 
    }
}
