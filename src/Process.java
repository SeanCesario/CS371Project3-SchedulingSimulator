/* Sean Cesario
 * Prince Paul
 * Geetanjali Kanojia
 * 12/9/2021
 * OS - Project 3: Scheduling Simulator
 * File Description - Process class
 */
public class Process {
    private int pid;
    private double nextNewProcessCreationTime;
    private double totalCPUTime;
    private boolean ioBound = false;
    private boolean cpuBound = false;
    private double currentCPUBurstLength;
    private double CPUTimeRemaining;
    private double currentCPUBurstTimeRemaining;
    private double CPUTimeSpent;
    private double ioTimeSpent;
    private double ReadyQueueTimeSpent;
    private int numOfIORequests;
    private double timeStampStart;
    private double timeStampEnd;

    // Constructor
    public Process(int pid, double avgCreationTime, double avgProcessLength, int IOBoundPct) {
        this.pid = pid;
        this.nextNewProcessCreationTime = generateExponentialRandom(avgCreationTime);
        this.totalCPUTime = generateExponentialRandom(avgProcessLength);
        int IoOrCPUBoundNumber = generateNormalRandom(1, 100);
        if (IoOrCPUBoundNumber < IOBoundPct) {
            this.ioBound = true;
        } else {
            this.cpuBound = true;
        }
        if (ioBound) {
            this.currentCPUBurstLength = (double)generateNormalRandom(1000, 2000) / 1000000.0;
        } else if (cpuBound) {
            this.currentCPUBurstLength = (double)generateNormalRandom(10000, 20000) / 1000000.0;
        }
        CPUTimeRemaining = totalCPUTime;
        currentCPUBurstTimeRemaining = currentCPUBurstLength;
        CPUTimeSpent = 0.0;
        ioTimeSpent = 0.0;
        ReadyQueueTimeSpent = 0.0;
        numOfIORequests = 0;
    }

    // Returns the PID
    public int getPid() {
        return pid;
    }

    // Sets the PID
    public void setPid(int pid) {
        this.pid = pid;
    }

    // Returns the next new process creation time
    public double getNextNewProcessCreationTime() {
        return nextNewProcessCreationTime;
    }

    // Sets the next new process creation time
    public void setNextNewProcessCreationTime(double nextNewProcessCreationTime) {
        this.nextNewProcessCreationTime = nextNewProcessCreationTime;
    }

    // Returns the total CPU time
    public double getTotalCPUTime() {
        return totalCPUTime;
    }

    // Sets the total CPU time
    public void setTotalCPUTime(double totalCPUTime) {
        this.totalCPUTime = totalCPUTime;
    }

    // Returns whether the process is IO Bound or not
    public boolean isIoBound() {
        return ioBound;
    }

    // Sets whether the process is IO Bound or not
    public void setIoBound(boolean ioBound) {
        this.ioBound = ioBound;
    }

    // Returns whether the process is CPU Bound or not
    public boolean isCpuBound() {
        return cpuBound;
    }

    // Sets whether the process is CPU Bound or not
    public void setCpuBound(boolean cpuBound) {
        this.cpuBound = cpuBound;
    }

    // Returns the current CPU burst length
    public double getCurrentCPUBurstLength() {
        return currentCPUBurstLength;
    }

    // Sets the current CPU burst length
    public void setCurrentCPUBurstLength(double currentCPUBurstLength) {
        this.currentCPUBurstLength = currentCPUBurstLength;
    }

    // Returns the CPU time remaining
    public double getCPUTimeRemaining() {
        return CPUTimeRemaining;
    }

    // Sets the CPU time remaining
    public void setCPUTimeRemaining(double cPUTimeRemaining) {
        CPUTimeRemaining = cPUTimeRemaining;
    }

    // Returns the current CPU burst time remaining
    public double getCurrentCPUBurstTimeRemaining() {
        return currentCPUBurstTimeRemaining;
    }

    // Sets the current CPU burst time remaining
    public void setCurrentCPUBurstTimeRemaining(double currentCPUBurstTimeRemaining) {
        this.currentCPUBurstTimeRemaining = currentCPUBurstTimeRemaining;
    }

    // Returns CPU time spent
    public double getCPUTimeSpent() {
        return CPUTimeSpent;
    }

    // Sets CPU time spent
    public void setCPUTimeSpent(double cPUTimeSpent) {
        CPUTimeSpent = cPUTimeSpent;
    }

    // Returns IO time spent
    public double getIoTimeSpent() {
        return ioTimeSpent;
    }

    // Sets IO time spent
    public void setIoTimeSpent(double ioTimeSpent) {
        this.ioTimeSpent = ioTimeSpent;
    }

    // Returns time spent in ready queue
    public double getReadyQueueTimeSpent() {
        return ReadyQueueTimeSpent;
    }

    // Sets time spent in ready queue
    public void setReadyQueueTimeSpent(double ReadyQueueTimeSpent) {
        this.ReadyQueueTimeSpent = ReadyQueueTimeSpent;
    }

    // Returns number of IO requests
    public int getNumOfIORequests() {
        return numOfIORequests;
    }

    // Sets number of IO requests
    public void setNumOfIORequests(int numOfIORequests) {
        this.numOfIORequests = numOfIORequests;
    }

    public void generateNewBurstLength(){
        if (ioBound) {
            currentCPUBurstLength = (double)generateNormalRandom(1000, 2000) / 1000000.0;
        } else if (cpuBound) {
            currentCPUBurstLength = (double)generateNormalRandom(10000, 20000) / 1000000.0;
        }
        currentCPUBurstTimeRemaining = currentCPUBurstLength;
    }

    public void startReadyWaiting(double timeStampStart){
        this.timeStampStart = timeStampStart;
    }

    public void endReadyWaiting(double timeStampEnd){
        this.timeStampEnd = timeStampEnd;
        ReadyQueueTimeSpent += (this.timeStampEnd - timeStampStart);
    }

    //Takes two int parameters, min and max, and returns a 
    //random int between those two values with a uniform distribution.
    public int generateNormalRandom(int min, int max) {
        return (int)Math.floor(Math.random()*(max-min+1)+min);
    }

    //Takes one double parameter, the average value, and returns a random
    //double with an exponential distribution around that average.
    public double generateExponentialRandom(double expected) {
        double nice = -expected * Math.log ( Math.random() );
        nice *= 10000;
        nice = (int) nice;
        nice /= 10000;
        return ( nice );
    }
}