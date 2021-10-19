import java.util.LinkedList;
import java.lang.Math;

public class Simulator {

    // Constructor
    public Simulator
    (
        Timeline timeline, 
        double lambdaA, 
        double lambdaB, 
        double lambdaC, 
        double lambdaD, 
        double t1,
        double p1,
        double t2,
        double p2,
        double t3,
        double p3, 
        int K2,
        double p01,
        double p02,
        double p3out,
        double p31,
        double p32
    ) 
    {
        this.timeline = timeline; 
        this.lambdaA = lambdaA;
        this.lambdaB = lambdaB; 
        this.lambdaC = lambdaC; 
        this.lambdaD = lambdaD;
        this.t1 = t1;
        this.p1 = p1;
        this.t2 = t2;
        this.p2 = p2;
        this.t3 = t3;
        this.p3 = p3; 
        this.K2 = K2;
        this.p01 = p01;
        this.p02 = p02;
        this.p3out = p3out;
        this.p31 = p31;  
        this.p32 = p32; 

        // S0 is a single-processor M/M/1 Server with determinate service time
        this.S0 = new DeterminateServer(lambdaB, 0, 1); 
        // S1 is a dual-processor M/M/1 Server with determinate service time                   
        this.S1 = new DeterminateServer(lambdaC, 1, 2); 
        // S2 is a single-processor M/M/K Server with determinate service time
        this.S2 = new MMKServer(lambdaD, 2, 1, K2); 
        // S3 is a single-processor M/M/1 Server with non-determinate service time
        this.S3 = new NondeterminateServer(3, 1, t1, p1, t2, p2, t3, p3); 

    }

    // Class members
    DeterminateServer S0;
    DeterminateServer S1;
    MMKServer S2;
    NondeterminateServer S3; 

    Timeline timeline;                      // Timeline queue to store events
    double lambdaA;                         // Rate parameter of arrival rate of requests
    double lambdaB;                         // Rate parameter of service time of S0
    double lambdaC;                         // Rate parameter of service time of S1
    double lambdaD;                         // Rate parameter of service time of S2
    double t1;
    double p1;
    double t2;
    double p2;
    double t3;
    double p3; 
    double K2;
    double p01;
    double p02;
    double p3out;
    double p31;
    double p32;
    
        
    // System Statistics
    double avgResponseTime; 
    double avgPopulation; 

    // Communication queues between the servers (necessary for decoupling)
    LinkedList<Request> queueSysIn = new LinkedList<Request>();     // Queue for requests entering the System
    LinkedList<Request> queueS0Out = new LinkedList<Request>();     // Queue for requests leaving S0
    LinkedList<Request> queueS1In = new LinkedList<Request>();     // Queue for requests entering S1
    LinkedList<Request> queueS2In = new LinkedList<Request>();      // Queue for requests entering S2 - S2 Queue is FINITE with size K2
    LinkedList<Request> queueS3In = new LinkedList<Request>();     // Queue for requests entering S3
    LinkedList<Request> queueS3Out = new LinkedList<Request>();     // Queue for requests exiting S3

    // simulate(...) simulates the arrival and execution of requests at a generic server for 'time' milliseconds
    void simulate(double time) {
        int requestId = 0;
        ArrEvent arrEvt = new ArrEvent(Exp.getExp(lambdaA), requestId, 0, true); 
        Request req = new Request(requestId, 0, arrEvt, null, null, null); 

        while (req.arrEvt.timeStamp < time) {
            // Add the ARR event to the simulation timeline. Note: This is an important 
            // step, because these arrivals actually get printed unlike the arrEvents in handOffRequest(...) of 
            // the Server class
            timeline.addToTimeline(req.arrEvt); 
            queueSysIn.add(req);
            queueS0Out = S0.handleIncomingRequest(time, queueSysIn, queueS0Out);   
            queueSysIn.clear();  
              
            // Split outgoing requests of S0 between S1 and S2         
            while (queueS0Out.size() != 0) {
                Request r = queueS0Out.remove();
                double prob = Math.random(); 
                if (prob <= p01) {
                    queueS1In.add(r);
                }
                else if (prob > p01 && prob < p01 + p02) {
                    double peekTime = r.arrEvt.timeStamp; 
                    int pop = queueS2In.size() + S2.getCurrentPopulation(peekTime);
                    if (pop < K2) {
                        queueS2In.add(r); 
                    } else {
                        S2.handleRejectedRequest();
                    }
                }
            }
            queueS3In = S1.handleIncomingRequest(time, queueS1In, queueS3In);
            queueS1In.clear();
            queueS3In = S2.handleIncomingRequest(time, queueS2In, queueS3In);
            queueS2In.clear();
            queueS3Out = S3.handleIncomingRequest(time, queueS3In, queueS3Out);
            queueS3In.clear();

            // Split outgoing requests of S3 between S1, S2, and exit
            while (queueS3Out.size() != 0) {
                Request r = queueS3Out.remove(); 
                double prob = Math.random(); 
                if (prob < p31) {
                    queueS1In.add(r);
                }
                else if (prob > p31 && prob < p31 + p32) {
                    double peekTime = r.arrEvt.timeStamp; 
                    int pop = queueS2In.size() + S2.getCurrentPopulation(peekTime);
                    if (pop < K2) {
                        queueS2In.add(r); 
                    } else {
                        S2.handleRejectedRequest();
                    }
                }
            }

            // Generate new request coming from outside the system (with dummy START and DONE times)
            requestId ++; 
            req = req.nextRequest(lambdaA, requestId, S0, true, 0);

        }


        // Create Monitor Events for the Servers
        S0.monitorSystem(time, lambdaA); 
        S1.monitorSystem(time, lambdaA); 
        S2.monitorSystem(time, lambdaA); 
        S3.monitorSystem(time, lambdaA); 

        // Compute Utilization and Average Queue Length for Primary Server
        S0.computeStatistics(time); 
        S1.computeStatistics(time); 
        S2.computeStatistics(time); 
        S3.computeStatistics(time); 

        // Compute the Average Population of the entire System
        double totalMonitors = S0.monitor.count + S1.monitor.count + S2.monitor.count + S3.monitor.count; 
        double runningPopulation = S0.runningPopulation + S1.runningPopulation + S2.runningPopulation + S3.runningPopulation; 
        this.avgPopulation = runningPopulation / totalMonitors; 

        // Compute the Average Response Time of the entire System
        double totalRequests = S0.completedRequests + S1.completedRequests + S2.completedRequests + S3.completedRequests; 
        double runningResponseTime = S0.runningResponseTime + S1.runningResponseTime + S2.runningResponseTime + S3.runningResponseTime; 
        this.avgResponseTime = runningResponseTime / totalRequests;
         
        // Append the timelime of S0, S1, S2, and S3 together
        timeline.queue.addAll(S0.timeline.queue); 
        timeline.queue.addAll(S1.timeline.queue); 
        timeline.queue.addAll(S2.timeline.queue); 
        timeline.queue.addAll(S3.timeline.queue); 

        // Print the timeline of Events for the Network Of Queues
        // timeline.printTimeline();

        System.out.println();

        // Print the Statistics
        this.printStatistics();                                      
    }

