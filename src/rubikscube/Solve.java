package rubikscube;

import java.io.*;
import java.util.ArrayDeque;
import java.util.Arrays;

public class Solve {

    Corner[] corners = new Corner[8];
    Edge[] edges = new Edge[12];
    Center[] center = new Center[8];

    Corner[] solvedCorners = new Corner[8];
    Edge[] solvedEdges = new Edge[12];
    Center[] solvedCenter = new Center[8];

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

    private static final String[] MOVES = {
        "U", "U'", "U2",
        "D", "D'", "D2",
        "L", "L'", "L2",
        "R", "R'", "R2",
        "F", "F'", "F2",
        "B", "B'", "B2"
    };

    private static final String[] PHASE2_MOVES = MOVES;

    public static class Node {

        char[][][] state;
        String moves;
        int depth;

        Node(char[][][] state, String moves, int depth) {
            this.state = state;
            this.moves = moves;
            this.depth = depth;
        }
    }

    public static class Facelet {

        char colour;
        int face;

        public Facelet(char colour, int face) {
            this.colour = colour;
            this.face = face;
        }
    }

    public static class Center {

        int index;
        Facelet f;

        public Center(int index, Facelet f) {
            this.index = index;
            this.f = f;
        }
    }

    public static class Edge {

        int index;
        int ori;
        Facelet f1, f2;

        public Edge(int index, int ori, Facelet f1, Facelet f2) {
            this.index = index;
            this.ori = ori;
            this.f1 = f1;
            this.f2 = f2;
        }
    }

    public static class Corner {

        int index;
        int ori;
        Facelet f1, f2, f3;

        public Corner(int index, int ori, Facelet f1, Facelet f2, Facelet f3) {
            this.index = index;
            this.ori = ori;
            this.f1 = f1;
            this.f2 = f2;
            this.f3 = f3;
        }
    }

    /*
              UP
        LEFT FRONT RIGHT BACK
             DOWN
     */
    public Solve() {
        cube = readString(SOLVED);
        initState(cube, corners, edges, center);
        char[][][] solvedCube = readString(SOLVED);
        initState(solvedCube, solvedCorners, solvedEdges, solvedCenter);
    }

    public Solve(File file) {
        cube = readFile(file);
        initState(cube, corners, edges, center);
        char[][][] solvedCube = readString(SOLVED);
        initState(solvedCube, solvedCorners, solvedEdges, solvedCenter);
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

        for (int i = 0; i < 3; i++) { //Down
            String line = lines[6 + i];
            for (int k = 0; k < 3; k++) {
                cube[D][i][k] = line.charAt(3 + k);
            }
        }

        return cube;

    }

