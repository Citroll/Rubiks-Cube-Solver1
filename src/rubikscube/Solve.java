package rubikscube;

import java.io.*;

public class Solve {

    // Face indices
    private static final int FRONT = 0;
    private static final int BACK = 1;
    private static final int RIGHT = 2;
    private static final int LEFT = 3;
    private static final int UP = 4;
    private static final int DONW = 5; // (kept your original name)

    // Current state of the cube
    private char[][][] state;

    // Solved reference state
    private static final char[][][] solvedState = createSolvedState();

    // ----- IDA* fields -----
    private static final int FOUND = -1;
    private static final int INF = Integer.MAX_VALUE;
    // Allowed moves (clockwise quarter-turns)
    private static final char[] MOVES = {'U', 'D', 'L', 'R', 'F', 'B'};
    // Maximum solution length we are willing to search
    private static final int MAX_DEPTH = 12; // tweak if needed

    // Solution buffer used by IDA*
    private String idaSolution = null;

    /**
     * Create the solved-state cube.
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
     * Default constructor: solved cube.
     */
    public Solve() {
        state = copyState(solvedState);
    }

    /**
     * Construct from a 9-line net file (same format as your toString). If
     * anything fails, it throws a RuntimeException.
     */
    public Solve(String fileName) {
        state = new char[6][3][3];
        try (BufferedReader input = new BufferedReader(new FileReader(fileName))) {
            String[] s = new String[9];
            for (int i = 0; i < 9; i++) {
                s[i] = input.readLine();
                if (s[i] == null) {
                    throw new RuntimeException("Not enough lines in cube file");
                }
            }

            // UP
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    state[UP][i][j] = s[i].charAt(j + 3);
                }
            }

