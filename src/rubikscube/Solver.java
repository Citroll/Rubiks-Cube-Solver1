package rubikscube;

import java.io.*;
import java.lang.invoke.MethodHandles;

public class Solver {

    // Using method of corner and edge cubies.
    // Store each piece's current position and orientation (index, colours)
    // To run, compile with:
    // javac -cp src -d src src/rubikscube/*.java
    // java -cp src rubikscube.Solver testcases/base.txt output.txt
    public static void main(String[] args) {
        long startTime = System.nanoTime();
        System.out.println("number of arguments: " + args.length);
        for (int i = 0; i < args.length; i++) {
            System.out.println(args[i]);
        }

        if (args.length < 2) {
            System.out.println("File names are not specified");
            System.out.println("usage: java " + MethodHandles.lookup().lookupClass().getName() + " input_file output_file");
            return;
        }

        File input = new File(args[0]); //converts file
        Solve solved = new Solve(input);

        solved.printCube();
        System.out.println(solved.isSolved());

        solved.applyMoves("R U R' U'");

        String moves = solved.firstLayerIDDFS(4);
        System.out.println(moves);


        File output = new File(args[1]);
        long endTime = System.nanoTime();
        long durationInNano = endTime - startTime;
        System.out.println("Execution time in seconds: " + durationInNano / 1_000_000_000.0);
    }

}
