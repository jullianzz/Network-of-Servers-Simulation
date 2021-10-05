// Julia Zeng, BU ID: U48618445
// CS-350 HW 3


public class Simulator {

    public Simulator(Timeline timeline, double lambdaA, double lambdaB, double lambdaC) {  
        this.timeline = timeline; 
        this.lambdaA = lambdaA;
        this.lambdaB = lambdaB; 
        this.lambdaC = lambdaC; 
        this.SS = new SecondaryServer(lambdaC, lambdaA);    // The arrival rate of requests at SS must be upper-bounded by the arr rate at the PS
        this.PS = new PrimaryServer(SS, lambdaB, lambdaA); 
    }

    PrimaryServer PS;  
    SecondaryServer SS; 
    Timeline timeline;                      // Timeline queue to store events
    double lambdaA;                         // rate parameter for arrival rate of requests
    double lambdaB;                         // rate parameter for service time of Primary Server
    double lambdaC;                         // rate parameter for service time of Secondary Server
        
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

        // Start Primary Server and send the first request. This will generate the timelines for both servers. 
        PS.serverUp(req, time); 
        // Create Monitor Events for the Primary Server
        PS.monitorSystem(time); 
        // Compute Utilization and Average Queue Length for Primary Server
        PS.computeStatistics(time); 
        // Create Monitor Events for the Secondary Server
        PS.secondaryServer.monitorSystem(time); 
        // Compute Utilization and Average Queue Length for Secondary Server
        PS.secondaryServer.computeStatistics(time);

        // Compute the Average Response Time of the Dual-Server System
        this.avgResponseTime = (PS.runningResponseTime + PS.secondaryServer.runningResponseTime) / ((double) PS.requestCount);
        
        // Compute the Average Waiting Time of the Dual-Server System
        this.avgWaitTime = (PS.runningWaitTime + PS.secondaryServer.runningWaitTime) / ((double) PS.requestCount); 

        // Append the timelime of the PS and SS together
        timeline.queue.addAll(PS.timeline.queue); 
        timeline.queue.addAll(PS.secondaryServer.timeline.queue); 

        // Print the timeline of Events for the Dual-Server Simulation
        timeline.printTimeline();

        // Print the Statistics
        this.printStatistics();                                             // Print System Statistics
    }

    void printStatistics() {
        System.out.printf("UTIL 0: %f", PS.Utilization);
        System.out.println();

        System.out.printf("UTIL 1: %f", PS.secondaryServer.Utilization);
        System.out.println();

        System.out.printf("QLEN 0: %f", PS.avgPopulationOfSystem);
        System.out.println();

        System.out.printf("QLEN 1: %f", PS.secondaryServer.avgPopulationOfSystem);
        System.out.println();

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