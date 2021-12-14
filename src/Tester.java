/* Sean Cesario
 * Prince Paul
 * Geetanjali Kanojia
 * 12/7/2021
 * OS - Project 3: Scheduling Simulator
 * File Description - Driver for Project 3
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Tester {
    public static void main(String[] args) throws FileNotFoundException {
    	// INPUT SECTION
        System.out.println("OS Scheduling Simulation");
        System.out.println(" ");
        Map<String, Double> variables = new HashMap<String, Double>();
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

        while(reader.hasNext()) {
            if (reader.hasNextDouble()) {
                var = reader.nextDouble();
            }
            else {
                varName = reader.next();
                variables.put(varName, var);
            }
        }
        in.close();
        reader.close();

        // OUTPUTS INPUT PARAMETERS
        System.out.println("  Total simulation time: " + variables.get("totalSimulationTime").intValue() +"s");
        System.out.println("  Quantum: " + variables.get("quantum").intValue() +" us");
        System.out.println("  Context switch time: "+ variables.get("contextSwitchTime").intValue() +" us");
        System.out.printf("  Average total CPU time per process: %.6f", variables.get("averageProcessLength") / 1000000.0);
        System.out.println(" s");
        System.out.printf("  Average time between new processes: %.6f", variables.get("averageCreationTime")  / 1000000.0);
        System.out.println(" s");
        System.out.println("  Percentage of I/O-bound processes: " + variables.get("IOBoundPct").intValue() +"% ");
        System.out.println("  Average I/O service time: "+ variables.get("averageIOserviceTime").intValue() +" us");
        System.out.println();
        System.out.println();
        System.out.println("Print debugging info -- Level (1) -- N/F events --");
        System.out.println();

        // RUN THE SIMULATOR
        SchedulingSimulator schedulingSimulator = new SchedulingSimulator(variables.get("averageCreationTime"),
            variables.get("averageIOserviceTime"), variables.get("averageProcessLength"), variables.get("quantum"),
            variables.get("totalSimulationTime"), variables.get("contextSwitchTime"), variables.get("IOBoundPct").intValue());
        schedulingSimulator.run();

        // PRINT END OF PROGRAM
        System.out.println();
        System.out.println("End of simulation.");
    }
}