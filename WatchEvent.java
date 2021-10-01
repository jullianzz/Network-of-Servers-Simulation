public class WatchEvent {

    // WatchEvent constructor
    public WatchEvent(double beginWatchTimestamp, double finishWatchTimestamp) {
        this.beginWatchTimestamp = beginWatchTimestamp;
        this.finishWatchTimestamp = finishWatchTimestamp; 
    }

    // Watch event metadata
    double beginWatchTimestamp;
    double finishWatchTimestamp; 

    // Identifiers for the system statistics the Monitor measures
    double totalServiceTime = 0.0;        // Aggregate utilization time of the server
    int requestCount = 0;               // Number of requests in the system
    int queueCount = 0;                 // Number of requests in the queue
    double totalResponseTime = 0.0;       // Aggregate response time of requests
    double totalWaitTime = 0.0;           // Aggregate time requests spend in the queue

    void computeMeasurements() {
        // When the WatchEvent expires, compute the system statistics
    }
}