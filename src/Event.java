public class Event {
    //Define a class or struct to represent an event. This holds only two pieces of info: event type and event timestamp, the time the event is to occur
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
}
