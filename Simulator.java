// Julia Zeng, BU ID: U48618445
// CS-350 HW 2


/* Simulator implements a discrete event simulator */ 
public class Simulator {

    public Simulator(Timeline timeline, double lambdaA, double lambdaB) {   // Simulator Constructor
        this.timeline = timeline; 
        this.lambdaA = lambdaA;
        this.lambdaB = lambdaB; 
    }

    Timeline timeline;                      // Timeline queue to store events

    // Parameters of the Simulation
    double lambdaA;                         // rate parameter for arrival rate of requests
    double lambdaB;                         // rate parameter for service time of requests

    // Time-keeping mechanism for the Simulation
    // NOTE: globalClock(*) variables are persistent for the life of the Timeline 
    //       and are monotonically increasing
    double globalClockA = 0.0;              // globalClockA is the arrival time of the request
    double globalClockDeath = 0.0;          // globalClockDeath is the death time of the last request processed by the server. Not necessarily smaller than arrival time
    int totalRequestsCount = 0; 


    // simulate(...) simulates the arrival and execution of requests
    // at a generic server for 'time' milliseconds, where time is passed as a 
    // parameter to the method
    void simulate(double time) {
        this.populateTimeline(time);        // Call populateTimeline(...) member function to generate Events for 'time' milliseconds

        this.timeline.printTimeline();      // Print the simulation output
    }

    void populateTimeline(double T) {
        Exp exp = new Exp();                // Instantiate Exp object to calculate interarrival times and service times

        double interArrivalTime; 
        double serviceTime;
        Event evt;

        // Push Events onto the queue
        while (this.globalClockA < T && this.globalClockDeath < T) {
            // Generate ARR event of request R_n
            interArrivalTime = exp.getExp(this.lambdaA); 
            globalClockA += interArrivalTime; 
            evt = new Event(Event.eventType.ARR, globalClockA, totalRequestsCount);
            timeline.addToTimeline(evt);            

            // Generate the START event of request R_n
            if (globalClockA >= globalClockDeath) {     // Aka, the resource is immediately free
                evt = new Event(Event.eventType.START, globalClockA, totalRequestsCount); 
            } else {                                    // Aka, the resource is not free, and the request R will begin when request R_(n-1) dies
                evt = new Event(Event.eventType.START, globalClockDeath, totalRequestsCount); 
            }
            timeline.addToTimeline(evt);

            // Generate the DONE event of request R_n
            serviceTime = exp.getExp(lambdaB); 
            globalClockDeath = evt.timeStamp + serviceTime;     // evt.timeStamp equals the time of START for request R_n 
            evt = new Event(Event.eventType.DONE, globalClockDeath, totalRequestsCount);
            timeline.addToTimeline(evt);

            // Increment totalRequestsCount
            this.totalRequestsCount += 1; 
        }

        // Sort Timeline queue according to the timeStamp of each Event
        timeline.sortChronologically();
    }
    public static void main(String[] args) {
        
        // Pass in arguments from calling environment
        double time = Double.parseDouble(args[0]);
        double avgArrivalRateOfRequests = Double.parseDouble(args[1]);
        double avgServiceTimeOfServer = Double.parseDouble(args[2]); 

        // Set rate parameters of exponential distribution
        double lambdaA = avgArrivalRateOfRequests;
        double lambdaB = 1.0 / avgServiceTimeOfServer; 

        // Instantiate Event Timeline object
        Timeline timeline = new Timeline();

        // Construct simulator using Timeline object
        Simulator simulator = new Simulator(timeline, lambdaA, lambdaB); 

        // TESTING CODE
        // Event evt = new Event(Event.eventType.START, 2.302096, 2);
        // timeline.addToTimeline(evt);  
        // evt = new Event(Event.eventType.DONE, 2.302096, 1);
        // timeline.addToTimeline(evt);  
        // timeline.sortChronologically();
        // timeline.printTimeline();

        // Invoke simulate(...) to run Simulation
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