    public void initState(char[][][] cube, Corner[] cornerArr, Edge[] edgeArr, Center[] centerArr) {
        for (int i = 0; i < 6; i++) {//center
            char col = cube[i][1][1];
            centerArr[i] = new Center(i, new Facelet(col, i));
        }

        char uCol = centerArr[U].f.colour;
        char dCol = centerArr[D].f.colour;
        char fCol = centerArr[F].f.colour;
        char bCol = centerArr[B].f.colour;

        for (int i = 0; i < CORNERS.length; i++) {//corners
            int[] c = CORNERS[i];

            Facelet f1 = new Facelet(cube[c[0]][c[1]][c[2]], c[0]);
            Facelet f2 = new Facelet(cube[c[3]][c[4]][c[5]], c[3]);
            Facelet f3 = new Facelet(cube[c[6]][c[7]][c[8]], c[6]);

            int ori = cornerOri(f1, f2, f3, uCol, dCol);

            cornerArr[i] = new Corner(i, ori, f1, f2, f3);
        }

        for (int i = 0; i < EDGES.length; i++) {//edges
            int[] e = EDGES[i];
            Facelet f1 = new Facelet(cube[e[0]][e[1]][e[2]], e[0]);
            Facelet f2 = new Facelet(cube[e[3]][e[4]][e[5]], e[3]);

            int ori = edgeOri(f1, f2, uCol, dCol, fCol, bCol);

            edgeArr[i] = new Edge(i, ori, f1, f2);
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

    public boolean isFirstSolved(char[][][] cube) {
        char up = cube[U][1][1];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (cube[U][i][j] != up) {
                    return false;
                }
            }
        }

        for (int i = 1; i < 5; i++) {
            char face = cube[i][1][1];
            for (int j = 0; j < 3; j++) {
                if (cube[i][0][j] != face) {
                    return false;
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

    /*
        Kociembas
     */
    public static class CubieCube {

        public int[] cp = new int[8];
        public int[] co = new int[8];

        public int[] ep = new int[12];
        public int[] eo = new int[12];

        public CubieCube() {
        }

        public CubieCube(CubieCube other) {
            System.arraycopy(other.cp, 0, cp, 0, 8);
            System.arraycopy(other.co, 0, co, 0, 8);
            System.arraycopy(other.ep, 0, ep, 0, 12);
            System.arraycopy(other.eo, 0, eo, 0, 12);
        }
    }

    public CubieCube toCubieCube() {
        CubieCube cc = new CubieCube();

        // corners
        for (int i = 0; i < 8; i++) {
            Corner c = corners[i];
            cc.cp[i] = c.index;
            cc.co[i] = c.ori;
        }

        // edges
        for (int i = 0; i < 12; i++) {
            Edge e = edges[i];
            cc.ep[i] = e.index;
            cc.eo[i] = e.ori;
        }

        return cc;
    }

    /*
        PHASE 1 Pruning Tables
     */
    private static final int EO_SIZE = 2048;
    private static final int CO_SIZE = 2187;

    private static int[] edgeOriPrune = null;
    private static int[] cornerOriPrune = null;
    private static boolean pruningInitialized = false;

    private static CubieCube solvedCubie() {
        CubieCube c = new CubieCube();
        for (int i = 0; i < 8; i++) {
            c.cp[i] = i;
            c.co[i] = 0;
        }
        for (int i = 0; i < 12; i++) {
            c.ep[i] = i;
            c.eo[i] = 0;
        }
        return c;
    }

    private static int edgeOriIndex(CubieCube cc) {
        int idx = 0;
        int pow = 1;
        for (int i = 0; i < 11; i++) {
            idx += cc.eo[i] * pow;
            pow <<= 1;
        }
        return idx;
    }

    private static int cornerOriIndex(CubieCube cc) {
        int idx = 0;
        int pow = 1;
        for (int i = 0; i < 7; i++) {
            idx += cc.co[i] * pow;
            pow *= 3;
        }
        return idx;
    }

    private static void cycle4(int[] arr, int a, int b, int c, int d) {
        int tmp = arr[a];
        arr[a] = arr[b];
        arr[b] = arr[c];
        arr[c] = arr[d];
        arr[d] = tmp;
    }

    private static void twistCornerArr(int[] co, int idx, int x) {
        co[idx] = (co[idx] + x + 3) % 3;
    }

    private static void flipEdgeArr(int[] eo, int idx) {
        eo[idx] ^= 1;
    }

    private static void moveUQuarter(CubieCube c) {
        cycle4(c.cp, 0, 1, 2, 3);
        cycle4(c.ep, 0, 1, 2, 3);
    }

    private static void moveDQuarter(CubieCube c) {
        cycle4(c.cp, 4, 5, 6, 7);
        cycle4(c.ep, 4, 5, 6, 7);
    }

    private static void moveLQuarter(CubieCube c) {
        cycle4(c.cp, 3, 0, 4, 7);
        twistCornerArr(c.co, 3, 1);
        twistCornerArr(c.co, 0, 2);
        twistCornerArr(c.co, 4, 1);
        twistCornerArr(c.co, 7, 2);

        cycle4(c.ep, 2, 9, 6, 10);
        // no edge flips
    }

    private static void moveRQuarter(CubieCube c) {
        cycle4(c.cp, 1, 2, 6, 5);
        twistCornerArr(c.co, 1, 1);
        twistCornerArr(c.co, 2, 2);
        twistCornerArr(c.co, 6, 1);
        twistCornerArr(c.co, 5, 2);

        cycle4(c.ep, 0, 11, 4, 8);
        // no edge flips
    }

    private static void moveFQuarter(CubieCube c) {
        cycle4(c.cp, 0, 1, 5, 4);
        twistCornerArr(c.co, 0, 1);
        twistCornerArr(c.co, 1, 2);
        twistCornerArr(c.co, 5, 1);
        twistCornerArr(c.co, 4, 2);

        cycle4(c.ep, 1, 8, 5, 9);
        flipEdgeArr(c.eo, 1);
        flipEdgeArr(c.eo, 8);
        flipEdgeArr(c.eo, 5);
        flipEdgeArr(c.eo, 9);
    }

    private static void moveBQuarter(CubieCube c) {
        cycle4(c.cp, 2, 3, 7, 6);
        twistCornerArr(c.co, 2, 1);
        twistCornerArr(c.co, 3, 2);
        twistCornerArr(c.co, 7, 1);
        twistCornerArr(c.co, 6, 2);

        cycle4(c.ep, 3, 10, 7, 11);
        flipEdgeArr(c.eo, 3);
        flipEdgeArr(c.eo, 10);
        flipEdgeArr(c.eo, 7);
        flipEdgeArr(c.eo, 11);
    }

    private static void applyQuarterTurn(CubieCube c, char face) {
        switch (face) {
            case 'U':
                moveUQuarter(c);
                break;
            case 'D':
                moveDQuarter(c);
                break;
            case 'L':
                moveLQuarter(c);
                break;
            case 'R':
                moveRQuarter(c);
                break;
            case 'F':
                moveFQuarter(c);
                break;
            case 'B':
                moveBQuarter(c);
                break;
        }
    }

    private static CubieCube applyMoveCubie(CubieCube cc, int moveIndex) {
        char face;
        int times;
        switch (moveIndex) {
            case 0:
                face = 'U';
                times = 1;
                break;
            case 1:
                face = 'U';
                times = 3;
                break;
            case 2:
                face = 'U';
                times = 2;
                break;

            case 3:
                face = 'D';
                times = 1;
                break;
            case 4:
                face = 'D';
                times = 3;
                break;
            case 5:
                face = 'D';
                times = 2;
                break;

            case 6:
                face = 'L';
                times = 1;
                break;
            case 7:
                face = 'L';
                times = 3;
                break;
            case 8:
                face = 'L';
                times = 2;
                break;

            case 9:
                face = 'R';
                times = 1;
                break;
            case 10:
                face = 'R';
                times = 3;
                break;
            case 11:
                face = 'R';
                times = 2;
                break;

            case 12:
                face = 'F';
                times = 1;
                break;
            case 13:
                face = 'F';
                times = 3;
                break;
            case 14:
                face = 'F';
                times = 2;
                break;

            case 15:
                face = 'B';
                times = 1;
                break;
            case 16:
                face = 'B';
                times = 3;
                break;
            case 17:
                face = 'B';
                times = 2;
                break;
            default:
                face = 'U';
                times = 0;
        }

        CubieCube res = new CubieCube(cc);
        for (int i = 0; i < times; i++) {
            applyQuarterTurn(res, face);
        }
        return res;
    }

    private static void initPruningTables() {
        if (pruningInitialized) {
            return;
        }

        System.out.println("Initializing Phase 1 orientation pruning tables...");

        // Edge orientation table
        edgeOriPrune = new int[EO_SIZE];
        Arrays.fill(edgeOriPrune, -1);

        ArrayDeque<CubieCube> qEO = new ArrayDeque<>();
        CubieCube solved = solvedCubie();
        int eoStart = edgeOriIndex(solved);
        edgeOriPrune[eoStart] = 0;
        qEO.add(solved);

        while (!qEO.isEmpty()) {
            CubieCube cur = qEO.poll();
            int curIdx = edgeOriIndex(cur);
            int curDist = edgeOriPrune[curIdx];

            for (int m = 0; m < 18; m++) {
                CubieCube next = applyMoveCubie(cur, m);
                int idx = edgeOriIndex(next);
                if (edgeOriPrune[idx] == -1) {
                    edgeOriPrune[idx] = curDist + 1;
                    qEO.add(next);
                }
            }
        }

        // Corner orientation table
        cornerOriPrune = new int[CO_SIZE];
        Arrays.fill(cornerOriPrune, -1);

        ArrayDeque<CubieCube> qCO = new ArrayDeque<>();
        CubieCube solved2 = solvedCubie();
        int coStart = cornerOriIndex(solved2);
        cornerOriPrune[coStart] = 0;
        qCO.add(solved2);

        while (!qCO.isEmpty()) {
            CubieCube cur = qCO.poll();
            int curIdx = cornerOriIndex(cur);
            int curDist = cornerOriPrune[curIdx];

            for (int m = 0; m < 18; m++) {
                CubieCube next = applyMoveCubie(cur, m);
                int idx = cornerOriIndex(next);
                if (cornerOriPrune[idx] == -1) {
                    cornerOriPrune[idx] = curDist + 1;
                    qCO.add(next);
                }
            }
        }

        pruningInitialized = true;
        System.out.println("Pruning tables initialized.");
    }

    /*
        PHASE 1:
     */
    private boolean allEdgesOriented(CubieCube cc) {
        for (int i = 0; i < 12; i++) {
            if (cc.eo[i] != 0) {
                return false;
            }
        }
        return true;
    }

    private boolean allCornersOriented(CubieCube cc) {
        for (int i = 0; i < 8; i++) {
            if (cc.co[i] != 0) {
                return false;
            }
        }
        return true;
    }

    private boolean isInG1(CubieCube cc) {
        return allEdgesOriented(cc) && allCornersOriented(cc);
    }

    public int phase1Heuristic(CubieCube cc) {
        initPruningTables();

        int eoIdx = edgeOriIndex(cc);
        int coIdx = cornerOriIndex(cc);

        int hEO = edgeOriPrune[eoIdx];
        int hCO = cornerOriPrune[coIdx];

        return Math.max(hEO, hCO);
    }

    public String solvePhase1(int maxDepth) {
        CubieCube start = toCubieCube();

        if (isInG1(start)) {
            return "";
        }

        int bound = phase1Heuristic(start);
        if (bound < 1) {
            bound = 1;
        }

        System.out.println("Phase1: initial heuristic bound = " + bound);

        while (bound <= maxDepth) {
            System.out.println("Phase1: trying bound = " + bound);
            String result = phase1DFS("", 0, bound, 'X');
            if (result != null) {
                System.out.println("Phase1: found G1 at bound " + bound);
                return result;
            }
            bound++;
        }

        return null;
    }

    private String phase1DFS(String moves, int g, int bound, char lastFace) {
        CubieCube cc = toCubieCube();

        if (isInG1(cc)) {
            return moves;
        }

        int h = phase1Heuristic(cc);
        int f = g + h;
        if (f > bound) {
            return null;
        }
        if (g == bound) {
            return null;
        }

        for (String move : MOVES) {
            char face = move.charAt(0);
            if (face == lastFace) {
                continue; // prune same-face repetition
            }
            applyMove(move);
            String newMoves = moves.isEmpty() ? move : moves + " " + move;

            String res = phase1DFS(newMoves, g + 1, bound, face);

            undoMove(move);

            if (res != null) {
                return res;
            }
        }

        return null;
    }

    /*
        PHASE 2:
     */
    private int phase2Heuristic(CubieCube cc) {
        int wrong = 0;

        for (int i = 0; i < 8; i++) {
            if (cc.cp[i] != i) {
                wrong++;
            }
        }
        for (int i = 0; i < 12; i++) {
            if (cc.ep[i] != i) {
                wrong++;
            }
        }

        if (wrong == 0) {
            return 0;
        }
        return (wrong + 3) / 4;
    }

    public String solvePhase2(int maxDepth) {
        if (isSolved()) {
            return "";
        }

        CubieCube cc = toCubieCube();
        int bound = phase2Heuristic(cc);
        if (bound < 1) {
            bound = 1;
        }

        System.out.println("Phase2: initial heuristic bound = " + bound);

        while (bound <= maxDepth) {
            System.out.println("Phase2: trying bound = " + bound);
            String res = phase2DFS("", 0, bound, 'X');
            if (res != null) {
                System.out.println("Phase2: found solution at bound " + bound);
                return res;
            }
            bound++;
        }

        System.out.println("Phase2: no solution within bound " + maxDepth);
        return null;
    }

    private String phase2DFS(String moves, int g, int bound, char lastFace) {
        if (isSolved()) {
            return moves;
        }

        CubieCube cc = toCubieCube();
        int h = phase2Heuristic(cc);
        int f = g + h;
        if (f > bound) {
            return null;
        }
        if (g == bound) {
            return null;
        }

        for (String move : PHASE2_MOVES) {
            char face = move.charAt(0);
            if (face == lastFace) {
                continue;
            }

            applyMove(move);
            String newMoves = moves.isEmpty() ? move : moves + " " + move;

            String res = phase2DFS(newMoves, g + 1, bound, face);

            undoMove(move);

            if (res != null) {
                return res;
            }
        }

        return null;
    }

    public String solveKociemba(int maxDepthPhase1, int maxDepthPhase2) {
        // Backup original state so we can verify candidate solution
        char[][][] cubeBackup = cloneCube(this.cube);
        Corner[] cornersBackup = new Corner[8];
        Edge[] edgesBackup = new Edge[12];
        Center[] centerBackup = new Center[6];

        for (int i = 0; i < 8; i++) {
            Corner c = this.corners[i];
            cornersBackup[i] = new Corner(
                    c.index, c.ori,
                    new Facelet(c.f1.colour, c.f1.face),
                    new Facelet(c.f2.colour, c.f2.face),
                    new Facelet(c.f3.colour, c.f3.face)
            );
        }
        for (int i = 0; i < 12; i++) {
            Edge e = this.edges[i];
            edgesBackup[i] = new Edge(
                    e.index, e.ori,
                    new Facelet(e.f1.colour, e.f1.face),
                    new Facelet(e.f2.colour, e.f2.face)
            );
        }
        for (int i = 0; i < 6; i++) {
            Center cen = this.center[i];
            centerBackup[i] = new Center(
                    cen.index,
                    new Facelet(cen.f.colour, cen.f.face)
            );
        }

        // ---- Phase 1: reach G1 (all oriented) ----
        String p1 = solvePhase1(maxDepthPhase1);
        if (p1 == null) {
            return null; // can't reach G1
        }

        if (!p1.isEmpty()) {
            applyMoves(p1);
        }

        // ---- Phase 2: solve from G1 ----
        String p2 = solvePhase2(maxDepthPhase2);

        // Build candidate
        String candidate;
        if (p2 == null || p2.trim().isEmpty()) {
            candidate = p1;
        } else if (p1.isEmpty()) {
            candidate = p2;
        } else {
            candidate = p1 + " " + p2;
        }

        // ---- Verify candidate actually solves from original state ----
        this.cube = cloneCube(cubeBackup);
        this.corners = cornersBackup;
        this.edges = edgesBackup;
        this.center = centerBackup;

        if (candidate != null && !candidate.trim().isEmpty()) {
            applyMoves(candidate);
            if (isSolved()) {
                return candidate; // ✅ real solution
            } else {
                return null;      // ❌ candidate failed, don't lie
            }
        }

        return null;
    }


    /*
        Getters, Setters, Rotation functions
     */
    public void applyMove(String move) {
        switch (move) {
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

    public void undoMove(String move) {
        switch (move) {
            case "U":
                moveUprime();
                break;
            case "U'":
                moveU();
                break;
            case "U2":
                moveU2();
                break;

            case "D":
                moveDprime();
                break;
            case "D'":
                moveD();
                break;
            case "D2":
                moveD2();
                break;

            case "L":
                moveLprime();
                break;
            case "L'":
                moveL();
                break;
            case "L2":
                moveL2();
                break;

            case "R":
                moveRprime();
                break;
            case "R'":
                moveR();
                break;
            case "R2":
                moveR2();
                break;

            case "F":
                moveFprime();
                break;
            case "F'":
                moveF();
                break;
            case "F2":
                moveF2();
                break;

            case "B":
                moveBprime();
                break;
            case "B'":
                moveB();
                break;
            case "B2":
                moveB2();
                break;
        }
    }

    public void applyMoves(String sequence) {
        if (sequence == null) {
            return;
        }

        sequence = sequence.trim();
        if (sequence.isEmpty()) {
            return;
        }

        String[] moves = sequence.split("\\s+");

        for (String m : moves) {
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

    }

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

    public void rotateCorners(int c1, int c2, int c3, int c4) {
        Corner temp = corners[c4];
        corners[c4] = corners[c3];
        corners[c3] = corners[c2];
        corners[c2] = corners[c1];
        corners[c1] = temp;
    }

    public void rotateEdges(int e1, int e2, int e3, int e4) {
        Edge temp = edges[e4];
        edges[e4] = edges[e3];
        edges[e3] = edges[e2];
        edges[e2] = edges[e1];
        edges[e1] = temp;
    }

    public void twistCorner(int index, int x) {
        corners[index].ori = (corners[index].ori + x + 3) % 3;
    }

    public void flipEdge(int index) {
        edges[index].ori ^= 1;
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

        rotateCorners(0, 1, 2, 3);
        rotateEdges(0, 1, 2, 3);
    }

    public void moveU2() {
        moveU();
        moveU();
    }

    public void moveUprime() { //change later if too slow
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

        rotateCorners(3, 0, 4, 7);
        twistCorner(3, 1);
        twistCorner(0, 2);
        twistCorner(4, 1);
        twistCorner(7, 2);

        rotateEdges(2, 9, 6, 10);
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

        rotateCorners(0, 1, 5, 4);
        twistCorner(0, 1);
        twistCorner(1, 2);
        twistCorner(5, 1);
        twistCorner(4, 2);

        rotateEdges(1, 8, 5, 9);
        flipEdge(1);
        flipEdge(8);
        flipEdge(5);
        flipEdge(9);
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

        rotateCorners(1, 2, 6, 5);
        twistCorner(1, 1);
        twistCorner(2, 2);
        twistCorner(6, 1);
        twistCorner(5, 2);

        rotateEdges(0, 11, 4, 8);
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

        rotateCorners(2, 3, 7, 6);
        twistCorner(2, 1);
        twistCorner(3, 2);
        twistCorner(7, 1);
        twistCorner(6, 2);

        rotateEdges(3, 10, 7, 11);
        flipEdge(3);
        flipEdge(10);
        flipEdge(7);
        flipEdge(11);
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

        rotateCorners(4, 5, 6, 7);
        rotateEdges(4, 5, 6, 7);
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
