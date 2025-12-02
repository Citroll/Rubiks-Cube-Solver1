package rubikscube;

import java.io.*;

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

    public void printCube(){
        System.out.println(cubeToString(cube));
    }

    public String cubeToString(char[][][] cube){
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
}
