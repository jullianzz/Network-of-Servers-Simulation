// Julia Zeng, BU ID: U48618445
// CS-350 HW 3


public class Simulator {

    public Simulator(Timeline timeline, double lambdaA, double lambdaB, double lambdaC) {  
        this.timeline = timeline; 
        this.lambdaA = lambdaA;
        this.lambdaB = lambdaB; 
        this.lambdaC = lambdaC; 
        this.SS = new SecondaryServer(lambdaC);
        this.PS = new PrimaryServer(SS, lambdaB, lambdaA); 
    }

    PrimaryServer PS;  
    SecondaryServer SS; 
    Timeline timeline;                      // Timeline queue to store events
    double lambdaA;                         // rate parameter for arrival rate of requests
    double lambdaB;                         // rate parameter for service time of Primary Server
    double lambdaC;                         // rate parameter for service time of Secondary Server
    int totalCompletedR = 0;                // total number of completed requests, i.e. requests that have an associated DONE event by the end of the Simulation    
        
    // System Statistics
    double avgUtilization; 
    double avgResponseTime; 
    double avgWaitTime; 
    double avgQueueLength; 
    double avgPopulationOfSystem; 

    int monitorCount = 1;                   // Note: monitorCount (and thus, the requestID) starts at 1

    // simulate(...) simulates the arrival and execution of requests at a generic server for 'time' milliseconds
    void simulate(double time) {
        // Create an initial request to send to Primary Server
        double arrTime = Exp.getExp(lambdaA);       // arrTime = arrival time of first request
        double startTime = arrTime;
        double doneTime = startTime + Exp.getExp(lambdaB); 
        Request req = new Request(arrTime, arrTime, doneTime, 0, this.PS);
        PS.serverUp(req, time); 
        PS.monitorSystem(time); 
        PS.secondaryServer.monitorSystem(time); 
        this.avgResponseTime = (PS.runningResponseTime + PS.secondaryServer.runningResponseTime) / ((double) PS.requestCount);
        this.avgWaitTime = (PS.runningWaitTime + PS.secondaryServer.runningWaitTime) / ((double) PS.requestCount); 
        //append PS and SS timelines together to make the Simulator Timeline
        timeline.queue.addAll(PS.timeline.queue); 
        timeline.queue.addAll(PS.secondaryServer.timeline.queue); 
        this.timeline.sortChronologically();
        timeline.iterateTimeline();
        // avgQueueLength = this.timeline.avgQueueLength;                      // Get avgQueueLength from the Timeline
        // avgPopulationOfSystem = this.timeline.avgPopulationOfSystem;        // Get avgPopulationOfSystem from the Timeline
        this.printStatistics();                                             // Print System Statistics
    }

    void printStatistics() {
        System.out.printf("UTIL 0: %f", PS.Utilization);
        System.out.println();

        System.out.printf("UTIL 1: %f", PS.secondaryServer.Utilization);
        System.out.println();

        // System.out.printf("QLEN 0: %f", PS.avgQueueLength); // average queue length
        // System.out.println();

        // System.out.printf("QLEN 1: %f", 0.0001);
        // System.out.println();

        System.out.printf("TRESP: %f", this.avgResponseTime);
        System.out.println();

        System.out.printf("TWAIT: %f", this.avgWaitTime);
        System.out.println();
    }


    public static void main(String[] args) {
        
        // Pass arguments from calling environment
        double time = Double.parseDouble(args[0]);
        double avgArrivalRateOfRequests = Double.parseDouble(args[1]);
        double avgServiceTimeOfPrimaryServer = Double.parseDouble(args[2]); 
        double avgServiceTimeOfSecondaryServer = Double.parseDouble(args[3]); 

        // Set rate parameters of exponential distribution
        double lambdaA = avgArrivalRateOfRequests;
        double lambdaB = 1.0 / avgServiceTimeOfPrimaryServer; 
        double lambdaC = 1.0 / avgServiceTimeOfSecondaryServer; 

        // Instantiate Timeline object
        Timeline timeline = new Timeline();

        // Construct Simulator
        Simulator simulator = new Simulator(timeline, lambdaA, lambdaB, lambdaC);

        // Invoke simulate(...) and run Simulation
        simulator.simulate(time);   
    }

}


/* my notes: 
I think a request STARTS immediately when the resource is available.
e.g. the start time for R0 is = arrival time of R0,
the start time for R1 = the DONE time of R0.
So, when a request finishes, we can immediately set the START time of the next request (IF there is one!)
EDIT: This is not necessarily true. Need to use death time instead --> If there is no request in the queue, the START time would be the ARRIVAL time of the next request. 

Arrival time is computed with, and only dependent on, average arrival rate.
Start time has to be kept track of
*/