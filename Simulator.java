// Julia Zeng, BU ID: U48618445
// CS-350 HW 3

import java.util.LinkedList;

public class Simulator {

    public Simulator(Timeline timeline, double lambdaA, double lambdaB, double lambdaC, double P_exit, double P_rerequest) {  
        this.timeline = timeline; 
        this.lambdaA = lambdaA;
        this.lambdaB = lambdaB; 
        this.lambdaC = lambdaC; 
        this.PS = new PrimaryServer(lambdaB, lambdaA, P_exit);
        this.SS = new SecondaryServer(lambdaC, lambdaA, P_rerequest);  
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

    // Communication queues between the servers (necessary for decoupling)
    LinkedList<Request> queue0 = new LinkedList<Request>();
    LinkedList<Request> queue1 = new LinkedList<Request>(); 


    // simulate(...) simulates the arrival and execution of requests at a generic server for 'time' milliseconds
    void simulate(double time) {
        // Create first request
        int requestId = 0;
        double arrTime = Exp.getExp(lambdaA); 
        double arrTimeQu; 
        Request youngestReq = new Request(arrTime, -1, -1, requestId, PS, 0);
        // Add first request to Server 0 incoming queue
        queue0.add(youngestReq); 
        while (youngestReq.arrEvt.timeStamp < time) {
            // Handle incoming and outgoing requests for PS
            queue1 = PS.handleIncomingRequest(time, queue0, queue1);

            // Clear queue0 after all of its requests have been handled by PS
            queue0.clear();

            // System.out.printf("%d\n", queue1.size());    // should be all 1s for P(drop) = 0 
            // Handle incoming and outgoing requests for SS
            queue0 = SS.handleIncomingRequest(time, queue1, queue0);

            // Clear queue1 after all of its requests have been handled by SS
            queue1.clear();

            // System.out.printf("%d\n", queue0.size());   // should be all 0s for P(rerequest) = 0
            // Generate new request with ARR non-empty timestamp only. Don't care about the request ID for now
            if (queue0.size() != 0) {
                arrTime = youngestReq.arrEvt.timeStamp + Exp.getExp(lambdaA); 
                arrTimeQu = queue0.peek().arrEvt.timeStamp;
                queue0.clear();
                if (arrTime < arrTimeQu) {     // Rebuild next request with just the requestId incremented
                    requestId ++; 
                    queue0.add(new Request(arrTime, -1, -1, requestId, PS, 0));
                    requestId ++; 
                    queue0.add(new Request(arrTimeQu, -1, -1, requestId, PS, 0));
                    youngestReq = queue0.get(1); 
                }
                else {
                    requestId ++; 
                    queue0.add(new Request(arrTimeQu, -1, -1, requestId, PS, 0));
                    requestId ++; 
                    queue0.add(new Request(arrTime, -1, -1, requestId, PS, 0));
                    youngestReq = queue0.get(1); 
                }
            } else {
                requestId ++; 
                youngestReq = youngestReq.nextRequest(lambdaA, requestId, PS, 0);
                queue0.add(youngestReq);
            }
            // System.out.printf("%d\n", queue0.size());
        }


        // Create Monitor Events for the Primary Server
        PS.monitorSystem(time); 
        // Compute Utilization and Average Queue Length for Primary Server
        PS.computeStatistics(time); 
        // Create Monitor Events for the Secondary Server
        SS.monitorSystem(time); 
        // Compute Utilization and Average Queue Length for Secondary Server
        SS.computeStatistics(time);

        // Compute the Average Response Time of the Dual-Server System
        this.avgResponseTime = (PS.runningResponseTime + SS.runningResponseTime) / ((double) PS.requestCount);
        
        // Compute the Average Waiting Time of the Dual-Server System
        this.avgWaitTime = (PS.runningWaitTime + SS.runningWaitTime) / ((double) PS.requestCount); 

        // Append the timelime of the PS and SS together
        timeline.queue.addAll(PS.timeline.queue); 
        timeline.queue.addAll(SS.timeline.queue); 

        // Print the timeline of Events for the Dual-Server Simulation
        timeline.printTimeline();

        // Print the Statistics
        this.printStatistics();                                             // Print System Statistics
    }

    void printStatistics() {
        System.out.printf("UTIL 0: %f", PS.Utilization);
        System.out.println();

        System.out.printf("UTIL 1: %f", SS.Utilization);
        System.out.println();

        System.out.printf("QLEN 0: %f", PS.avgPopulationOfSystem);
        System.out.println();

        System.out.printf("QLEN 1: %f", SS.avgPopulationOfSystem);
        System.out.println();

        System.out.printf("TRESP: %f", this.avgResponseTime);
        System.out.println();

        System.out.printf("TWAIT: %f", this.avgWaitTime);
        System.out.println();

        // System.out.printf("RUNS: ", );        // Number of servers visited by a request
        // System.out.println();
    }


    public static void main(String[] args) {
        
        // Pass arguments from calling environment
        double time = Double.parseDouble(args[0]);
        double avgArrivalRateOfRequests = Double.parseDouble(args[1]);
        double avgServiceTimeOfPrimaryServer = Double.parseDouble(args[2]); 
        double avgServiceTimeOfSecondaryServer = Double.parseDouble(args[3]); 
        double P_exit = Double.parseDouble(args[4]);        // Probability of exiting after server 0
        double P_rerequest = Double.parseDouble(args[5]);   // Probability of re-requesting back to server 0 after exiting server 1

        // Set rate parameters of exponential distribution
        double lambdaA = avgArrivalRateOfRequests;
        double lambdaB = 1.0 / avgServiceTimeOfPrimaryServer; 
        double lambdaC = 1.0 / avgServiceTimeOfSecondaryServer; 

        // Instantiate Timeline object
        Timeline timeline = new Timeline();

        // Construct Simulator
        Simulator simulator = new Simulator(timeline, lambdaA, lambdaB, lambdaC, P_exit, P_rerequest);

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