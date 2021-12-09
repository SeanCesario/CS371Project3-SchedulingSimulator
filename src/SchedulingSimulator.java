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
                eventQueue.add(new Event("terminate", systemTime + CPU.peek().getTotalCPUTime()));
            } else if(CPU.peek().getCurrentCPUBurstTimeRemaining() > quantum){
                //burst > quantum = I/O event
                eventQueue.add(new Event("ioBound", systemTime + CPU.peek().getCurrentCPUBurstTimeRemaining()));
            }
            else{
                //quantum > burst
                eventQueue.add(new Event("quantum", systemTime + quantum));
            }
        }
        else{
            //cpu bound event
            eventQueue.add(new Event("cpuBound", systemTime));
        }
    }

    public void run(){
        int pID = 0;
        eventQueue.add(new Event("n", 0));
        while(totalSimulationTime > systemTime){
            Event cEvent = eventQueue.peek();
            switch(cEvent.getType()) {
                case ("new"):
                    //creates new process and set next new event, updates cpu if empty
                    Process np = new Process(pID, avgCreationTime, avgProcessLength, ioBoundPct);
                    eventQueue.add(new Event("n", np.getNextNewProcessCreationTime()));
                    readyQueue.add(np);
                    updateCPU();
                    pID++;
                    break;
                case("cpuBound"):
                    //cpu bound
                    CPU.add(readyQueue.poll());
                    updateCPU();
                    break;
                case("quantum"):
                    //quantum hit and sent back to ready queue
                    readyQueue.add(CPU.poll());
                    updateCPU();
                    break;
                case("ioBound"):
                    //io service start, end time set
                    double cIOserviceTime = 0.0;
                    cIOserviceTime = exponentialRandom(avgIOServiceTime);
                    eventQueue.add(new Event("ioEnd", systemTime + cIOserviceTime));
                    break;
                case("ioEnd"):
                    //io service done
                    readyQueue.add(CPU.poll());
                    updateCPU();
                    break;
                case("terminate"):
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
