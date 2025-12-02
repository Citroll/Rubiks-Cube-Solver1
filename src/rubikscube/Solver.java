package rubikscube;

import java.io.*;
import java.lang.invoke.MethodHandles;

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

        File input = new File(args[0]);
        File output = new File(args[1]);
        Solve s = new Solve(input);
        s.debugHeuristicCheck();

        int maxDepth = 20;
        String solution = s.expandToQuarterTurns(s.solveCube(maxDepth));

        try (PrintWriter out = new PrintWriter(new FileWriter(output))) {
            if (solution == null) {
                System.out.println("No solution found within depth " + maxDepth);
            } else {
                s.printCube();
                System.out.println("Solution: " + solution);
                s.applyMoves(solution);
                System.out.println("Solved? " + s.isSolved());
                out.println(solution);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        long endTime = System.nanoTime();
        long durationInNano = endTime - startTime;
        System.out.println("Execution time in milliseconds: " + durationInNano / 1_000_000.0);

    }
}