    void printStatistics() {
        /* S0 Statistics */
        System.out.printf("S0 UTIL: %f", S0.Utilization);
        System.out.println();
        System.out.printf("S0 QLEN: %f", S0.avgPopulation);
        System.out.println();
        System.out.printf("S0 TRESP: %f", S0.avgResponseTime);
        System.out.println();
        System.out.println();

        /* S1 Statistics */
        System.out.printf("S1,1 UTIL: %f", S1.processors[0].Utilization);
        System.out.println();
        System.out.printf("S1,2 UTIL: %f", S1.processors[1].Utilization);
        System.out.println();
        System.out.printf("S1 QLEN: %f", S1.avgPopulation);
        System.out.println();
        System.out.printf("S1 TRESP: %f", S1.avgResponseTime);
        System.out.println();
        System.out.println();

        /* S2 Statistics */
        System.out.printf("S2 UTIL: %f", S2.Utilization);
        System.out.println();
        System.out.printf("S2 QLEN: %f", S2.avgPopulation);
        System.out.println();
        System.out.printf("S2 TRESP: %f", S2.avgResponseTime);
        System.out.println();
        System.out.printf("S2 DROPPED: %d", S2.numDroppedRequests);        
        System.out.println();
        System.out.println();

        /* S3 Statistics */
        System.out.printf("S3 UTIL: %f", S3.Utilization);
        System.out.println();
        System.out.printf("S3 QLEN: %f", S3.avgPopulation);
        System.out.println();
        System.out.printf("S3 TRESP: %f", S3.avgResponseTime);
        System.out.println();
        System.out.println();

        /* System Statistics */
        System.out.printf("QTOT: %f", avgPopulation);
        System.out.println();
        System.out.printf("TRESP: %f", avgResponseTime);
        System.out.println();
    }


    public static void main(String[] args) {
        
        // Pass arguments from calling environment
        double time = Double.parseDouble(args[0]);
        double avgArrivalRateOfRequests = Double.parseDouble(args[1]);
        double avgServiceTimeOfServer0 = Double.parseDouble(args[2]); 
        double avgServiceTimeOfServer1 = Double.parseDouble(args[3]); 
        double avgServiceTimeOfServer2 = Double.parseDouble(args[4]); 
        double t1 = Double.parseDouble(args[5]); 
        double p1 = Double.parseDouble(args[6]); 
        double t2 = Double.parseDouble(args[7]); 
        double p2 = Double.parseDouble(args[8]); 
        double t3 = Double.parseDouble(args[9]); 
        double p3 = Double.parseDouble(args[10]); 
        int K2 = Integer.parseInt(args[11]);  
        double p01 = Double.parseDouble(args[12]);
        double p02 = Double.parseDouble(args[13]);
        double p3out = Double.parseDouble(args[14]);
        double p31 = Double.parseDouble(args[15]); 
        double p32 = Double.parseDouble(args[16]); 
        
        
        // Set rate parameters of exponential distribution
        double lambdaA = avgArrivalRateOfRequests;
        double lambdaB = 1.0 / avgServiceTimeOfServer0; 
        double lambdaC = 1.0 / avgServiceTimeOfServer1; 
        double lambdaD = 1.0 / avgServiceTimeOfServer2; 

        // Instantiate Timeline object
        Timeline timeline = new Timeline();

        // Construct Simulator
        Simulator simulator = new Simulator(
            timeline, 
            lambdaA, 
            lambdaB, 
            lambdaC, 
            lambdaD, 
            t1,
            p1,
            t2,
            p2,
            t3,
            p3, 
            K2,
            p01,
            p02,
            p3out,
            p31,
            p32
        );

        // Run Simulation
        simulator.simulate(time);   
    }

}