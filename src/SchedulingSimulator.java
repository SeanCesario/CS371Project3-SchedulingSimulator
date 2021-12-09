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
    private final double ioBoundPct;

    private final Queue<Process> processes = new LinkedList<>();
    private final Queue<Process> readyQueue = new LinkedList<>();
    private final Queue<Process> CPU = new LinkedList<>();
    private final Queue<Process> IOQueue = new LinkedList<>();
    private Queue<Event> eventQueue = new PriorityQueue<>();

    public SchedulingSimulator(double avgCreationTime,double avgIOServiceTime,double avgProcessLength,double quantum,double totalSimulationTime,double contextSwitchTime,double IOBoundPct){
        this.avgCreationTime = (avgCreationTime/1000000.0);
        this.avgIOServiceTime = (avgIOServiceTime/1000000.0);
        this.avgProcessLength = (avgProcessLength/1000000.0);
        this.quantum = (quantum/1000000.0);
        this.totalSimulationTime = totalSimulationTime;
        this.contextSwitchTime = (contextSwitchTime/1000000.0);
        this.ioBoundPct = IOBoundPct;
        this.systemTime = 0;
    }

    public void newProcess(){
        systemTime += exponentialRandom(avgCreationTime);
        readyQueue.add(new Process());
    }

    public void updateCPU(){
        if(CPU.isEmpty()){
            CPU.add(readyQueue.poll());
            if(CPU.peek().getProcessBurst() >= CPU.peek().getProcessLength() || quantum >= CPU.peek().getProcessLength()){
                //terminated
                CPU.peek().setProcessLength(0);
                systemTime += CPU.peek().getProcessLength();
                CPU.remove();
            } else if(CPU.peek().getProcessBurst() > quantum){
                //burst > quantum
                CPU.peek().setProcessLength(CPU.peek().getProcessLength() - CPU.peek().getProcessBurst());
                systemTime += CPU.peek().getProcessBurst();
                readyQueue.add(CPU.poll());
            }
            else{
                //quantum > burst
                CPU.peek().setProcessLength(CPU.peek().getProcessLength() - quantum);
                systemTime += quantum;
                readyQueue.add(CPU.poll());
            }
        }
        else{
            //check for io event?
            readyQueue.add(CPU.poll());
            updateCPU();
        }
    }

    public void run(){
        while(totalSimulationTime > systemTime){
            newProcess();
            updateCPU();
        }
    }

    //add methods here
    double exponentialRandom (double expected) {
        double nice = -expected * Math.log ( Math.random() );
        nice *= 10000;
        nice = (int) nice;
        nice /= 10000;
        return nice;
    }
}
