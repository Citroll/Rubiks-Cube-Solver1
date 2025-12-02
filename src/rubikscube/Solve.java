package rubikscube;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

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
<<<<<<< HEAD
        {D, 2, 0, B, 2, 2, L, 2, 0} //7 DBL
=======
        {D, 2, 0, B, 2, 2, L, 2, 0}  //7 DBL
>>>>>>> parent of aab0de8 (fuck)
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

    // All face turns we'll search over
    private static final String[] MOVES = {
        "U", "U'", "U2",
        "D", "D'", "D2",
        "L", "L'", "L2",
        "R", "R'", "R2",
        "F", "F'", "F2",
        "B", "B'", "B2"
    };

    public static class Facelet {
<<<<<<< HEAD

=======
>>>>>>> parent of aab0de8 (fuck)
        char colour;
        int face;

        public Facelet(char colour, int face) {
            this.colour = colour;
            this.face = face;
        }
    }

    public static class Center {
<<<<<<< HEAD

=======
>>>>>>> parent of aab0de8 (fuck)
        int index;
        Facelet f;

        public Center(int index, Facelet f) {
            this.index = index;
            this.f = f;
        }
    }

    public static class Edge {
<<<<<<< HEAD

=======
>>>>>>> parent of aab0de8 (fuck)
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
<<<<<<< HEAD

=======
>>>>>>> parent of aab0de8 (fuck)
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

        //Down
        for (int i = 0; i < 3; i++) {
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

    // --- helper used by the solver for arbitrary states ---
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
<<<<<<< HEAD
            if (m.isEmpty()) {
                continue;
            }
=======
            if (m.isEmpty()) continue;
>>>>>>> parent of aab0de8 (fuck)
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

    public char[][][] applyMoveToCube(char[][][] cube, String move) {
        char[][][] prev = this.cube;
        this.cube = cloneCube(cube);
        applySingleMove(move);

        char[][][] result = cloneCube(this.cube);
        this.cube = prev;
        return result;
    }
<<<<<<< HEAD

    public static String expandMove(String move) {
        move = move.trim();
        if (move.isEmpty()) {
            return "";
        }

        char face = move.charAt(0); // U, D, L, R, F, B

        if (move.length() == 1) {
            // Just "U"
            return String.valueOf(face);
        }

        char suffix = move.charAt(1);
        switch (suffix) {
            case '2': // U2 -> "U U"
                return face + "" + face;
            case '\'': // U' -> "U U U"
                return face + "" + face + "" + face;
            default:
                // Unknown suffix, just return as-is
                return move;
        }
    }

    public static String expandSequence(String sequence) {
        StringBuilder sb = new StringBuilder();
        String[] tokens = sequence.trim().split("\\s+");

        for (String t : tokens) {
            if (t.isEmpty()) {
                continue;
            }
            String expanded = expandMove(t);
            if (expanded.isEmpty()) {
                continue;
            }
            sb.append(expanded);
        }

        return sb.toString();
    }

    // A* search node
    private static class Node {

        char[][][] state;
        String key;      // serialized state (for HashMap/HashSet)
        int g;           // cost so far
        int f;           // g + h
        String lastMove; // last move used to get here (for pruning)

        Node(char[][][] state, String key, int g, int f, String lastMove) {
            this.state = state;
            this.key = key;
            this.g = g;
            this.f = f;
            this.lastMove = lastMove;
        }
    }

// To reconstruct the path: where did this state come from?
    private static class ParentInfo {

        String parentKey;
        String move; // move that took parent -> this

        ParentInfo(String parentKey, String move) {
            this.parentKey = parentKey;
            this.move = move;
        }
    }

    //Heuristic
    private int heuristic(char[][][] state) {
        int mismatch = 0;
        for (int face = 0; face < 6; face++) {
            char center = state[face][1][1];
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    // skip centers (they already match center by definition)
                    if (i == 1 && j == 1) {
                        continue;
                    }

                    if (state[face][i][j] != center) {
                        mismatch++;
                    }
                }
            }
        }

        // ceil(mismatch / 8.0) using integer math
        return (mismatch + 7) / 8;
    }

    public String solveCubeAStar() {
        // Starting state
        char[][][] start = cloneCube(this.cube);
        String startKey = cubeToString(start);

        // Goal state
        char[][][] goalState = readString(SOLVED);
        String goalKey = cubeToString(goalState);

        if (startKey.equals(goalKey)) {
            return ""; // already solved
        }

        // Priority queue ordered by lowest f = g + h
        PriorityQueue<Node> open = new PriorityQueue<>(Comparator.comparingInt(n -> n.f));

        // gScore: best known cost from start to this state
        Map<String, Integer> gScore = new HashMap<>();

        // parent map for path reconstruction
        Map<String, ParentInfo> parent = new HashMap<>();

        // closed set of fully-explored states
        Set<String> closed = new HashSet<>();

        int h0 = heuristic(start);
        Node startNode = new Node(start, startKey, 0, h0, null);

        open.add(startNode);
        gScore.put(startKey, 0);

        while (!open.isEmpty()) {
            Node current = open.poll();

            // If we've already found a better path to this state, skip this stale entry
            Integer bestG = gScore.get(current.key);
            if (bestG != null && current.g > bestG) {
                continue;
            }

            // Goal test
            if (current.key.equals(goalKey)) {
                // reconstruct compact path, then expand it to quarter turns
                String compact = reconstructPath(parent, goalKey).trim();
                return expandSequence(compact).trim();
            }

            if (!closed.add(current.key)) {
                continue; // already processed
            }

            // Expand neighbors
            for (String move : MOVES) {

                // Simple pruning: don't move the same face twice in a row
                if (current.lastMove != null && !current.lastMove.isEmpty()) {
                    if (move.charAt(0) == current.lastMove.charAt(0)) {
                        continue;
                    }
                }

                char[][][] nextState = applyMoveToCube(current.state, move);
                String nextKey = cubeToString(nextState);

                int tentativeG = current.g + 1;

                if (closed.contains(nextKey)) {
                    continue;
                }

                int knownG = gScore.getOrDefault(nextKey, Integer.MAX_VALUE);
                if (tentativeG >= knownG) {
                    continue; // not a better path
                }

                // This is the best path so far to nextKey
                gScore.put(nextKey, tentativeG);
                parent.put(nextKey, new ParentInfo(current.key, move));

                int h = heuristic(nextState);
                int f = tentativeG + h;

                Node neighbor = new Node(nextState, nextKey, tentativeG, f, move);
                open.add(neighbor);
            }
        }

        // No solution found (this shouldn't happen for valid scrambles)
        return null;
    }

    private String reconstructPath(Map<String, ParentInfo> parent, String goalKey) {
        List<String> moves = new ArrayList<>();
        String currentKey = goalKey;

        while (parent.containsKey(currentKey)) {
            ParentInfo info = parent.get(currentKey);
            moves.add(info.move);
            currentKey = info.parentKey;
        }

        Collections.reverse(moves);
        return String.join(" ", moves);
    }

    // row/col helpers and moves
    public char[] getRow(int face, int row) {
        char[] temp = new char[3];
        for (int i = 0; i < 3; i++) {
            temp[i] = cube[face][row][i];
        }
        return temp;
    }

