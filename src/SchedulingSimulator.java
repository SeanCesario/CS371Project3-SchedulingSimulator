import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

public class SchedulingSimulator {
    private double systemTime;
    private final double avgCreationTime; //used to tell when next event will be created
    private final double avgIOServiceTime; //used to give i/o time
    private final double avgProcessLength; //used when creating process
    private final double quantum;
    private final double totalSimulationTime;
    private final double contextSwitchTime; //constant
    private final int ioBoundPct;

    private final Queue<Process> readyQueue = new LinkedList<>();
    private final Queue<Process> CPU = new LinkedList<>();
    private final Queue<Process> IO = new LinkedList<>();
    private final PriorityQueue<Event> eventQueue = new PriorityQueue<>();

    public SchedulingSimulator(double avgCreationTime,double avgIOServiceTime,double avgProcessLength,double quantum,double totalSimulationTime,double contextSwitchTime,int IOBoundPct){
        this.avgCreationTime = (avgCreationTime/1000000.0);
        this.avgIOServiceTime = (avgIOServiceTime/1000.0);
        this.avgProcessLength = (avgProcessLength/1000000.0);
        this.quantum = (quantum/1000000.0);
        this.totalSimulationTime = totalSimulationTime;
        this.contextSwitchTime = (contextSwitchTime/1000000.0);
        this.ioBoundPct = IOBoundPct;
        this.systemTime = 0;
    }

    public void updateCPU(){
        System.out.println("Update CPU called CPU is " + CPU.isEmpty());
        if(!CPU.isEmpty()){
            System.out.print("Current burst time remaining: " + CPU.peek().getCurrentCPUBurstTimeRemaining() + " total CPU time remaining: " + CPU.peek().getCPUTimeRemaining() + " quantum: " + quantum + " so it ");
            if((CPU.peek().getCPUTimeRemaining() - CPU.peek().getCurrentCPUBurstTimeRemaining() <= 0) && (CPU.peek().getCPUTimeRemaining() - quantum <= 0)){
                //total time less than or equal to both quantum and burst time
                System.out.println("terminates at: " + (contextSwitchTime + systemTime + CPU.peek().getCurrentCPUBurstTimeRemaining()));
                eventQueue.add(new Event("terminate", (contextSwitchTime + systemTime + CPU.peek().getTotalCPUTime())));
            } else if(CPU.peek().getCurrentCPUBurstTimeRemaining() < quantum){
                //burst > quantum = I/O event
                System.out.println("IOs until: " + (contextSwitchTime + systemTime + CPU.peek().getCurrentCPUBurstTimeRemaining()));
                eventQueue.add(new Event("ioBound", (contextSwitchTime + systemTime + CPU.peek().getCurrentCPUBurstTimeRemaining())));
            } else{
                //quantum > burst = back to ready queue
                System.out.println("quantums until: " + (contextSwitchTime + systemTime + quantum));
                eventQueue.add(new Event("quantum", (contextSwitchTime + systemTime + quantum)));
            }
        } else{
            //if empty create cpu bound event
            eventQueue.add(new Event("cpuBound", + systemTime));
        }
    }

