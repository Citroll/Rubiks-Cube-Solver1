package rubikscube;

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

        /*if (args.length < 2) {
            System.out.println("File names are not specified");
            System.out.println("usage: java " + MethodHandles.lookup().lookupClass().getName() + " input_file output_file");
            return;
        }*/

 /*String fileName = args[0];
        File output = new File(args[1]);
        Solve cube = new Solve(fileName);

        String solution = cube.idaStarSolve(10_000); // 10 seconds
        if (solution == null) {
            System.out.println("No solution found within time limit.");
        } else {
            System.out.println("Solution: " + solution);
            cube.applyMoves(solution);
            System.out.println("Solved? " + cube.isSolved());
        }*/
        Solve c = new Solve();
        System.out.println("Solved?\n" + c.isSolved());
        System.out.println(c);

        c.makeMove('F');
        c.makeMove('F');
        c.makeMove('F');
        c.makeMove('F');
        System.out.println("After FFFF, solved?\n" + c.isSolved());

        c = new Solve();
        String scram = "FRUL";
        c.applyMoves(scram);
        System.out.println("After scramble " + scram + ":\n" + c);

        // Try solving that scrambled cube with a small time limit
        String sol = c.idaStarSolve(5_000);
        System.out.println("Found solution: " + sol);
        if (sol != null) {
            c.applyMoves(sol);
            System.out.println("Solved after applying solution? " + c.isSolved());
        }

        /*try (PrintWriter out = new PrintWriter(new FileWriter(output))) {
            if (solution == null) {
                // If you want, you can change this to just be blank instead
                System.out.println("No solution found within depth " + maxDepth);
            } else {
                System.out.println(solution);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        long endTime = System.nanoTime();
        long durationInNano = endTime - startTime;
        System.out.println("Execution time in milliseconds: " + durationInNano / 1_000_000.0);

    }
}
