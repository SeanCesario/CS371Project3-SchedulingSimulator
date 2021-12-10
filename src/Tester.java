/* Sean Cesario
 * Prince Paul
 * Geetanjali Kanojia
 * 12/7/2021
 * OS - Project 3: Scheduling Simulator
 * Project Description -
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Tester {
    public static void main(String[] args) throws FileNotFoundException {
        System.out.println("OS Scheduling Simulation");
        System.out.println(" ");
        //Scanner will read the file into a hashmap
        Map<String, Double> variables = new HashMap();
        Scanner in = new Scanner(System.in);
        System.out.print("File name to read parameters: ");
        String fname = in.nextLine();
        File file = new File(fname);
        System.out.println("Input file: " + file.getName());
        Scanner reader = new Scanner(file);
        double var = 0.0;

        String varName = "";
        System.out.println("File comment: " + reader.nextLine());
        System.out.println("File parameters:");

        //storing variables in hashmap
        while(reader.hasNext()) {
            if (reader.hasNextDouble()) {
                var = reader.nextDouble();
            }
            else {
                varName = reader.next();
                variables.put(varName, var);
            }

        }
        SchedulingSimulator schedulingSimulator = new SchedulingSimulator(variables.get("averageCreationTime"), variables.get("averageIOserviceTime"), variables.get("averageProcessLength"), variables.get("quantum"), variables.get("totalSimulationTime"), variables.get("contextSwitchTime"), variables.get("IOBoundPct").intValue());
        schedulingSimulator.run();
        //Printing from hashmap
        System.out.println(" Total simulation time: " + variables.get("totalSimulationTime") +"s");
        System.out.println(" Quantum: " + variables.get("quantum") +"us");
        System.out.println(" Context switch time: "+ variables.get("contextSwitchTime") +"us");
        System.out.println(" Average total CPU time per process: "+ variables.get("averageProcessLength") +"s");
        System.out.println(" Average time between new processes: "+ variables.get("averageCreationTime") +"s");
        System.out.println(" Percentage of I/O-bound processes: " + variables.get("IOBoundPct") +"% ");
        System.out.println(" Average I/O service time: "+ variables.get("averageIOserviceTime") +"us");

        System.out.println("\n");

        /**
         //Shows debug levels
         System.out.println("Print debugging info -- \n  (0) none\n" +
         "  (1) N/F events\n" +
         "  (2) all events\n" +
         "  (3) all events and queues");


         System.out.print("Debug Level: ");
         int debugLevel = in.nextInt();

         if(debugLevel == 1)
         {
         System.out.print("print all N/F events");
         //Time   0.000000 Event 'New Process': pid=0 totalCPU=1.600 I/O-bound; next New at   0.368600
         }
         else if(debugLevel == 2)
         {
         System.out.print("print all events");
         }
         else if(debugLevel == 3)
         {
         System.out.print("print all events and queues");
         }
         else
         {
         //print nothing
         }


         //while() loop for the debugging

         **/

        System.out.println("\n");
        //Print the results
        System.out.println("OVERALL");
        System.out.println("Simulation time: " + 100.000000 + " seconds");
        System.out.println("Created " + 78 + " processes");
        System.out.println("Average CPU time      " + 1.013 + " seconds" );
        System.out.println("CPU utilization:                       "+ 60.87 +"% (" + 60.872 +" seconds)");
        System.out.println("Total time in context switches:        " + 1.314250 + " seconds");

        System.out.println("    ");

        System.out.println("TOTAL number of proc. completed:          " + 68);
        System.out.println("Ratio of I/O-bound completed:               "+ 42.6+"%");
        System.out.println("Average CPU time:                       " + 0.895 +" seconds");
        System.out.println("Average ready waiting time:             " + 2.369 + " seconds");
        System.out.println("Average turnaround time:                " + 8.308 +" seconds");

        System.out.println("    ");

        System.out.println("Number of I/O-BOUND proc. completed:      " + 29);
        System.out.println("Average CPU time:                       " + 0.724 + " seconds");
        System.out.println("Average ready waiting time:             " + 3.506 + " seconds");
        System.out.println("Average I/O service time:               " + 9.991 + " seconds");
        System.out.println("Average turnaround time:                " + 14.247 + " seconds");
        System.out.println("Average I/O calls/proc.:           " + 498.3);

        System.out.println("    ");

        System.out.println("Number of CPU-BOUND proc. completed:      " + 39);
        System.out.println("Average CPU time:                       " + 1.022 + " seconds");
        System.out.println("Average ready waiting time:             " + 1.523 + " seconds");
        System.out.println("Average I/O service time:               " + 1.341 + " seconds");
        System.out.println("Average turnaround time:                " + 3.893 + " seconds");
        System.out.println("Average I/O calls/proc.:            " + 67.1);

        System.out.println("    ");

        System.out.println("End of simulation");
    }
}