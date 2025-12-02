package rubikscube;

import java.io.*;
import java.lang.invoke.MethodHandles;

public class Solver {

    private static final int FRONT = 0;
    private static final int BACK = 1;
    private static final int RIGHT = 2;
    private static final int LEFT = 3;
    private static final int UP = 4;
    private static final int DONW = 5; // keeping your original name

    // current state of the cube
    private char[][][] state;

    // solved reference state
    private static final char[][][] solvedState = createSolvedState();

    /**
     * Create the solved cube state.
     */
    private static char[][][] createSolvedState() {
        char[][][] c = new char[6][3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                c[FRONT][i][j] = 'W';
                c[BACK][i][j] = 'Y';
                c[RIGHT][i][j] = 'B';
                c[LEFT][i][j] = 'G';
                c[UP][i][j] = 'O';
                c[DONW][i][j] = 'R';
            }
        }
        return c;
    }

    /**
     * Default constructor: start in solved state.
     */
    public Solver() {
        state = createSolvedState();
    }

    /**
     * Helper: set a color; assume input is valid.
     */
    private void setColor(int face, int i, int j, char color) {
        state[face][i][j] = color;
    }

    /**
     * Create a Rubik's Cube from a file description. Assumes file is valid and
     * formatted as expected.
     */
    public Solver(String fileName) throws IOException {
        try (BufferedReader input = new BufferedReader(new FileReader(fileName))) {
            String[] s = new String[9];
            for (int i = 0; i < 9; i++) {
                s[i] = input.readLine();
            }

            state = new char[6][3][3];
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    setColor(UP, i, j, s[i].charAt(j + 3));
                    setColor(LEFT, i, j, s[i + 3].charAt(j));
                    setColor(FRONT, i, j, s[i + 3].charAt(j + 3));
                    setColor(RIGHT, i, j, s[i + 3].charAt(j + 6));
                    setColor(BACK, i, j, s[i + 3].charAt(j + 9));
                    setColor(DONW, i, j, s[i + 6].charAt(j + 3));
                }
            }
        }
    }

    /**
     * Rotate 4 stickers in a cycle.
     */
    private void rotate4(int ind1[], int ind2[], int ind3[], int ind4[]) {
        char tmp = state[ind4[0]][ind4[1]][ind4[2]];
        state[ind4[0]][ind4[1]][ind4[2]] = state[ind3[0]][ind3[1]][ind3[2]];
        state[ind3[0]][ind3[1]][ind3[2]] = state[ind2[0]][ind2[1]][ind2[2]];
        state[ind2[0]][ind2[1]][ind2[2]] = state[ind1[0]][ind1[1]][ind1[2]];
        state[ind1[0]][ind1[1]][ind1[2]] = tmp;
    }

    /**
     * Rotate one face clockwise.
     */
    private void rotateOneSide(int side) {
        rotate4(new int[]{side, 0, 0}, new int[]{side, 0, 2}, new int[]{side, 2, 2}, new int[]{side, 2, 0});
        rotate4(new int[]{side, 0, 1}, new int[]{side, 1, 2}, new int[]{side, 2, 1}, new int[]{side, 1, 0});
    }

    // Basic face moves
    private void moveF() {
        rotateOneSide(FRONT);
        rotate4(new int[]{UP, 2, 0}, new int[]{RIGHT, 0, 0}, new int[]{DONW, 0, 2}, new int[]{LEFT, 2, 2});
        rotate4(new int[]{UP, 2, 1}, new int[]{RIGHT, 1, 0}, new int[]{DONW, 0, 1}, new int[]{LEFT, 1, 2});
        rotate4(new int[]{UP, 2, 2}, new int[]{RIGHT, 2, 0}, new int[]{DONW, 0, 0}, new int[]{LEFT, 0, 2});
    }

    private void moveB() {
        rotateOneSide(BACK);
        rotate4(new int[]{UP, 0, 2}, new int[]{LEFT, 0, 0}, new int[]{DONW, 2, 0}, new int[]{RIGHT, 2, 2});
        rotate4(new int[]{UP, 0, 1}, new int[]{LEFT, 1, 0}, new int[]{DONW, 2, 1}, new int[]{RIGHT, 1, 2});
        rotate4(new int[]{UP, 0, 0}, new int[]{LEFT, 2, 0}, new int[]{DONW, 2, 2}, new int[]{RIGHT, 0, 2});
    }

    private void moveR() {
        rotateOneSide(RIGHT);
        rotate4(new int[]{FRONT, 2, 2}, new int[]{UP, 2, 2}, new int[]{BACK, 0, 0}, new int[]{DONW, 2, 2});
        rotate4(new int[]{FRONT, 1, 2}, new int[]{UP, 1, 2}, new int[]{BACK, 1, 0}, new int[]{DONW, 1, 2});
        rotate4(new int[]{FRONT, 0, 2}, new int[]{UP, 0, 2}, new int[]{BACK, 2, 0}, new int[]{DONW, 0, 2});
    }

    private void moveL() {
        rotateOneSide(LEFT);
        rotate4(new int[]{BACK, 2, 2}, new int[]{UP, 0, 0}, new int[]{FRONT, 0, 0}, new int[]{DONW, 0, 0});
        rotate4(new int[]{BACK, 1, 2}, new int[]{UP, 1, 0}, new int[]{FRONT, 1, 0}, new int[]{DONW, 1, 0});
        rotate4(new int[]{BACK, 0, 2}, new int[]{UP, 2, 0}, new int[]{FRONT, 2, 0}, new int[]{DONW, 2, 0});
    }

    private void moveU() {
        rotateOneSide(UP);
        rotate4(new int[]{BACK, 0, 0}, new int[]{RIGHT, 0, 0}, new int[]{FRONT, 0, 0}, new int[]{LEFT, 0, 0});
        rotate4(new int[]{BACK, 0, 1}, new int[]{RIGHT, 0, 1}, new int[]{FRONT, 0, 1}, new int[]{LEFT, 0, 1});
        rotate4(new int[]{BACK, 0, 2}, new int[]{RIGHT, 0, 2}, new int[]{FRONT, 0, 2}, new int[]{LEFT, 0, 2});
    }

    private void moveD() {
        rotateOneSide(DONW);
        rotate4(new int[]{LEFT, 2, 0}, new int[]{FRONT, 2, 0}, new int[]{RIGHT, 2, 0}, new int[]{BACK, 2, 0});
        rotate4(new int[]{LEFT, 2, 1}, new int[]{FRONT, 2, 1}, new int[]{RIGHT, 2, 1}, new int[]{BACK, 2, 1});
        rotate4(new int[]{LEFT, 2, 2}, new int[]{FRONT, 2, 2}, new int[]{RIGHT, 2, 2}, new int[]{BACK, 2, 2});
    }

    /**
     * Apply a single face turn.
     */
    public void makeMove(char c) {
        if (c == 'F') {
            moveF(); 
        }else if (c == 'B') {
            moveB(); 
        }else if (c == 'R') {
            moveR(); 
        }else if (c == 'L') {
            moveL(); 
        }else if (c == 'U') {
            moveU(); 
        }else if (c == 'D') {
            moveD(); 
        }else {
            throw new IllegalArgumentException("Incorrect move: " + c);
        }
    }

    /**
     * Apply a sequence of moves.
     */
    public void applyMoves(String moves) {
        for (char c : moves.toCharArray()) {
            makeMove(c);
        }
    }

    /**
     * True if cube is in solved state.
     */
    public boolean isSolved() {
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 3; j++) {
                for (int k = 0; k < 3; k++) {
                    if (state[i][j][k] != solvedState[i][j][k]) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * String representation (net layout).
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        // UP
        for (int i = 0; i < 3; i++) {
            sb.append("   ");
            sb.append(state[UP][i][0]).append(state[UP][i][1]).append(state[UP][i][2]).append("\n");
        }

        // LEFT, FRONT, RIGHT, BACK
        for (int i = 0; i < 3; i++) {
            sb.append(state[LEFT][i][0]).append(state[LEFT][i][1]).append(state[LEFT][i][2]);
            sb.append(state[FRONT][i][0]).append(state[FRONT][i][1]).append(state[FRONT][i][2]);
            sb.append(state[RIGHT][i][0]).append(state[RIGHT][i][1]).append(state[RIGHT][i][2]);
            sb.append(state[BACK][i][0]).append(state[BACK][i][1]).append(state[BACK][i][2]).append("\n");
        }

        // DONW
        for (int i = 0; i < 3; i++) {
            sb.append("   ");
            sb.append(state[DONW][i][0]).append(state[DONW][i][1]).append(state[DONW][i][2]).append("\n");
        }

        return sb.toString();
    }

    /**
     * Order of a move sequence (how many times you must apply it to return to
     * solved).
     */
    public static int order(String moves) {
        Solver cube = new Solver();
        cube.applyMoves(moves);
        int ord = 1;
        while (!cube.isSolved()) {
            cube.applyMoves(moves);
            ord++;
        }
        return ord;
    }

    /**
     * Undo a single move (c⁻¹ = c c c).
     */
    private void undoMove(char c) {
        makeMove(c);
        makeMove(c);
        makeMove(c);
    }

    // Allowed moves for the solver
    private static final char[] ALL_MOVES = {'F', 'B', 'R', 'L', 'U', 'D'};

    /**
     * Simple pruning: avoid 4 identical moves in a row (FFFF is identity).
     */
    private boolean isRedundantMove(char move, StringBuilder path) {
        int len = path.length();
        return (len >= 3
                && path.charAt(len - 1) == move
                && path.charAt(len - 2) == move
                && path.charAt(len - 3) == move);
    }

    /**
     * Solver the cube with iterative deepening DFS up to maxDepth. Returns the
     * move sequence or null if not found within depth.
     */
    public String solve(int maxDepth) {
        if (isSolved()) {
            return "";
        }

        StringBuilder path = new StringBuilder();

        for (int depth = 1; depth <= maxDepth; depth++) {
            if (dfsSolve(depth, path)) {
                return path.toString();
            }
        }

        return null;
    }

    /**
     * DFS on the same cube instance with backtracking + pruning.
     */
    private boolean dfsSolve(int remainingDepth, StringBuilder path) {
        if (isSolved()) {
            return true;
        }
        if (remainingDepth == 0) {
            return false;
        }

        for (char move : ALL_MOVES) {
            if (isRedundantMove(move, path)) {
                continue;
            }

            makeMove(move);
            path.append(move);

            if (dfsSolve(remainingDepth - 1, path)) {
                return true;
            }

            undoMove(move);
            path.deleteCharAt(path.length() - 1);
        }

        return false;
    }

    public static void main(String[] args) {
        // To run, compile with:
        // javac -cp src -d src src/rubikscube/*.java
        // java -cp src rubikscube.Solver testcases/base.txt output.txt
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

        String inputFileName = args[0];
        String outputFileName = args[1];

        try {
            Solver cube = new Solver(inputFileName);

            int maxDepth = 12;
            String solution = cube.solve(maxDepth);

            try (PrintWriter out = new PrintWriter(new FileWriter(outputFileName))) {
                if (solution == null) {
                    out.println("No solution found within depth " + maxDepth);
                } else {
                    out.println(solution);
                }
            }

            long endTime = System.nanoTime();
            long durationInNano = endTime - startTime;
            System.out.println("Execution time in seconds: "
                    + durationInNano / 1_000_000_000.0);

            // Optional: also print to console
            if (solution == null) {
                System.out.println("No solution found within depth " + maxDepth);
            } else {
                System.out.println("Solution: " + solution);
            }

        } catch (IOException e) {
            System.err.println("I/O error: " + e.getMessage());
        }
    }
}
