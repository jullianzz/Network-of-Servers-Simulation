// Julia Zeng, BU ID: U48618445
// CS-350 HW 3

import java.util.LinkedList;
import java.util.Iterator;
import java.util.Collections;
import java.util.Comparator;

public class Timeline {

    LinkedList<Event> queue = new LinkedList<Event>();  // Implement the Timeline queue using LinkedList

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
                return 
                (a.timeStamp > b.timeStamp) ? 1 : ( // 1
                    (a.timeStamp < b.timeStamp) ? -1 : ( //2 
                        (a.type == Event.eventType.START && (b.type == Event.eventType.NEXT || b.type == Event.eventType.DONE)) ? 1 : ( // 3
                            ((a.type == Event.eventType.DONE || a.type == Event.eventType.NEXT) && b.type == Event.eventType.START) ? -1 : ( // 4
                                (a.type == Event.eventType.START && b.type == Event.eventType.START && a.requestId < b.requestId) ? -1 : 0
                            ) // 4
                        ) // 3
                    ) //2
                ); // 1
            }
        }
        );
    }


    // Scan through queue and print all events in their chronological order
    // Pop Event from queue and print the eventType and timeStamp
    void printTimeline() {
        sortChronologically();
        Event evt; 
        for (Iterator<Event> iter = queue.iterator(); iter.hasNext();) {
            evt = iter.next(); 
            switch (evt.type) {
                case ARR: 
                    System.out.printf("%s%d %s: %f", "R", evt.requestId, evt.type.toString(), evt.timeStamp); 
                    System.out.println(); 
                    break;
                case START:
                    System.out.printf("%s%d %s %d: %f", "R", evt.requestId, evt.type.toString(), evt.serverId, evt.timeStamp); 
                    System.out.println(); 
                    break;
                case DONE: 
                    if (evt.print) {
                        System.out.printf("%s%d %s %d: %f", "R", evt.requestId, evt.type.toString(), evt.serverId, evt.timeStamp); 
                        System.out.println(); 
                    }
                    break;
                case NEXT:
                    System.out.printf("%s%d %s %d: %f", "R", evt.requestId, evt.type.toString(), evt.serverId, evt.timeStamp); 
                    System.out.println(); 
                    break;
                case MONITOR:
                    break;
                default:
                    break; 
            }
        }
    }

}

