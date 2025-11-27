package rubikscube;

import java.io.File;

public class Solve {

    Corner[] corners = new Corner[8];
    Edge[] edges = new Edge[12];
    Center[] center = new Center[8];

    Corner[] solvedCorners = new Corner[8];
    Edge[] solvedEdges = new Edge[12];
    Center[] solvedCenter = new Center[8];

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
        {U,2,0, F,0,0, L,0,2}, //0 UFL
        {U,2,2, R,0,0, F,0,2}, //1 URF
        {U,0,2, B,0,0, R,0,2}, //2 UBR
        {U,0,0, L,0,0, B,0,2}, //3 ULB
        {D,0,0, L,2,2, F,2,0}, //4 DLF
        {D,0,2, F,2,2, R,2,0}, //5 DFR
        {D,2,2, R,2,2, B,2,0}, //6 DRB
        {D,2,0, B,2,2, L,2,0} //7 DBL
    };

    private static final int[][] EDGES = {
        {U,1,2, R,0,1}, //0 UR
        {U,2,1, F,0,1}, //1 UF
        {U,1,0, L,0,1}, //2 UL
        {U,0,1, B,0,1}, //3 UB
        {D,1,2, R,2,1}, //4 DR
        {D,0,1, F,2,1}, //5 DF
        {D,1,0, L,2,1}, //6 DL
        {D,2,1, B,2,1}, //7 DB
        {F,1,2, R,1,0}, //8 FR
        {F,1,0, L,1,2}, //9 FL
        {B,1,2, L,1,0}, //10 BL
        {R,1,2, B,1,0}, //11 BR
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
    }

    public Solve(File file) {
    }

    private void solvedCube(){
        
    }
}
