// Julia Zeng, BU ID: U48618445
// CS-350 HW 2

import java.util.LinkedList;
import java.util.Collections;
import java.util.Comparator;

public class Timeline {

    LinkedList<Event> queue = new LinkedList<Event>();  // Implement the Timeline queue using LinkedList

    double avgQueueLength;              // Average queue length of the queue taken at different intervals of time specified by the Monitor events in the queue
    double avgPopulationOfSystem;       // Average population size of the queue taken at different intervals of time specified by the Monitor events in the queue


    // addToTimeline(...) adds the Event to the queue
    void addToTimeline(Event evtToAdd) {
        queue.add(evtToAdd);
    }

    // popNext() removes and returns the oldest Event from the queue 
    Event popNext() {
        Event evt = queue.remove();
        return evt; 
    }

    // sortChronologically() sorts the queue according to the timeStamp of each Event
    void sortChronologically() {
        Collections.sort(queue, 
        new Comparator<Event>() {
            @Override
            public int compare(Event a, Event b) {
                return (a.timeStamp > b.timeStamp) ? 1 : 
                       ((a.timeStamp < b.timeStamp) ? -1 : 
                       ((a.type == Event.eventType.START && b.type == Event.eventType.DONE) ? 1 :
                       ((a.type == Event.eventType.DONE && b.type == Event.eventType.START) ? -1 : 0
                       )));
            }
        }
        );
    }

    // Todo : Understand that this method will remove timeline elements. May need refactoring
    // in future assignments to use peek() method instead
    void iterateTimeline() {
        // Scan through queue and print all events in their chronological order
        // Pop Event from queue and print the eventType and timeStamp
        int runningQueueLength = 0; 
        int runningPopulationLength = 0; 
        int monitorCount = 0;
        Event event; 
        while (this.queue.size() != 0) {
            event = this.popNext(); 
            if (event.type != Event.eventType.MONITOR) {
                switch (event.type) {
                    case ARR: 
                        runningQueueLength ++;
                        runningPopulationLength ++;
                        break;
                    case START:
                        runningQueueLength --; 
                        break;
                    case DONE: 
                        runningPopulationLength --;
                        break;
                    default:
                        break; 
                }
                System.out.printf("%s%d %s: %f", "R", event.requestId, event.type.toString(), event.timeStamp); 
                System.out.println(); 
            } else {
                avgQueueLength += runningQueueLength; 
                avgPopulationOfSystem += runningPopulationLength; 
                monitorCount ++; 
            }
        }
        this.avgQueueLength = ((double) avgQueueLength) / ((double) monitorCount); 
        this.avgPopulationOfSystem = ((double) avgPopulationOfSystem) / ((double) monitorCount); 
    }

}

