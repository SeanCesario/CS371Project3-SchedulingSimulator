import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class tester {
    public static void main(String[] args) throws FileNotFoundException {
        Map<String, Integer> variables = new HashMap<String, Integer>(); //Map to hold variables
        Scanner in = new Scanner(System.in); //Scanner for user input

        //Ask and collect file name
        System.out.println("Input file name: ");
        String fname = in.nextLine();
        File file = new File(fname);

        //Read data from the file
        Scanner reader = new Scanner(file);

        int var = 0;
        String varName = "";
        reader.nextLine(); //Skip first line

        while(reader.hasNext()){
            if(reader.hasNextInt()){
                var = reader.nextInt();
            }
            else{
                varName = reader.next();
                variables.put(varName, var);
            }
        }

        System.out.println(variables);

        //testing goes here
    }
}
