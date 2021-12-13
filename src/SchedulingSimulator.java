/* Sean Cesario
 * Prince Paul
 * Geetanjali Kanojia
 * 12/7/2021
 * OS - Project 3: Scheduling Simulator
 * File Description - Simulates the CPU scheduler for processes
 */
import java.text.DecimalFormat;
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
    private DecimalFormat dfLong=new DecimalFormat("#,###,##0.000000");
	private DecimalFormat dfShort=new DecimalFormat("#,###,##0.000");
	private DecimalFormat dfVeryShort=new DecimalFormat("#,###,##0.00");
    private int numOfProcessesCreated = 0;
    private int numOfCPUBoundProcessesFinished = 0;
    private int numOfIOBoundProcessesFinished = 0;
    private double totalCPUBoundFinishedProcessesTurnaroundTime = 0.0;
    private double totalIOBoundFinishedProcessesTurnaroundTime = 0.0;
    private double totalNumOfIOCallsForCPUBoundProcesses = 0.0;
    private double totalNumOfIOCallsForIOBoundProcesses = 0.0;
    private double totalIOServiceTimeForCPUBoundProcesses = 0.0;
    private double totalIOServiceTimeForIOBoundProcesses = 0.0;
    private double totalReadyWaitingTimeForCPUBoundProcesses = 0.0;
    private double totalReadyWaitingTimeForIOBoundProcesses = 0.0;
    private double totalTotalCPUTimeForCPUBoundProcesses = 0.0;
    private double totalTotalCPUTimeForIOBoundProcesses = 0.0;
    private double totalCPUTimeSpentForAllCreatedProcesses = 0.0;

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
    	if(!CPU.isEmpty()){
			if (CPU.peek().getCurrentCPUBurstTimeRemaining() < quantum && CPU.peek().getCurrentCPUBurstTimeRemaining() > CPU.peek().getCPUTimeRemaining()) {
    			//burst > quantum = I/O event
    			totalCPUTimeSpentForAllCreatedProcesses += CPU.peek().getCPUTimeSpent();
    			eventQueue.add(new Event("ioBound", (contextSwitchTime + systemTime + CPU.peek().getCurrentCPUBurstTimeRemaining())));
    		} else if (quantum < CPU.peek().getCPUTimeRemaining()) {
    			//quantum > burst = back to ready queue
    			totalCPUTimeSpentForAllCreatedProcesses += CPU.peek().getCPUTimeSpent();
    			eventQueue.add(new Event("quantum", (contextSwitchTime + systemTime + quantum)));
    		} else {
    			//total time less than or equal to both quantum and burst time
    			totalCPUTimeSpentForAllCreatedProcesses += CPU.peek().getCPUTimeSpent();
    			eventQueue.add(new Event("terminate", (contextSwitchTime + systemTime + CPU.peek().getTotalCPUTime())));
    		}
    		
        }
        else{
            //if empty create cpu bound event
            eventQueue.add(new Event("cpuBound", + systemTime));
        }
    }

    public void run() {
        // SCHEDULING SIMULATION
        int pID = 1;
        eventQueue.add(new Event("new", 0));
        while(totalSimulationTime > eventQueue.peek().getTimeStamp()){
            Event cEvent = eventQueue.remove();
            systemTime = cEvent.getTimeStamp();
            switch(cEvent.getType()) {
                case ("new"):
                	numOfProcessesCreated++;
                	Process np = new Process(pID, avgCreationTime, avgProcessLength, ioBoundPct); //create new process
                    //creates new process and set next new event, updates cpu if empty
                	if (np.isCpuBound()) {
                		System.out.println("Time   " + dfLong.format(systemTime) + " Event 'New Process': pid=" + np.getPid() 
                		+ " totalCPU=" + dfShort.format(np.getTotalCPUTime()) + " CPU-bound; next New at   " + dfLong.format(systemTime + np.getNextNewProcessCreationTime()));
                	} else if (np.isIoBound()) {
                		System.out.println("Time   " + dfLong.format(systemTime) + " Event 'New Process': pid=" + np.getPid() 
                		+ " totalCPU=" + dfShort.format(np.getTotalCPUTime()) + " I/O-bound; next New at   " + dfLong.format(systemTime + np.getNextNewProcessCreationTime()));
                	}
                	
                    pID++; //iterate id for next process
                    eventQueue.add(new Event("new", np.getNextNewProcessCreationTime() + systemTime)); //make event for next process
                    readyQueue.add(np); //add new process to ready queue
                    if(CPU.isEmpty())
                        updateCPU(); //run cpu method only if it is empty, since there might be a process in there and we don't want duplicate events
                    break;
                case("cpuBound"):
                    //cpu bound
                    CPU.add(readyQueue.remove()); //move top of ready to CPU
                    updateCPU(); //decide what to do with current process in CPU
                    break;
                case("quantum"):
                    //quantum hit and sent back to ready queue
                    
                    CPU.peek().setCPUTimeRemaining(CPU.peek().getCPUTimeRemaining() - quantum); //decrease time remaining by quantum
                	CPU.peek().setCPUTimeSpent(CPU.peek().getCPUTimeSpent() + CPU.peek().getCurrentCPUBurstTimeRemaining()); //add remaining burst to cpu time spent
                	if (CPU.peek().getCurrentCPUBurstLength() - quantum <= 0) {
                		CPU.peek().generateNewBurstLength();
                	}
                	else {
                		CPU.peek().setCurrentCPUBurstTimeRemaining(CPU.peek().getCurrentCPUBurstTimeRemaining() - quantum); //decrease current burst time remaining quantum
                	}
                    readyQueue.add(CPU.remove()); //remove from CPU and put back into ready queue
                    updateCPU(); //update CPU as it is empty and there is at least this process in the ready queue
                    break;
                case("ioBound"):
                    //io service start, end time set, current bust subtracted, total time subtracted
                    CPU.peek().setCPUTimeRemaining(CPU.peek().getCPUTimeRemaining() - CPU.peek().getCurrentCPUBurstTimeRemaining()); //burst time finished so subtract the time just spent from total cpu time
                    CPU.peek().generateNewBurstLength(); //Burst is at 0 so create a new burst
                    double cIOserviceTime = exponentialRandom(avgIOServiceTime);//create new random io service time
                    cIOserviceTime /= 1000.0;
                    CPU.peek().setIoTimeSpent(CPU.peek().getIoTimeSpent() + cIOserviceTime); //add the io time to io time spent
                    CPU.peek().setNumOfIORequests(CPU.peek().getNumOfIORequests() + 1); //iterate io request count
                    
                    IO.add(CPU.remove()); //move process from the CPU to the IO
                    eventQueue.add(new Event("ioEnd", systemTime + cIOserviceTime)); //create the event for how long it will take
                    break;
                case("ioEnd"):
                    //io service done
                    readyQueue.add(IO.remove());
                    break;
                case("terminate"):
                    //log final data and remove it
                	CPU.peek().setCPUTimeSpent(CPU.peek().getCPUTimeSpent() + CPU.peek().getCPUTimeRemaining()); //add remaining time to the time spent in the CPU
                	CPU.peek().setCPUTimeRemaining(0); //set time remaining to 0
                	CPU.peek().setReadyQueueTimeSpent(CPU.peek().getTotalCPUTime() - (CPU.peek().getCPUTimeSpent() + CPU.peek().getIoTimeSpent()));
                	
                	if (CPU.peek().isCpuBound()) {
       				 	System.out.println("Time   " + dfLong.format(systemTime) + " Event 'Process Finished': pid=" + CPU.peek().getPid() 
                        + " CPU-bound totalCPU=" + dfShort.format(CPU.peek().getTotalCPUTime()) + " waitready=" + dfShort.format(CPU.peek().getReadyQueueTimeSpent()) 
                        + " inI/O=" + dfShort.format(CPU.peek().getIoTimeSpent()));
       			 	} else if (CPU.peek().isIoBound()) {
       				 System.out.println("Time   " + dfLong.format(systemTime) + " Event 'Process Finished': pid=" + CPU.peek().getPid() 
                        	+ " I/O-bound totalCPU=" + dfShort.format(CPU.peek().getTotalCPUTime()) + " waitready=" + dfShort.format(CPU.peek().getReadyQueueTimeSpent()) 
                        	+ " inI/O=" + dfShort.format(CPU.peek().getIoTimeSpent()));
       			 	}
                    if (CPU.peek().isCpuBound()) {
                    	numOfCPUBoundProcessesFinished++;
                    	totalCPUBoundFinishedProcessesTurnaroundTime += (CPU.peek().getReadyQueueTimeSpent() + CPU.peek().getCPUTimeSpent() + CPU.peek().getIoTimeSpent());
                    	totalNumOfIOCallsForCPUBoundProcesses += (double)CPU.peek().getNumOfIORequests();
                    	totalIOServiceTimeForCPUBoundProcesses += CPU.peek().getIoTimeSpent();
                    	totalReadyWaitingTimeForCPUBoundProcesses += CPU.peek().getReadyQueueTimeSpent();
                    	totalTotalCPUTimeForCPUBoundProcesses += CPU.peek().getTotalCPUTime();
                    }
                    else if (CPU.peek().isIoBound()) {
                    	numOfIOBoundProcessesFinished++;
                    	totalIOBoundFinishedProcessesTurnaroundTime += (CPU.peek().getReadyQueueTimeSpent() + CPU.peek().getCPUTimeSpent() + CPU.peek().getIoTimeSpent());
                    	totalNumOfIOCallsForIOBoundProcesses += (double)CPU.peek().getNumOfIORequests();
                    	totalIOServiceTimeForIOBoundProcesses += CPU.peek().getIoTimeSpent();
                    	totalReadyWaitingTimeForIOBoundProcesses += CPU.peek().getReadyQueueTimeSpent();
                    	totalTotalCPUTimeForIOBoundProcesses += CPU.peek().getTotalCPUTime();
                    }
                    CPU.remove(); //remove it
                    break;
            }
        }
        
        // PRINT OVERALL RESULTS
        System.out.println();
        System.out.println("OVERALL");
        System.out.println("Simulation time:                        " + dfLong.format(totalSimulationTime) + " seconds");
        System.out.println("Processes created:                      " + numOfProcessesCreated);
        System.out.println("Average CPU time:                       " + (dfShort.format(totalCPUTimeSpentForAllCreatedProcesses / (double)numOfProcessesCreated)) + " seconds" );
        System.out.println("CPU utilization:                        " + dfVeryShort.format(((totalCPUTimeSpentForAllCreatedProcesses / totalSimulationTime) * 100)) + "% (" + dfShort.format(totalCPUTimeSpentForAllCreatedProcesses) + " seconds)");
        System.out.println();
        System.out.println("TOTAL number of proc. completed:        " + (numOfCPUBoundProcessesFinished + numOfIOBoundProcessesFinished));
        System.out.println("Ratio of I/O-bound completed:           " + dfVeryShort.format((((double)(numOfIOBoundProcessesFinished) / ((double)numOfCPUBoundProcessesFinished + (double)numOfIOBoundProcessesFinished)) * 100.0)) + "%");
        System.out.println("Average CPU time:                       " + dfShort.format(((totalTotalCPUTimeForCPUBoundProcesses + totalTotalCPUTimeForIOBoundProcesses) / ((double)numOfCPUBoundProcessesFinished + (double)numOfIOBoundProcessesFinished))) + " seconds");
        System.out.println("Average ready waiting time:             " + dfShort.format(((totalReadyWaitingTimeForCPUBoundProcesses + totalReadyWaitingTimeForIOBoundProcesses) / ((double)numOfCPUBoundProcessesFinished + (double)numOfIOBoundProcessesFinished))) + " seconds");
        System.out.println("Average turnaround time:                " + dfShort.format(((totalCPUBoundFinishedProcessesTurnaroundTime + totalIOBoundFinishedProcessesTurnaroundTime) / ((double)numOfCPUBoundProcessesFinished + (double)numOfIOBoundProcessesFinished))) + " seconds");
        System.out.println();
        System.out.println("Number of I/O-BOUND proc. completed:    " + numOfIOBoundProcessesFinished);
        System.out.println("Average CPU time:                       " + dfShort.format((totalTotalCPUTimeForIOBoundProcesses / (double)numOfIOBoundProcessesFinished)) + " seconds");
        System.out.println("Average ready waiting time:             " + dfShort.format((totalReadyWaitingTimeForIOBoundProcesses / (double)numOfIOBoundProcessesFinished)) + " seconds");
        System.out.println("Average I/O service time:               " + dfShort.format((totalIOServiceTimeForIOBoundProcesses / (double)numOfIOBoundProcessesFinished)) + " seconds");
        System.out.println("Average turnaround time:                " + dfShort.format((totalIOBoundFinishedProcessesTurnaroundTime / (double)numOfIOBoundProcessesFinished)) + " seconds");
        System.out.println("Average I/O calls/proc.:                " + dfVeryShort.format((totalNumOfIOCallsForIOBoundProcesses / (double)numOfIOBoundProcessesFinished)));
        System.out.println();
        System.out.println("Number of CPU-BOUND proc. completed:    " + numOfCPUBoundProcessesFinished);
        System.out.println("Average CPU time:                       " + dfShort.format((totalTotalCPUTimeForCPUBoundProcesses / (double)numOfCPUBoundProcessesFinished)) + " seconds");
        System.out.println("Average ready waiting time:             " + dfShort.format((totalReadyWaitingTimeForCPUBoundProcesses / (double)numOfCPUBoundProcessesFinished)) + " seconds");
        System.out.println("Average I/O service time:               " + dfShort.format((totalIOServiceTimeForCPUBoundProcesses / (double)numOfCPUBoundProcessesFinished)) + " seconds");
        System.out.println("Average turnaround time:                " + dfShort.format((totalCPUBoundFinishedProcessesTurnaroundTime / (double)numOfCPUBoundProcessesFinished)) + " seconds");
        System.out.println("Average I/O calls/proc.:                " + dfVeryShort.format((totalNumOfIOCallsForCPUBoundProcesses / (double)numOfCPUBoundProcessesFinished)));
    }

    //Takes one double parameter, the average value, and returns a random
    //double with an exponential distribution around that average.
    double exponentialRandom (double expected) {
        double nice = -expected * Math.log (Math.random());
        nice *= 10000;
        nice = (int) nice;
        nice /= 10000;
        return nice;
    }
}