            // LEFT, FRONT, RIGHT, BACK
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    state[LEFT][i][j] = s[i + 3].charAt(j);
                    state[FRONT][i][j] = s[i + 3].charAt(j + 3);
                    state[RIGHT][i][j] = s[i + 3].charAt(j + 6);
                    state[BACK][i][j] = s[i + 3].charAt(j + 9);
                }
            }

            // DOWN
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    state[DONW][i][j] = s[i + 6].charAt(j + 3);
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("Error reading cube file", e);
        }
    }

    // ===================== Basic cube operations =====================
    /**
     * Rotate 4 stickers (indices: [face,row,col]) in a cycle.
     */
    private void rotate4(int[] ind1, int[] ind2, int[] ind3, int[] ind4) {
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

    // ---- Six face moves (clockwise quarter turns) ----
    private void moveF() {
        rotateOneSide(FRONT);
        // update the four faces adjacent to F
        rotate4(new int[]{UP, 2, 0}, new int[]{RIGHT, 0, 0}, new int[]{DONW, 0, 2}, new int[]{LEFT, 2, 2});
        rotate4(new int[]{UP, 2, 1}, new int[]{RIGHT, 1, 0}, new int[]{DONW, 0, 1}, new int[]{LEFT, 1, 2});
        rotate4(new int[]{UP, 2, 2}, new int[]{RIGHT, 2, 0}, new int[]{DONW, 0, 0}, new int[]{LEFT, 0, 2});
    }

    private void moveB() {
        rotateOneSide(BACK);
        // update the four faces adjacent to B
        rotate4(new int[]{UP, 0, 2}, new int[]{LEFT, 0, 0}, new int[]{DONW, 2, 0}, new int[]{RIGHT, 2, 2});
        rotate4(new int[]{UP, 0, 1}, new int[]{LEFT, 1, 0}, new int[]{DONW, 2, 1}, new int[]{RIGHT, 1, 2});
        rotate4(new int[]{UP, 0, 0}, new int[]{LEFT, 2, 0}, new int[]{DONW, 2, 2}, new int[]{RIGHT, 0, 2});
    }

    private void moveR() {
        rotateOneSide(RIGHT);
        // update the four faces adjacent to R
        rotate4(new int[]{FRONT, 2, 2}, new int[]{UP, 2, 2}, new int[]{BACK, 0, 0}, new int[]{DONW, 2, 2});
        rotate4(new int[]{FRONT, 1, 2}, new int[]{UP, 1, 2}, new int[]{BACK, 1, 0}, new int[]{DONW, 1, 2});
        rotate4(new int[]{FRONT, 0, 2}, new int[]{UP, 0, 2}, new int[]{BACK, 2, 0}, new int[]{DONW, 0, 2});
    }

    private void moveL() {
        rotateOneSide(LEFT);
        // update the four faces adjacent to L
        rotate4(new int[]{BACK, 2, 2}, new int[]{UP, 0, 0}, new int[]{FRONT, 0, 0}, new int[]{DONW, 0, 0});
        rotate4(new int[]{BACK, 1, 2}, new int[]{UP, 1, 0}, new int[]{FRONT, 1, 0}, new int[]{DONW, 1, 0});
        rotate4(new int[]{BACK, 0, 2}, new int[]{UP, 2, 0}, new int[]{FRONT, 2, 0}, new int[]{DONW, 2, 0});
    }

    private void moveU() {
        rotateOneSide(UP);
        // update the four faces adjacent to U
        rotate4(new int[]{BACK, 0, 0}, new int[]{RIGHT, 0, 0}, new int[]{FRONT, 0, 0}, new int[]{LEFT, 0, 0});
        rotate4(new int[]{BACK, 0, 1}, new int[]{RIGHT, 0, 1}, new int[]{FRONT, 0, 1}, new int[]{LEFT, 0, 1});
        rotate4(new int[]{BACK, 0, 2}, new int[]{RIGHT, 0, 2}, new int[]{FRONT, 0, 2}, new int[]{LEFT, 0, 2});
    }

    private void moveD() {
        rotateOneSide(DONW);
        // update the four faces adjacent to D
        rotate4(new int[]{LEFT, 2, 0}, new int[]{FRONT, 2, 0}, new int[]{RIGHT, 2, 0}, new int[]{BACK, 2, 0});
        rotate4(new int[]{LEFT, 2, 1}, new int[]{FRONT, 2, 1}, new int[]{RIGHT, 2, 1}, new int[]{BACK, 2, 1});
        rotate4(new int[]{LEFT, 2, 2}, new int[]{FRONT, 2, 2}, new int[]{RIGHT, 2, 2}, new int[]{BACK, 2, 2});
    }

    /**
     * Apply a single clockwise quarter turn.
     */
    public void makeMove(char c) {
        switch (c) {
            case 'F':
                moveF();
                break;
            case 'B':
                moveB();
                break;
            case 'R':
                moveR();
                break;
            case 'L':
                moveL();
                break;
            case 'U':
                moveU();
                break;
            case 'D':
                moveD();
                break;
            default:
                throw new IllegalArgumentException("Incorrect move: " + c);
        }
    }

    /**
     * Apply a sequence of moves like "RUFDL...".
     */
    public void applyMoves(String moves) {
        for (char c : moves.toCharArray()) {
            makeMove(c);
        }
    }

    /**
     * Check if current cube is solved.
     */
    public boolean isSolved() {
        return isSolved(this.state);
    }

    /**
     * String representation as the 9-line net.
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
        // DOWN
        for (int i = 0; i < 3; i++) {
            sb.append("   ");
            sb.append(state[DONW][i][0]).append(state[DONW][i][1]).append(state[DONW][i][2]).append("\n");
        }
        return sb.toString();
    }

    /**
     * Order of a move sequence: smallest k such that applying it k times
     * returns to solved.
     */
    public static int order(String moves) {
        Solve cube = new Solve();
        cube.applyMoves(moves);
        int ord = 1;
        while (!cube.isSolved()) {
            cube.applyMoves(moves);
            ord++;
        }
        return ord;
    }

    // ===================== IDA* + "PDB-style" heuristic =====================
    // Deep copy of a cube state
    private static char[][][] copyState(char[][][] src) {
        char[][][] dst = new char[6][3][3];
        for (int f = 0; f < 6; f++) {
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    dst[f][i][j] = src[f][i][j];
                }
            }
        }
        return dst;
    }

    // Apply a single move to an arbitrary state using existing move logic
    private void applyMoveToState(char[][][] s, char move) {
        char[][][] backup = this.state;
        this.state = s;
        makeMove(move);
        this.state = backup;
    }

    // Check if an arbitrary state is solved
    private static boolean isSolved(char[][][] s) {
        for (int f = 0; f < 6; f++) {
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (s[f][i][j] != solvedState[f][i][j]) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    // Trivially admissible heuristic: always 0
    // This turns IDA* into a plain iterative deepening DFS on depth.
    private int heuristic(char[][][] s) {
        return 0;
    }

    /**
     * Solve the current cube using IDA*. Returns a move string like "RUFD..."
     * (clockwise quarter-turns only), or null if no solution found within the
     * time limit.
     */
    public String idaStarSolve(long timeLimitMillis) {
        char[][][] start = copyState(this.state);

        if (isSolved(start)) {
            return "";
        }

        long startTime = System.currentTimeMillis();
        int bound = heuristic(start);  // currently 0
        idaSolution = null;

        // Ensure bound starts at least 1
        if (bound < 1) {
            bound = 1;
        }

        while (bound <= MAX_DEPTH) {
            int t = idaSearch(start, 0, bound, startTime, timeLimitMillis, new StringBuilder(), '\0');

            if (t == FOUND) {
                return idaSolution;  // solution path as string of moves
            }

            if (t == INF) {
                // no solution at this or smaller f-bounds within depth/time limits
                return null;
            }

            if (System.currentTimeMillis() - startTime > timeLimitMillis) {
                return null; // timed out
            }

            bound = t; // increase f-bound to smallest overrun
        }

        // Exhausted up to MAX_DEPTH
        return null;
    }

    /**
     * Recursive IDA* DFS with iterative deepening on f = g + h.
     */
    private int idaSearch(char[][][] state,
            int g,
            int bound,
            long startTime,
            long timeLimit,
            StringBuilder path,
            char lastMove) {

// Hard depth limit
        if (g > MAX_DEPTH) {
            return INF;
        }

// Time guard
        if (System.currentTimeMillis() - startTime > timeLimit) {
            return INF;
        }

        int h = heuristic(state);   // currently 0
        int f = g + h;

        if (f > bound) {
            return f;
        }

        if (isSolved(state)) {
            idaSolution = path.toString();
            return FOUND;
        }

        int min = INF;

        for (char move : MOVES) {

            char[][][] next = copyState(state);
            applyMoveToState(next, move);

            path.append(move);
            int t = idaSearch(next, g + 1, bound, startTime, timeLimit, path, move);
            path.deleteCharAt(path.length() - 1);

            if (t == FOUND) {
                return FOUND;
            }
            if (t < min) {
                min = t;
            }
        }

        return min;
    }

}
