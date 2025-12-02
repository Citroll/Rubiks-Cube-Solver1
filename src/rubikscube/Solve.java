package rubikscube;

import java.io.*;
import java.util.Arrays;

public class Solve {

    private char[][][] cube;
    private static final int U = 0, L = 1, F = 2, R = 3, B = 4, D = 5;
    private static final String SOLVED
            = "   OOO\n"
            + "   OOO\n"
            + "   OOO\n"
            + "GGGWWWBBBYYY\n"
            + "GGGWWWBBBYYY\n"
            + "GGGWWWBBBYYY\n"
            + "   RRR\n"
            + "   RRR\n"
            + "   RRR\n";

    private static final String[] MOVES = {
        "U", "U'", "U2",
        "D", "D'", "D2",
        "L", "L'", "L2",
        "R", "R'", "R2",
        "F", "F'", "F2",
        "B", "B'", "B2"
    };

    //CORNERS: UFL, URF, UBR, ULB, DLF, DFR, DRB, DBL
    //EDGES: UR, UF, UL, UB, DR, DF, DL, DB, FR, FL, BL, BR
    private static final int[][] CORNERS = {
        {U, 2, 0, F, 0, 0, L, 0, 2}, //0 UFL
        {U, 2, 2, R, 0, 0, F, 0, 2}, //1 URF
        {U, 0, 2, B, 0, 0, R, 0, 2}, //2 UBR
        {U, 0, 0, L, 0, 0, B, 0, 2}, //3 ULB
        {D, 0, 0, L, 2, 2, F, 2, 0}, //4 DLF
        {D, 0, 2, F, 2, 2, R, 2, 0}, //5 DFR
        {D, 2, 2, R, 2, 2, B, 2, 0}, //6 DRB
        {D, 2, 0, B, 2, 2, L, 2, 0} //7 DBL
    };

    private static final int[][] EDGES = {
        {U, 1, 2, R, 0, 1}, //0 UR
        {U, 2, 1, F, 0, 1}, //1 UF
        {U, 1, 0, L, 0, 1}, //2 UL
        {U, 0, 1, B, 0, 1}, //3 UB
        {D, 1, 2, R, 2, 1}, //4 DR
        {D, 0, 1, F, 2, 1}, //5 DF
        {D, 1, 0, L, 2, 1}, //6 DL
        {D, 2, 1, B, 2, 1}, //7 DB
        {F, 1, 2, R, 1, 0}, //8 FR
        {F, 1, 0, L, 1, 2}, //9 FL
        {B, 1, 2, L, 1, 0}, //10 BL
        {R, 1, 2, B, 1, 0}, //11 BR
    };

    private static final int FOUND = -1;
    private static final int INF = Integer.MAX_VALUE;

    private String idaSolution = null;

    public static class Facelet {

        char colour;
        int face;

        public Facelet(char colour, int face) {
            this.colour = colour;
            this.face = face;
        }
    }

    public Solve() {
        cube = readString(SOLVED);
    }

    public Solve(File file) {
        cube = readFile(file);
    }