    public void run(){
        int pID = 1;
        eventQueue.add(new Event("new", 0));
        while(totalSimulationTime > eventQueue.peek().getTimeStamp()){
            for (Event e: eventQueue)
                System.out.println("Type: " + e.getType() + " Time: " + e.getTimeStamp());
            Event cEvent = eventQueue.remove();
            System.out.println("Current event: " + cEvent.getType() + " time: " + cEvent.getTimeStamp());
            systemTime = cEvent.getTimeStamp();
            switch(cEvent.getType()) {
                case ("new"):
                    //creates new process and set next new event, updates cpu if empty
                    Process np = new Process(pID, avgCreationTime, avgProcessLength, ioBoundPct); //create new process

                    System.out.println("Process " + pID + " created with length of " + np.getTotalCPUTime() + " next process at " + (systemTime + np.getNextNewProcessCreationTime()));

                    pID++; //iterate id for next process
                    eventQueue.add(new Event("new", (np.getNextNewProcessCreationTime() + systemTime))); //make event for next process
                    readyQueue.add(np); //add new process to ready queue
                    if(CPU.isEmpty())
                        updateCPU(); //run cpu method only if it is empty, since there might be a process in there and we don't want duplicate events
                    break;
                case("cpuBound"):
                    //cpu bound
                    System.out.println("Process " + readyQueue.peek().getPid() + " cpuBound at " + systemTime);
                    CPU.add(readyQueue.remove()); //move top of ready to CPU
                    updateCPU(); //decide what to do with current process in CPU
                    break;
                case("quantum"):
                    //quantum hit and sent back to ready queue
                    System.out.println("Process " + CPU.peek().getPid() + " ready bound at  " + systemTime);

                    CPU.peek().setCPUTimeRemaining(CPU.peek().getCPUTimeRemaining() - quantum); //decrease time remaining by quantum
                    CPU.peek().setCPUTimeSpent(CPU.peek().getCPUTimeSpent() + quantum); //add quantum to cpu time spent
                    if(CPU.peek().getCurrentCPUBurstTimeRemaining() - quantum <= 0) //if the current burst will expire from the quantum then make a new burst length
                        CPU.peek().generateNewBurstLength();
                    else
                        CPU.peek().setCurrentCPUBurstTimeRemaining(CPU.peek().getCurrentCPUBurstTimeRemaining() - quantum); //decrease current burst time remaining quantum
                    readyQueue.add(CPU.remove()); //remove from CPU and put back into ready queue
                    updateCPU(); //update CPU as it is empty and there is at least this process in the ready queue
                    break;
                case("ioBound"):
                    //io service start, end time set, current bust subtracted, total time subtracted
                    CPU.peek().setCPUTimeRemaining(CPU.peek().getCPUTimeRemaining() - CPU.peek().getCurrentCPUBurstTimeRemaining()); //burst time finished so subtract the time just spent from total cpu time
                    CPU.peek().setCPUTimeSpent(CPU.peek().getCPUTimeSpent() + CPU.peek().getCurrentCPUBurstTimeRemaining()); //add remaining burst to cpu time spent
                    CPU.peek().generateNewBurstLength(); //Burst is at 0 so create a new burst
                    double cIOserviceTime = exponentialRandom(avgIOServiceTime); //create new random io service time
                    cIOserviceTime /= 1000.0; //convert into seconds
                    CPU.peek().setIoTimeSpent(CPU.peek().getIoTimeSpent() + cIOserviceTime); //add the io time to io time spent
                    CPU.peek().setNumOfIORequests(CPU.peek().getNumOfIORequests() + 1); //increment io request count
                    System.out.println("Process " + CPU.peek().getPid() + " IO bound at  " + systemTime + " for " +  cIOserviceTime + " until " + (cIOserviceTime + systemTime));

                    IO.add(CPU.remove()); //move process from the CPU to the IO
                    eventQueue.add(new Event("ioEnd", systemTime + cIOserviceTime)); //create the event for how long it will take
                    break;
                case("ioEnd"):
                    //io service done
                    System.out.println("Process " + IO.peek().getPid() + " IO finished, ready bound at " + systemTime);

                    readyQueue.add(IO.remove());
                    break;
                case("terminate"):
                    //log final data and remove it
                    System.out.println("Process " + CPU.peek().getPid() + " terminated at " + systemTime);

                    CPU.peek().setCPUTimeSpent(CPU.peek().getCPUTimeSpent() + CPU.peek().getCPUTimeRemaining()); //add remaining time to the time spent in the CPU
                    CPU.peek().setCPUTimeRemaining(0); //set time remaining to 0
                    CPU.remove(); //remove it
                    updateCPU();
                    break;
            }
        }
        System.out.println("Final " + totalSimulationTime + " > " + eventQueue.peek().getTimeStamp());
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