=======
    
    //Heuristic
    private int heuristic(char[][][] state) {
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
        return mismatch;
    }
    
    //IDDFS
    public String solveCube(int maxDepth) {
        char[][][] start = cloneCube(this.cube);
        char[][][] solvedCube = readString(SOLVED);

        if (cubesEqual(start, solvedCube)) {
            return ""; // already solved
        }

        for (int depth = 1; depth <= maxDepth; depth++) {
            String result = dfsSolve(start, solvedCube, "", depth, null);
            if (result != null) {
                return result.trim();
            }
        }
        return null; // no solution within maxDepth
    }

    private String dfsSolve(char[][][] state,
                            char[][][] solved,
                            String path,
                            int depth,
                            String lastMove) {
        if (depth == 0) {
            if (cubesEqual(state, solved)) {
                return path;
            }
            return null;
        }

        for (String move : MOVES) {
            // Simple pruning: don't turn the same face twice in a row
            if (lastMove != null && !lastMove.isEmpty()) {
                if (move.charAt(0) == lastMove.charAt(0)) {
                    continue;
                }
            }

            char[][][] next = applyMoveToCube(state, move);
            String newPath = path.isEmpty() ? move : (path + " " + move);

            String result = dfsSolve(next, solved, newPath, depth - 1, move);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    // row/col helpers and moves

    public char[] getRow(int face, int row) {
        char[] temp = new char[3];
        for (int i = 0; i < 3; i++) {
            temp[i] = cube[face][row][i];
        }
        return temp;
    }

>>>>>>> parent of aab0de8 (fuck)
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
