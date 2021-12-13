/* Sean Cesario
 * Prince Paul
 * Geetanjali Kanojia
 * 12/7/2021
 * OS - Project 3: Scheduling Simulator
 * File Description - Event class
 */
public class Event implements Comparable<Event>{
    //Define a class or struct to represent an event. This holds only two pieces of info: 
	//event type and event timestamp, the time the event is to occur
    private final String type;
    private final double timeStamp;

    public Event(String type, double timeStamp){
        this.type = type;
        this.timeStamp = timeStamp;
    }

    public String getType() {
        return type;
    }

    public double getTimeStamp() {
        return timeStamp;
    }

    public boolean equals(Event other){
        return this.timeStamp == other.timeStamp;
    }

    public int compareTo(Event other){
        if(this.equals(other))
            return 0;
        else if(this.timeStamp > other.timeStamp)
            return 1;
        else
            return -1;
    }
}