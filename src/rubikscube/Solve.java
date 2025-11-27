package rubikscube;

import java.io.File;

public class Solve {
    Corner[] corners = new Corner[8];
    Edge[] edges = new Edge[12];
    Center[] center = new Center[8];

    Corner[] solvedCorners = new Corner[8];
    Edge[] solvedEdges = new Edge[12];
    Center[] solvedCenter = new Center[8];

    private static final int UP = 0, LEFT = 1, FRONT = 2, RIGHT = 3, BACK = 4, DOWN = 5;
    private static final String SOLVED =
            "   OOO\n" +
            "   OOO\n" +
            "   OOO\n" +
            "GGGWWWBBBYYY\n" +
            "GGGWWWBBBYYY\n" +
            "GGGWWWBBBYYY\n" +
            "   RRR\n" +
            "   RRR\n" +
            "   RRR\n";

    public static class Facelet{
        char colour;
        int face;

        public Facelet(char colour, int face){
            this.colour = colour;
            this.face = face;
        }
    }

    public static class Center{
        int index;
        Facelet f;

        public Center(int index, Facelet f){
            this.index = index;
            this.f = f;
        }
    }

    public static class Edge{
        int index;
        int ori;
        Facelet f1, f2;

        public Edge(int index, int ori, Facelet f1, Facelet f2){
            this.index = index;
            this.ori = ori;
            this.f1 = f1;
            this.f2 = f2;
        }
    }

    public static class Corner{
        int index;
        int ori;
        Facelet f1, f2, f3;

        public Corner(int index, int ori, Facelet f1, Facelet f2, Facelet f3){
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
}
