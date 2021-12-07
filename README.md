<h2>OS Scheduling Simulator Project</h2>
</div>
<p>One way to evaluate short-term CPU scheduling algorithms is to create a computer model and to run simulations with it. For this project, you will write a program to model running processes in an operating system in order to study how the choice of CPU scheduling algorithm and other features affects system performance.</p>
<p class="p6"><b>Discrete event simulation</b> is a method used to model real world systems that can be decomposed into a series of logical <i>events</i> that happen at specific times. The main restriction on the system is that an event cannot affect the outcome of a prior event. As actions are processed by the simulator, these generate additional events that will take place in the future. For example, when a Process enters the CPU to continue executing, we know it will leave the CPU again in the near future, so that action will create a new event showing when and for what reason the Process will leave the CPU.</p>
<p>All events are stored in an <i>event queue</i> (actually, a priority queue). Every event has just <b>two data members:</b> an event type and a timestamp at which that event is to take place. A <i>virtual clock</i> is maintained to represent the current simulation time. At any point in the simulation, the next event to take place is at the head of the event queue. Since nothing of interest can happen between the present simulation time and the timestamp of the event at the head of the event queue, removal of an event for processing means we advance the virtual clock directly to that event&#39;s timestamp, rather than just incrementing our simulated clock one &quot;tick&quot; or time unit.</p>
<p class="p6">We will use this model to simulate a simple operating system. Processes enter the system and take turns on the CPU, occasionally spending some time waiting for I/O service. Each Process remains in the system until it has completed all of its (predetermined) total CPU time. There are a small number of events that can occur that will affect the system, and it is these events that drive the simulation.</p>
<p class="p7"><b>Work In Groups of Two or Three</b> for this project. You may use Java, C, C++, C#, or Python for your program. For others, <b>ask me first</b>.</p>
<h2>Project Details</h2>
<p class="p5">You will write an event-based discrete simulation program. It will simulate processes entering a computer operating system, running on a single CPU, and having occasional I/O system calls. You are to implement the Round-Robin scheduling algorithm for the queue of ready processes, with a fixed quantum timeout value.</p>
<p class="p6">Processes enter the system and wait for their turn on the CPU. They run on the CPU, possibly being pre-empted by the scheduler, until they leave for an I/O call. Once they have spent their entire service time on the CPU, they terminate and leave the system.</p>
<ul class="ul1">
<li><b>Time:</b> A logical clock is used to coordinate all events in the system; the &quot;ticks&quot; of the clock will represent microseconds. The total simulation running time is a parameter supplied by the user. (Use a <b>long int</b> for your clock variable)</li>
<li><b>Ready queue:</b> A round-robin ready queue is FIFO (first-in, first-out). A Process is selected from the front of the ready queue when the CPU becomes available. The CPU becomes available when a Process leaves the CPU for one of three reasons (see below).</li>
<li><b>Inter-Process creation time:</b> The time between the arrival of new processes entering the system&#39;s ready queue is a random number of ticks; these random numbers have an exponential distribution about a user-supplied mean value. Every time we create a new Process, we make a new event to trigger creation of the following new Process in the future. Several values will have to be generated for this new Process, which will be described below.</li>
<li><b>Process CPU requirements:</b> The amount of CPU service time (total number of microseconds elapsed while the Process is in the CPU) required by the Process will be a random number selected at the time the Process first enters the system (aka is created). This random value has an exponential distribution whose average is a user-supplied parameter.</li>
<li><b>Quantum:</b> Each Process can spend up to, but not more than, one quantum in the CPU before it is switched out. The quantum is a system wide constant value, entered as a parameter by the user.</li>
<li><b>Removal of processes from the CPU:</b> When a Process is assigned to the CPU, a future event will be generated that will remove it from the CPU at a later time. You find the minimum of three values to determine what happens to it:</li>
<ol class="ol1">
<li>If the total time remaining for the Process to complete is less than the length of its current CPU burst or the quantum, an event is generated that will cause the Process to leave the system. Performance statistics for the Process should be collected when this event is processed.</li>
<li>Else, if the time remaining for the current CPU burst (see next item) is less than the quantum, an event is generated that will cause the Process will leave for I/O service.</li>
<li>Else the Process&#39; quantum will expire. An event is generated that will return the Process to the ready queue.</li>
</ol>
<li>Context switch time is entered as a parameter by the user and should be accounted for. Add it in every time a Process enters the CPU.</li>
<li><b>I/O system call:</b> At the end of each CPU burst, the Process is removed from the CPU to wait for its I/O event to be serviced.</li>
<li><b>I/O Service time:</b> The time needed to service an I/O call is generated randomly based on the value of a user-supplied parameter. When each Process enters I/O service, an event is created to signal the future time of the service&#39;s completion.</li>
</ul>
<p class="p6">This diagram shows all of the possible states and locations for processes in the simulator. It pretty closely matches the diagram we studied in Chapter 3 of our text showing the possible states of a process and how it changes over time.</p>
<p align="center"><img src="https://i.gyazo.com/2ea90eff2e2adff6e602dec38cfe01e5.gif" alt="Process State Diagram" title="Process State Diagram" style="max-width: 100%;"/></p>
<p class="p6">When your program begins, it should first read several parameters from an input file.</p>
<ul class="ul1">
<li>program should ignore first line, reserved for comments</li>
<li>total simulation time, in integer <b>seconds</b></li>
<li>quantum size for round-robin, constant, in integer microseconds</li>
<li>time required for a context switch, constant, in integer microseconds</li>
<li>average Process length, in integer microseconds</li>
<li>average time between new processes being created, in integer microseconds</li>
<li>percentage of jobs that are &quot;I/O bound&quot;, i.e. jobs with frequent I/O, constant, an integer between 0 and 100</li>
<li>average time for an I/O system call to be serviced, in integer microseconds</li>
</ul>
<p class="p6">Whenever you create a new Process, you will have to generate several random numbers:</p>
<ul class="ul1">
<li>time when next new Process will be created, in microseconds: exponential (centered on input parameter)</li>
<li>total CPU time of this Process, in microseconds: exponential (centered on input parameter)</li>
<li>whether this Process is I/O bound or CPU-bound: choose a number 1-100; if less than &quot;I/O bound&quot; input parameter, make it I/O Bound, else CPU Bound</li>
<li>the length of its current CPU burst, in microseconds: normal random. If I/O bound, bursts are 1,000 - 2,000 usec; if CPU bound, 10,000 - 20,000 usec.</li>
</ul>
<p>Each Process also needs to keep track of its performance numbers as it goes, including all of these:</p>
<ul>
<li>how much total CPU time it has remaining in its life</li>
<li>how much time is left in its current CPU burst</li>
<li>how much time it has spent on the CPU so far</li>
<li>how much time it has spent doing I/O so far</li>
<li>how much time it has spent waiting in the ready queue so far</li>
<li>the number of times it has had an I/O request</li>
</ul>
<p class="p6">You will need to keep track of the following statistics as your simulation runs, and display them when it finishes:</p>
<ul class="ul1">
<li>overall CPU utilization (percentage of time CPU spent running user processes)</li>
<li>average CPU time per Process</li>
<li>average waiting time per Process in the ready queue and the I/O queue (separately)</li>
<li>average turnaround time per Process</li>
<li>show these averages for I/O-bound processes, CPU-bound processes, and all processes combined</li>
</ul>
<p class="p6">How do you start? Here are my suggestions.</p>
<ul>
<li>Get a working queue implementation and a priority queue implementation from anywhere you like (but document in your code comments where you got it from if you didn&#39;t write it yourself).</li>
<li>Define a class or struct to hold all of the information for each Process as described above (similar to a PCB). Maybe extend what you wrote for project 1?</li>
<li>Define a class or struct to represent an event. This holds only two pieces of info: event type and event timestamp, the time the event is to occur</li>
<li>Create functions/methods to generate the two types of random numbers in the requested ranges (&#39;regular&#39; which generates numbers between a min and a max value with equal probability, and &#39;exponential&#39; which generates in a distribution centered around the &#39;expected&#39; value, and which I&#39;m giving you straight up)</li>
<li>Write your main simulation event loop.</li>
<li>Write the code that handles each of the different event types.</li>
</ul>
<p></p>
<p class="p6">Your &quot;clock&quot; is a simple variable representing the microseconds from the start of the simulation (time = 0). Whenever you generate a new Process or move a Process between queues and the CPU, you will create a new event with a time stamp of when that event happens. These events go into a separate event queue, which is sorted by event time (a priority queue). Once all set-up steps are complete, you will have a loop that takes the next event off of the queue and processes it. Most events will end up generating other events -- for instance, the event of a Process leaving the CPU for any reason will generate a new event that checks if a Process in the ready queue can move into the CPU. The CPU-loading event will create another event describing how and when that Process will leave the CPU. You might want to represent your CPU as a queue as well (but limit it to hold only one Process at a time).</p>
<p><b>Random numbers</b> - when we say &#39;pick a number between 1 and 10&#39; we mean any number in the range is equally likely to be chosen. That&#39;s called <b>uniform distribution</b>. I&#39;m assuming you have written programs before that can choose random numbers within a given range and that this is easy. Another method that you probably haven&#39;t seen is to choose them from an <b>exponential distribution</b>. Here you give an expected or average value and use a short formula: generated_value = - expected * ln ( Random float between 0-1 ). I&#39;m giving you the Java code for this for free.</p>
<p>MAIN method outline:</p>
<ul>
<li>Setup</li>
<ul>
<li>Initialize all queues.</li>
<li>Set virtual clock simulation time to 0 usec.</li>
<li>Add a first event to your event queue: CREATE a new Process at time 0 usec.</li>
</ul>
<li>Main Simulation Loop</li>
<ul>
<li>Pull next event from event queue</li>
<li>Set simulation time to the timestamp of this event</li>
<li>Using a switch statement or similar, invoke the event handler code/method for the type of event you are processing</li>
<li>(Within that code, additional events may be added to the event queue. A Process may have its data updated and may be moved to another queue/location in the simulator.)</li>
</ul>
<li>Post Processing</li>
<ul>
<li>Tally up and print out collected statistics and data</li>
</ul>
</ul>
<p class="p6">I will publish my own executable version so you can compare the statistics you generate to mine, to be sure they&#39;re believable.</p>
<h2>Sample Input Files</h2>
<p>I&#39;ve provided three sample input files and the corresponding output produced by my simulator for them. Remember that most values are randomly generated, so results can differ quite a bit from one run to the next.</p>
<h3>params1</h3>
<p>This one is meant to model a realistic system with several processes running at once. The average total CPU time for a Process is 1 second. A new Process enters on average every 1.5 seconds. 50% of the processes will be CPU-bound (longer bursts that may be interrupted by the hardware quantum timer) and 50% I/O-bound (their bursts are all below the quantum value). I gave two different output files from different runs.</p>
<p>With a total sim time of 100 seconds and an average of one new Process every 1.5 seconds, you can expect about 66 (100 / 1.5) processes to be created in total over the simulation.</p>
<h3>params2</h3>
<p>This one is set up to have just one Process created, so you can trace its every step to help verify and debug your simulator. This is done by having the simulation time at 10 seconds but the average time between new processes at 100 seconds.</p>
<p>That one Process will be CPU-bound (per the input file, every new Process has a 0% chance of being selected as I/O-bound). The quantum value is set so high that the Process will never have quantum timeouts. And since the Process total CPU time is low, it will typically have only a couple of I/O system calls</p>
<h3>params3</h3>
<p>This one runs for 50,000 seconds so it creates lots and lots of processes, but the average time between processes is large enough that this system will often have only one Process active at a time. With so many processes being simulated over 50,000 seconds, the average statistics generated from one run to the next should be much more consistent compared to params1 and params2.</p>
<h2>My example program</h2>
<p>I am sharing a compiled copy of my solution so you may run it yourself. It features four possible levels of output (debug level 0-3). <i>Your submission does not have to have these features, but you may want to implement them to help you with development and debugging your project!</i> These are the actions of the various debug levels:</p>
<ol start="0">
<li>Nothing is printed during the simulation, only the statistics at the end</li>
<li>It prints a message every time a Process begins (New) or ends (Finished)</li>
<li>It prints a message for every event in the simulation</li>
<li>It prints a message for every event, and it shows the full state of the simulator -- Processes in the READY queue, in the CPU, and in I/O, plus the event queue. Here is an example of Debug Level 3:
<pre>Time  54.399000 Event &#39;Quantum Exp&#39;: pid=33
&#39;READY&#39; 2/999 (35:54.394) (33:54.399) 
&#39;CPU&#39; 0/1 
&#39;&#39; 0/0 
&#39;I/O&#39; 2/999 (30:54.463) (36:54.466) 
&#39;events&#39; 4/999 (R:54.399) (O:54.463) (O:54.466) (N:54.757) </pre>
<p>And here is its meaning. The simulation is at time 54.399000 seconds. Currently there are 4 active processes in the system. Their ID numbers are 30, 33, 35, and 36. (Processes 31, 32, and 34 must have already finished.)</p>
<ul>
<li>Line 1: the last event was a quantum expiration on Process 33 at time 54.399000, so that Process is getting bumped from the CPU and moved to the back of the ready queue.</li>
<li>Line 2: two processes are in the ready queue (out of a limit of 999 allowed). Process 35 is at the head, having entered this queue at time 54.394xxx. Process 33 is second, having just entered.</li>
<li>Line 3: The CPU is empty - 0 processes in it and a max limit of 1 (my simulator can set the number of CPU cores to any value, but you don&#39;t have to do that :)</li>
<li>Line 4: Reserved (I took this out to make the project easier for you :)</li>
<li>Line 5: two processes are currently waiting for I/O. In this queue, the time associated with a Process represents when that Process will FINISH its I/O operation and return to the ready state.</li>
<li>Line 6: the event queue. (R:54.399) means we are running the scheduler next, because the CPU just emptied out and there is at least one READY Process. (O:54.463) will be Process 30 finishing its I/O. (O:54.466) is the event when Process 36 will finish its current I/O. And finally, (N:54.757) the next new Process will be created at the time shown.</li>
</ul>
</li>
</ol>
<p align="center"><img src="https://i.gyazo.com/4817b504782947e5c9ba052c5ca076ef.png" alt="Debug Illustration" title="Debug Illustration" style="max-width: 100%;"/></p>
<p>You need to open a Command Prompt or Terminal window to run the program. Use the <b>cd</b> command to move to the directory where you put the executable and the input text files. If you want to save the output to a file, you can use your Unix redirect skills like so: <b>ossim21 &gt; outputfilename.txt</b></p>
<h2>Submitting Your Program</h2>
<p class="p5">Run your program with different sets of input parameters, and save the output of these runs to individual text files. Combine these and all of your source code into a single ZIP file. The file name should be of the form <b>P3-name1-name2.zip</b> (or .tgz) using each group member&#39;s last name in the filename. All documentation should be written as comments in your source files; you do not need to prepare a separate document.</p>
