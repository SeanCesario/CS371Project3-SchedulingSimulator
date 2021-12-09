import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

public class SchedulingSimulator {
    private double systemTime;
    private final double avgCreationTime;
    private final double avgIOServiceTime;
    private final double avgProcessLength;
    private final double quantum;
    private final double totalSimulationTime;
    private final double contextSwitchTime;
    private final int ioBoundPct;

    private final Queue<Process> readyQueue = new LinkedList<>();
    private final Queue<Process> CPU = new LinkedList<>();
    private Queue<Event> eventQueue = new PriorityQueue<>();

    public SchedulingSimulator(double avgCreationTime,double avgIOServiceTime,double avgProcessLength,double quantum,double totalSimulationTime,double contextSwitchTime,int IOBoundPct){
        this.avgCreationTime = (avgCreationTime/1000000.0);
        this.avgIOServiceTime = (avgIOServiceTime/1000000.0);
        this.avgProcessLength = (avgProcessLength/1000000.0);
        this.quantum = (quantum/1000000.0);
        this.totalSimulationTime = totalSimulationTime;
        this.contextSwitchTime = (contextSwitchTime/1000000.0);
        this.ioBoundPct = IOBoundPct;
        this.systemTime = 0;
    }

    public void updateCPU(){
        if(!CPU.isEmpty()){
            if(CPU.peek().getCurrentCPUBurstTimeRemaining() >= CPU.peek().getTotalCPUTime() && quantum >= CPU.peek().getTotalCPUTime()){
                //total time less than or equal to both quantum and burst time
                eventQueue.add(new Event("t", systemTime + CPU.peek().getTotalCPUTime()));
            } else if(CPU.peek().getCurrentCPUBurstTimeRemaining() > quantum){
                //burst > quantum = I/O event
                eventQueue.add(new Event("i", systemTime + CPU.peek().getCurrentCPUBurstTimeRemaining()));
            }
            else{
                //quantum > burst
                eventQueue.add(new Event("q", systemTime + quantum));
            }
        }
        else{
            //cpu bound event
            eventQueue.add(new Event("c", systemTime));
        }
    }

    public void run(){
        int p = 0;
        eventQueue.add(new Event("n", 0));
        while(totalSimulationTime > systemTime){
            Event cEvent = eventQueue.peek();
            switch(cEvent.getType()) {
                case ("n"):
                    //creates new process and set next new event, updates cpu if empty
                    Process np = new Process(p, avgCreationTime, avgProcessLength, ioBoundPct);
                    eventQueue.add(new Event("n", np.getNextNewProcessCreationTime()));
                    readyQueue.add(np);
                    updateCPU();
                    p++;
                    break;
                case("c"):
                    //cpu bound
                    CPU.add(readyQueue.poll());
                    updateCPU();
                    break;
                case("q"):
                    //quantum hit and sent back to ready queue
                    readyQueue.add(CPU.poll());
                    updateCPU();
                    break;
                case("i"):
                    //io service start, end time set
                    double cIOserviceTime = 0.0;
                    cIOserviceTime = exponentialRandom(avgIOServiceTime);
                    eventQueue.add(new Event("r", systemTime + cIOserviceTime));
                    break;
                case("r"):
                    //io service done
                    System.out.println("IO Done");
                    readyQueue.add(CPU.poll());
                    updateCPU();
                    break;
                case("t"):
                    //log final data and remove it
                    CPU.remove();
                    break;
            }
        }
    }

    //add methods here
    double exponentialRandom (double expected) {
        double nice = -expected * Math.log (Math.random());
        nice *= 10000;
        nice = (int) nice;
        nice /= 10000;
        return nice;
    }
}
