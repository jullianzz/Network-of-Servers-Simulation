// Julia Zeng, BU ID: U48618445
// CS-350 HW 2

import java.util.LinkedList;
import java.util.Collections;
import java.util.Comparator;

public class Timeline {

    // Timeline Constructor
    // public Timeline() {
    // }

    // implementation of timeline queue (FIFO) using Linked List
    LinkedList<Event> queue = new LinkedList<Event>();

    // accepts two pieces of information that add the event input into the Timeline
    void addToTimeline(Event evtToAdd) {
        queue.add(evtToAdd);
    }

    // popNext() removes the oldest event from the timeline
    Event popNext() {
        Event evt = queue.peek(); 
        queue.remove();
        return evt; 
    }

    void sortChronologically() {
        // Sort Timeline queue according to the timeStamp of each Event
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
    void printTimeline() {
        // Scan through queue and print all events in their chronological order
        // Pop Event from queue and print the eventType and timeStamp
        Event event; 
        while (this.queue.size() != 0) {
            event = this.popNext(); 
            System.out.printf("%s%d %s: %f", "R", event.requestId, event.type.toString(), event.timeStamp); 
            System.out.println(); 
        }
    }

}

