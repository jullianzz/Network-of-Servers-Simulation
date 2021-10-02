// Julia Zeng, BU ID: U48618445
// CS-350 HW 2

import java.util.LinkedList;

/* Simulator implements a discrete event simulator */ 
public class Simulator {

    // Simulator Constructor
    public Simulator(Timeline timeline, double lambdaA, double lambdaB) {  
        this.timeline = timeline; 
        this.lambdaA = lambdaA;
        this.lambdaB = lambdaB; 
    }

    Timeline timeline;                      // Timeline queue to store events
    double lambdaA;                         // rate parameter for arrival rate of requests
    double lambdaB;                         // rate parameter for service time of requests
    double globalClockA = 0.0;              // globalClockA is used for time-keeping of the arrival time of the request
    double globalClockDeath = 0.0;          // globalClockDeath is used for time-keeping of the death time of the last request processed by the server
    double globalClockMonitor = 0.0;        // globalClockMonitor is used for time-keeping of the Monitor events of the Simulation
    int totalCompletedR = 0;                // total number of completed requests, i.e. requests that have an associated DONE event by the end of the Simulation    
    
    LinkedList<Event> startQueue = new LinkedList<Event>();    // Queue of items waiting to Start
    
    // System Statistics
    double avgUtilization; 
    double avgResponseTime; 
    double avgWaitTime; 
    double avgQueueLength; 
    double avgPopulationOfSystem; 

    int monitorCount = 1;                   // Note: monitorCount (and thus, the requestID) starts at 1

    // simulate(...) simulates the arrival and execution of requests at a generic server for 'time' milliseconds
    void simulate(double time) {
        this.populateTimeline(time);                                        // Creates Events for a period of 'time' milliseconds and computes avgUtilization, avgResponseTime, and avgWaitTime of the System
        this.timeline.iterateTimeline();                                    // Print the ARR, START, and DONE Events of the Simulation and computes avgQueueLength and avgPopulationOfSystem of the System
        avgQueueLength = this.timeline.avgQueueLength;                      // Get avgQueueLength from the Timeline
        avgPopulationOfSystem = this.timeline.avgPopulationOfSystem;        // Get avgPopulationOfSystem from the Timeline
        System.out.println();
        this.printStatistics();                                             // Print System Statistics
    }

    void populateTimeline(double T) {
        Exp exp = new Exp();                // Instantiate Exp to calculate interarrival and service times
        double runningUtilization = 0.0;
        double runningResponseTime = 0.0;
        double runningWaitTime = 0.0; 
        double interArrivalTime, serviceTime;
        double startTimestamp, doneTimestamp, arrTimestamp; 
        int requestId = 0; 
        Event evt;

        while (this.globalClockA < T && this.globalClockDeath < T) {
            // Create ARR event of request R_n
            interArrivalTime = exp.getExp(this.lambdaA); 
            globalClockA += interArrivalTime; 
            evt = new Event(Event.eventType.ARR, globalClockA, requestId);
            timeline.addToTimeline(evt);     
            arrTimestamp = evt.timeStamp;        

            // Create the START event of request R_n
            if (globalClockA >= globalClockDeath) {                                 // The resource will service the request at arrival time
                evt = new Event(Event.eventType.START, globalClockA, requestId); 
            } else {                                                                // The resource is not free, and the request R will begin when request R_(n-1) is done
                evt = new Event(Event.eventType.START, globalClockDeath, requestId); 
                startQueue.add(evt); 
            }
            timeline.addToTimeline(evt);
            startTimestamp = evt.timeStamp;

            // Create the DONE event of request R_n
            serviceTime = exp.getExp(lambdaB); 
            globalClockDeath = startTimestamp + serviceTime;   
            evt = new Event(Event.eventType.DONE, globalClockDeath, requestId);
            timeline.addToTimeline(evt);
            doneTimestamp = evt.timeStamp; 
            
            // Update request ID
            requestId += 1; 

            // Create MONITOR event
            interArrivalTime = exp.getExp(this.lambdaA); 
            globalClockMonitor += interArrivalTime; 
            if (globalClockMonitor <= T) {
                evt = new Event(Event.eventType.MONITOR, globalClockMonitor, this.monitorCount); 
                timeline.addToTimeline(evt);
                this.monitorCount += 1; 
            }

            // If the time-bounds of the request are within the period of the Simulation then update System Statistics
            if (arrTimestamp <= T && doneTimestamp <= T) {
                this.totalCompletedR += 1;                                      // Increment the total number of completed requests
                runningUtilization += (doneTimestamp - startTimestamp);         // Update total utilization time
                runningResponseTime += (doneTimestamp - arrTimestamp);          // Update total response time
                runningWaitTime += (startTimestamp - arrTimestamp);             // Update total wait time
            }
            
        }

        timeline.sortChronologically();             // Sort Timeline queue according to the timeStamp of each Event

        // Determine avgUtilization, avgResponseTime, and avgWaitTime of the System
        avgUtilization = runningUtilization / T; 
        avgResponseTime = runningResponseTime / ((double) this.totalCompletedR); 
        avgWaitTime = runningWaitTime / ((double) this.totalCompletedR); 
    }

    void printStatistics() {
        System.out.printf("UTIL: %f",this.avgUtilization);
        System.out.println();

        System.out.printf("QLEN: %f", this.avgPopulationOfSystem);
        System.out.println();

        System.out.printf("WLEN: %f", this.avgQueueLength);
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
        double avgServiceTimeOfServer = Double.parseDouble(args[2]); 

        // Set rate parameters of exponential distribution
        double lambdaA = avgArrivalRateOfRequests;
        double lambdaB = 1.0 / avgServiceTimeOfServer; 

        // Instantiate Timeline object
        Timeline timeline = new Timeline();

        // Construct Simulator
        Simulator simulator = new Simulator(timeline, lambdaA, lambdaB);

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