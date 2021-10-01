// Julia Zeng, BU ID: U48618445
// CS-350 HW 2

import java.util.ArrayList;


public class Monitor {

    // Monitor Constructor
    public Monitor(double avgArrivalRateOfRequests, double avgServiceTimeOfServer, double time) {
        this.monitorFrequency = avgArrivalRateOfRequests;
        this.avgMonitorPeriod = avgServiceTimeOfServer * ((double) this.linguiniFactor);
        this.simulationPeriod = time; 
    }

    // Create an ArrayList where each item is a Watch event
    ArrayList<WatchEvent> arr = new ArrayList<WatchEvent>();

    // Member fields of Monitor
    int linguiniFactor = 3;      
    double monitorFrequency;
    double avgMonitorPeriod; 
    double simulationPeriod;    // Length of simulation in milliseconds 

    // Populate Monitor queue with Watch events
    void generateWatchEvents() {
        double globalClock = 0.0; 
        while (globalClock < simulationPeriod) {
            Exp exp = new Exp();
            double beginWatchTimestamp = globalClock; 
            double watchPeriod = exp.getExp(1.0 / avgMonitorPeriod);         // Length of monitoring for each Watch event
            double finishWatchTimestamp = beginWatchTimestamp + watchPeriod; 
            globalClock = finishWatchTimestamp; 
            if (finishWatchTimestamp <= simulationPeriod) {
                arr.add(new WatchEvent(beginWatchTimestamp, finishWatchTimestamp));
            } 
        }
    }

}


// Notes on Design Choices: 
// The linguiniFactor is the slow-down factor used to compute the duration of the watch period. 
// I chose the name "linguini" because after eating pasta you're gonna want to slow down by taking a nap.
// 1 < linguiniFactor << simulationTime/avgServiceTimeOfServer, with the assumption that avgRateOfService > 1 request per simulationPeriod