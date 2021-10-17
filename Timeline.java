
import java.util.LinkedList;
import java.util.Iterator;
import java.util.Collections;
import java.util.Comparator;

public class Timeline {

    LinkedList<Event> queue = new LinkedList<Event>();

    // addToTimeline(...) adds the Event to the queue
    void addToTimeline(Event evtToAdd) {
        if (evtToAdd != null) {
            queue.add(evtToAdd);
        }
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
                    (a.timeStamp < b.timeStamp) ? -1 : (
                        (a.requestId < b.requestId) ? -1 : (
                            (a.requestId > b.requestId) ? 1 : (
                                (a.type == Event.eventType.DONE) ? -1 : (
                                    (b.type == Event.eventType.DONE) ? 1 : (
                                        (a.type == Event.eventType.START && b.type == Event.eventType.FROM) ? 1 : (
                                            (a.type == Event.eventType.FROM && b.type == Event.eventType.START) ? -1 : 0
                                        )
                                    )
                                )
                            )
                        )
                    )
                ); // 1
            }
        }
        );
    }


    void printTimeline() {
        sortChronologically();
        Event evt; 
        for (Iterator<Event> iter = queue.iterator(); iter.hasNext();) {
            evt = iter.next(); 
            evt.print(); 
        }
    }

    static LinkedList<Request> sortRequests(LinkedList<Request> queue) {
        Collections.sort(queue, 
        new Comparator<Request>() {
            @Override
            public int compare(Request a, Request b) {
                return 
                (a.arrEvt.timeStamp >= b.arrEvt.timeStamp) ? 1 : ( 
                    (a.arrEvt.timeStamp < b.arrEvt.timeStamp) ? -1 : 0
                ); 
            }
        }
        );
        
        return queue; 
    }

}