    public char[][][] readFile(File file) {
        String[] lines = new String[9];
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            for (int i = 0; i < 9; i++) {
                lines[i] = br.readLine();
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return readLines(lines);
    }

    public char[][][] readString(String string) {
        String[] lines = string.split("\n");
        return readLines(lines);
    }

    public char[][][] readLines(String[] lines) {
        char[][][] cube = new char[6][3][3];

        //Up
        for (int i = 0; i < 3; i++) {
            String line = lines[i];
            for (int k = 0; k < 3; k++) {
                cube[U][i][k] = line.charAt(3 + k);
            }
        }

        //Left, Front, Right, Back
        for (int i = 0; i < 3; i++) {
            String line = lines[3 + i];

            for (int k = 0; k < 3; k++) { //Left
                cube[L][i][k] = line.charAt(k);
                cube[F][i][k] = line.charAt(k + 3);
                cube[R][i][k] = line.charAt(k + 6);
                cube[B][i][k] = line.charAt(k + 9);
            }
        }

        //Down
        for (int i = 0; i < 3; i++) {
            String line = lines[6 + i];
            for (int k = 0; k < 3; k++) {
                cube[D][i][k] = line.charAt(3 + k);
            }
        }

        return cube;
    }

    public void printCube() {
        System.out.println(cubeToString(cube));
    }

    public String cubeToString(char[][][] cube) {
        StringBuilder string = new StringBuilder();

        for (int row = 0; row < 3; row++) {
            string.append("   ");
            for (int col = 0; col < 3; col++) {
                string.append(cube[U][row][col]);
            }
            string.append("\n");
        }

        for (int row = 0; row < 3; row++) {
            for (int face = 1; face <= 4; face++) {
                for (int col = 0; col < 3; col++) {
                    string.append(cube[face][row][col]);
                }
            }
            string.append("\n");
        }

        for (int row = 0; row < 3; row++) {
            string.append("   ");
            for (int col = 0; col < 3; col++) {
                string.append(cube[D][row][col]);
            }
            string.append("\n");
        }

        return string.toString();
    }

    public boolean isSolved() {
        char[][][] solvedCube = readString(SOLVED);

        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 3; j++) {
                for (int k = 0; k < 3; k++) {
                    if (cube[i][j][k] != solvedCube[i][j][k]) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private boolean cubesEqual(char[][][] a, char[][][] b) {
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 3; j++) {
                for (int k = 0; k < 3; k++) {
                    if (a[i][j][k] != b[i][j][k]) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public char[][][] cloneCube(char[][][] cube) {
        char[][][] temp = new char[6][3][3];
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 3; j++) {
                for (int k = 0; k < 3; k++) {
                    temp[i][j][k] = cube[i][j][k];
                }
            }
        }
        return temp;
    }

    public void applyMoves(String sequence) {
        String[] moves = sequence.trim().split("\\s+");

        for (String m : moves) {
            if (m.isEmpty()) {
                continue;
            }
            applySingleMove(m);
        }
    }

    // factor out single-move application so solver can reuse it
    private void applySingleMove(String m) {
        switch (m) {
            case "U":
                moveU();
                break;
            case "U'":
                moveUprime();
                break;
            case "U2":
                moveU2();
                break;

            case "D":
                moveD();
                break;
            case "D'":
                moveDprime();
                break;
            case "D2":
                moveD2();
                break;

            case "L":
                moveL();
                break;
            case "L'":
                moveLprime();
                break;
            case "L2":
                moveL2();
                break;

            case "R":
                moveR();
                break;
            case "R'":
                moveRprime();
                break;
            case "R2":
                moveR2();
                break;

            case "F":
                moveF();
                break;
            case "F'":
                moveFprime();
                break;
            case "F2":
                moveF2();
                break;

            case "B":
                moveB();
                break;
            case "B'":
                moveBprime();
                break;
            case "B2":
                moveB2();
                break;
        }
    }

    public int cornerOri(Facelet f1, Facelet f2, Facelet f3, char uCol, char dCol) {
        Facelet ud;
        int pos;

        if (f1.colour == uCol || f1.colour == dCol) {
            ud = f1;
            pos = 0;
        } else if (f2.colour == uCol || f2.colour == dCol) {
            ud = f2;
            pos = 1;
        } else {
            ud = f3;
            pos = 2;
        }

        if (ud.face == U || ud.face == D) {
            return 0;
        }

        return pos;
    }

    public int edgeOri(Facelet f1, Facelet f2, char uCol, char dCol, char fCol, char bCol) {
        Facelet fb, ud;
        boolean f1ud = (f1.colour == uCol || f1.colour == dCol);
        boolean f2ud = (f2.colour == uCol || f2.colour == dCol);

        if (f1ud || f2ud) {
            if (f1ud) {
                ud = f1;
                if (ud.face == U || ud.face == D) {
                    return 0;
                } else {
                    return 1;
                }
            } else {
                ud = f2;
                if (ud.face == U || ud.face == D) {
                    return 0;
                } else {
                    return 1;
                }
            }
        }

        boolean f1fb = (f1.colour == fCol || f1.colour == bCol);

        if (f1fb) {
            fb = f1;
        } else {
            fb = f2;
        }

        if (fb.face == F || fb.face == B) {
            return 0;
        } else {
            return 1;
        }
    }

    public String expandToQuarterTurns(String sequence) {
        sequence = sequence.trim();
        if (sequence.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        String[] moves = sequence.split("\\s+");
        boolean first = true;

        for (String m : moves) {
            if (m.isEmpty()) {
                continue;
            }

            char face = m.charAt(0);    // U, D, L, R, F, B
            int reps = 1;               // default: one quarter turn

            if (m.length() > 1) {
                char mod = m.charAt(1);
                if (mod == '2') {
                    reps = 2;           // X2 -> XX
                } else if (mod == '\'') {
                    reps = 3;           // X' -> XXX
                }
            }

            // append that many quarter turns as one combined token, e.g. "UUU"
            for (int i = 0; i < reps; i++) {
                sb.append(face);
            }
        }

        return sb.toString();
    }

    public char[][][] applyMoveToCube(char[][][] cube, String move) {
        char[][][] prev = this.cube;
        this.cube = cloneCube(cube);
        applySingleMove(move);

        char[][][] result = cloneCube(this.cube);
        this.cube = prev;
        return result;
    }

    //Heuristic
    private int stickerHeuristic(char[][][] state) {
        int mismatch = 0;
        for (int face = 0; face < 6; face++) {
            char center = state[face][1][1];
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (state[face][i][j] != center) {
                        mismatch++;
                    }
                }
            }
        }
        // Each move can fix at most 8 stickers => depth >= ceil(mismatch/8)
        return (mismatch + 7) / 8;
    }

    private int orientationHeuristic(char[][][] state) {
        // center colours
        char uCol = state[U][1][1];
        char dCol = state[D][1][1];
        char fCol = state[F][1][1];
        char bCol = state[B][1][1];

        int twistedCorners = 0;
        int flippedEdges = 0;

        // ---- corners ----
        for (int i = 0; i < CORNERS.length; i++) {
            int[] c = CORNERS[i];

            Facelet f1 = new Facelet(state[c[0]][c[1]][c[2]], c[0]);
            Facelet f2 = new Facelet(state[c[3]][c[4]][c[5]], c[3]);
            Facelet f3 = new Facelet(state[c[6]][c[7]][c[8]], c[6]);

            int ori = cornerOri(f1, f2, f3, uCol, dCol);
            if (ori != 0) {
                twistedCorners++;
            }
        }

        // ---- edges ----
        for (int i = 0; i < EDGES.length; i++) {
            int[] e = EDGES[i];

            Facelet f1 = new Facelet(state[e[0]][e[1]][e[2]], e[0]);
            Facelet f2 = new Facelet(state[e[3]][e[4]][e[5]], e[3]);

            int ori = edgeOri(f1, f2, uCol, dCol, fCol, bCol);
            if (ori != 0) {
                flippedEdges++;
            }
        }

        // Each move can fix at most 4 twisted corners (on F/R/L/B turns)
        int cornerMoves = (twistedCorners + 3) / 4;

        // Each move can flip at most 4 edges (on F/R/L/B turns)
        int edgeMoves = (flippedEdges + 3) / 4;

        return Math.max(cornerMoves, edgeMoves);
    }

    private int permutationHeuristic(char[][][] state) {
        char[][][] solved = readString(SOLVED); // solved reference (small but frequent cost)

        int misplacedCorners = 0;
        int misplacedEdges = 0;

        // ---- corners ----
        for (int i = 0; i < CORNERS.length; i++) {
            int[] c = CORNERS[i];

            // current colours at this corner position
            char[] cur = {
                state[c[0]][c[1]][c[2]],
                state[c[3]][c[4]][c[5]],
                state[c[6]][c[7]][c[8]]
            };

            // solved colours at this corner position
            char[] sol = {
                solved[c[0]][c[1]][c[2]],
                solved[c[3]][c[4]][c[5]],
                solved[c[6]][c[7]][c[8]]
            };

            Arrays.sort(cur);
            Arrays.sort(sol);

            if (!Arrays.equals(cur, sol)) {
                // different colour triple => wrong corner piece in this slot
                misplacedCorners++;
            }
        }

        // ---- edges ----
        for (int i = 0; i < EDGES.length; i++) {
            int[] e = EDGES[i];

            // current colours at this edge position
            char[] cur = {
                state[e[0]][e[1]][e[2]],
                state[e[3]][e[4]][e[5]]
            };

            // solved colours at this edge position
            char[] sol = {
                solved[e[0]][e[1]][e[2]],
                solved[e[3]][e[4]][e[5]]
            };

            Arrays.sort(cur);
            Arrays.sort(sol);

            if (!Arrays.equals(cur, sol)) {
                // different colour pair => wrong edge piece in this slot
                misplacedEdges++;
            }
        }

        // Each move can move up to 4 corners / 4 edges
        int cornerMoves = (misplacedCorners + 3) / 4;
        int edgeMoves = (misplacedEdges + 3) / 4;

        return Math.max(cornerMoves, edgeMoves);
    }

    private int heuristic(char[][][] state) {
        int hSticker = stickerHeuristic(state);
        int hOri = orientationHeuristic(state);
        int hPerm = permutationHeuristic(state);

        // Take the maximum of admissible lower bounds -> still admissible
        return Math.max(hSticker, Math.max(hOri, hPerm));
    }

    public void debugHeuristicCheck() {
        // 1) True solved state
        char[][][] solvedState = readString(SOLVED);
    
        int hStickerSolved = stickerHeuristic(solvedState);
        int hOriSolved     = orientationHeuristic(solvedState);
        int hPermSolved    = permutationHeuristic(solvedState);
        int hSolved        = heuristic(solvedState);
    
        System.out.println("Solved state heuristic components:");
        System.out.println("  sticker = " + hStickerSolved);
        System.out.println("  ori     = " + hOriSolved);
        System.out.println("  perm    = " + hPermSolved);
        System.out.println("  TOTAL   = " + hSolved);
    
        // 2) Current cube (from file or default constructor)
        char[][][] current = cloneCube(this.cube);
    
        int hStickerCur = stickerHeuristic(current);
        int hOriCur     = orientationHeuristic(current);
        int hPermCur    = permutationHeuristic(current);
        int hCur        = heuristic(current);
    
        System.out.println("Current state heuristic components:");
        System.out.println("  sticker = " + hStickerCur);
        System.out.println("  ori     = " + hOriCur);
        System.out.println("  perm    = " + hPermCur);
        System.out.println("  TOTAL   = " + hCur);
    }

    // IDA* search using the heuristic
    public String solveCube(int maxDepth) {
        char[][][] start = cloneCube(this.cube);
        char[][][] solvedCube = readString(SOLVED);

        if (cubesEqual(start, solvedCube)) {
            return ""; // already solved
        }

        idaSolution = null;

        // initial bound is heuristic(start)
        int bound = heuristic(start);
        System.out.println("Initial hSticker=" + stickerHeuristic(start)
        + " hOri=" + orientationHeuristic(start)
        + " hPerm=" + permutationHeuristic(start));

        while (bound <= maxDepth) {
            int t = idaSearch(start, solvedCube, 0, bound, null, "");
            if (t == FOUND) {
                return idaSolution == null ? null : idaSolution.trim();
            }
            if (t == INF) {
                break; // no solution within any higher bound
            }
            bound = t; // next IDA* threshold
        }

        return null; // no solution up to maxDepth
    }

    private int idaSearch(char[][][] state,
            char[][][] solved,
            int g,
            int bound,
            String lastMove,
            String path) {

        int h = heuristic(state);
        int f = g + h;

        if (f > bound) {
            return f; // too expensive for this iteration
        }

        if (cubesEqual(state, solved)) {
            idaSolution = path;
            return FOUND;
        }

        int min = INF;

        for (String move : MOVES) {
// Simple pruning: don't turn the same face twice in a row
            if (lastMove != null && !lastMove.isEmpty()
                    && move.charAt(0) == lastMove.charAt(0)) {
                continue;
            }

            char[][][] next = applyMoveToCube(state, move);
            String newPath = path.isEmpty() ? move : (path + " " + move);

            int t = idaSearch(next, solved, g + 1, bound, move, newPath);

            if (t == FOUND) {
                return FOUND;
            }
            if (t < min) {
                min = t;
            }
        }

        return min;
    }

    // row/col helpers and moves
    public char[] getRow(int face, int row) {
        char[] temp = new char[3];
        for (int i = 0; i < 3; i++) {
            temp[i] = cube[face][row][i];
        }
        return temp;
    }

    public void setRow(int face, int row, char[] val) {
        for (int i = 0; i < 3; i++) {
            cube[face][row][i] = val[i];
        }
    }

    public char[] getCol(int face, int col) {
        char[] temp = new char[3];
        for (int i = 0; i < 3; i++) {
            temp[i] = cube[face][i][col];
        }
        return temp;
    }

    public void setCol(int face, int col, char[] val) {
        for (int i = 0; i < 3; i++) {
            cube[face][i][col] = val[i];
        }
    }

    public char[] reverse(char[] array) {
        char temp = array[0];
        array[0] = array[2];
        array[2] = temp;
        return array;
    }

    public void rotateCW(int face) {
        char[][] temp = cube[face];

        char prev = temp[0][0]; //corners
        temp[0][0] = temp[2][0];
        temp[2][0] = temp[2][2];
        temp[2][2] = temp[0][2];
        temp[0][2] = prev;

        prev = temp[0][1]; //edges
        temp[0][1] = temp[1][0];
        temp[1][0] = temp[2][1];
        temp[2][1] = temp[1][2];
        temp[1][2] = prev;
    }

    public void moveU() {
        rotateCW(U);

        char[] f = getRow(F, 0);
        char[] r = getRow(R, 0);
        char[] b = getRow(B, 0);
        char[] l = getRow(L, 0);

        setRow(F, 0, r);
        setRow(L, 0, f);
        setRow(B, 0, l);
        setRow(R, 0, b);
    }

    public void moveU2() {
        moveU();
        moveU();
    }

    public void moveUprime() {
        moveU();
        moveU();
        moveU();
    }

    public void moveL() {
        rotateCW(L);

        char[] u = getCol(U, 0);
        char[] f = getCol(F, 0);
        char[] d = getCol(D, 0);
        char[] b = getCol(B, 2);

        setCol(F, 0, u);
        setCol(D, 0, f);
        setCol(B, 2, reverse(d));
        setCol(U, 0, reverse(b));
    }

    public void moveL2() {
        moveL();
        moveL();
    }

    public void moveLprime() {
        moveL();
        moveL();
        moveL();
    }

    public void moveF() {
        rotateCW(F);

        char[] u = getRow(U, 2);
        char[] r = getCol(R, 0);
        char[] d = getRow(D, 0);
        char[] l = getCol(L, 2);

        setCol(R, 0, u);
        setRow(D, 0, reverse(r));
        setCol(L, 2, reverse(d));
        setRow(U, 2, reverse(l));
    }

    public void moveFprime() {
        moveF();
        moveF();
        moveF();
    }

    public void moveF2() {
        moveF();
        moveF();
    }

    public void moveR() {
        rotateCW(R);

        char[] u = getCol(U, 2);
        char[] b = getCol(B, 0);
        char[] d = getCol(D, 2);
        char[] f = getCol(F, 2);

        setCol(U, 2, f);
        setCol(F, 2, d);
        setCol(D, 2, reverse(b));
        setCol(B, 0, reverse(u));
    }

    public void moveRprime() {
        moveR();
        moveR();
        moveR();
    }

    public void moveR2() {
        moveR();
        moveR();
    }

    public void moveB() {
        rotateCW(B);

        char[] u = getRow(U, 0);
        char[] l = getCol(L, 0);
        char[] d = getRow(D, 2);
        char[] r = getCol(R, 2);

        setRow(U, 0, r);
        setCol(R, 2, d);
        setRow(D, 2, l);
        setCol(L, 0, u);
    }

    public void moveBprime() {
        moveB();
        moveB();
        moveB();
    }

    public void moveB2() {
        moveB();
        moveB();
    }

    public void moveD() {
        rotateCW(D);

        char[] f = getRow(F, 2);
        char[] r = getRow(R, 2);
        char[] b = getRow(B, 2);
        char[] l = getRow(L, 2);

        setRow(R, 2, f);
        setRow(B, 2, reverse(r));
        setRow(L, 2, reverse(b));
        setRow(F, 2, l);
    }

    public void moveDprime() {
        moveD();
        moveD();
        moveD();
    }

    public void moveD2() {
        moveD();
        moveD();
    }
}
