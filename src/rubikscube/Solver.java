package rubikscube;

import java.io.*;
import java.lang.invoke.MethodHandles;
import java.util.*;

public class Solver {
    // 	To run compile with: javac src/rubikscube/Solver.java
    // java -cp src rubikscube.Solver testcases/base.txt output.txt

    // Using method of corner and edge cubies.
    // Store each piece's current position and orientation (index, colours)
    class Cube {
        public char face, side;
        public int index;
    }

    private char cube[][][] = new char[6][3][3];

    public static void main(String[] args) {
        System.out.println("number of arguments: " + args.length);
        for (int i = 0; i < args.length; i++) {
            System.out.println(args[i]);
        }

        if (args.length < 2) {
            System.out.println("File names are not specified");
            System.out.println("usage: java " + MethodHandles.lookup().lookupClass().getName() + " input_file output_file");
            return;
        }

        readFile(args); //converts file
        // solve...
        File output = new File(args[1]);
    }

    public static void readFile(String[] args){
        File input = new File(args[0]);
        try (Scanner sc = new Scanner(input)) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                System.out.println(line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }

    }
}
