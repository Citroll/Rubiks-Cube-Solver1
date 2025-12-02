package rubikscube;

import java.io.*;
<<<<<<< HEAD
import java.lang.invoke.MethodHandles;
=======
>>>>>>> parent of aab0de8 (fuck)

public class Solver {

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
<<<<<<< HEAD
        File output = new File(args[1]);
        Solve cube = new Solve(input);
=======
        Solve s = new Solve(input);

        int maxDepth = 8;
        String solution = s.solveCube(maxDepth);
>>>>>>> parent of aab0de8 (fuck)

        if (solution == null) {
            System.out.println("No solution found within depth " + maxDepth);
        } else {
            s.printCube();
            System.out.println("Solution: " + solution);
<<<<<<< HEAD
            cube.applyMoves(solution);
            System.out.println("Solved? " + cube.isSolved());
=======
            s.applyMoves(solution);
            System.out.println("Solved? " + s.isSolved());
>>>>>>> parent of aab0de8 (fuck)
        }
        //File output = new File(args[1]);
        long endTime = System.nanoTime();
        long durationInNano = endTime - startTime;
        System.out.println("Execution time in milliseconds: " + durationInNano / 1_000_000.0);
    }

}
