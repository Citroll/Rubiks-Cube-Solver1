package rubikscube;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Solve {

    private String solvedState;
    private char[][][] cube = new char[6][3][3];
    private static final int UP = 0, LEFT = 1, FRONT = 2, RIGHT = 3, BACK = 4, DOWN = 5;

    public Solve() {
        this.cube = null;
        this.solvedState
                = "   OOO\n"
                + "   OOO\n"
                + "   OOO\n"
                + "GGGWWWBBBYYY\n"
                + "GGGWWWBBBYYY\n"
                + "GGGWWWBBBYYY\n"
                + "   RRR\n"
                + "   RRR\n"
                + "   RRR\n";
    }

    public Solve(File file) {
        this.cube = createCube(file);
        this.solvedState
                = "   OOO\n"
                + "   OOO\n"
                + "   OOO\n"
                + "GGGWWWBBBYYY\n"
                + "GGGWWWBBBYYY\n"
                + "GGGWWWBBBYYY\n"
                + "   RRR\n"
                + "   RRR\n"
                + "   RRR\n";
    }

    public char[][][] createCube(File file) {
        String output = "";
        try (Scanner sc = new Scanner(file)) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                output += line;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        output = output.replaceAll("[ ]", "");

        int k = 0;

        for (int i = 0; i < 9; i++, k++) {
            cube[0][i / 3][i % 3] = output.charAt(k);
        }

        for (int row = 0; row < 3; row++) {
            for (int face = 1; face <= 4; face++) {
                for (int col = 0; col < 3; col++, k++) {
                    cube[face][row][col] = output.charAt(k);
                }
            }
        }

        for (int i = 0; i < 9; i++, k++) {
            cube[5][i / 3][i % 3] = output.charAt(k);
        }
        return cube;
    }

    @Override
    public String toString() {
        StringBuilder string = new StringBuilder();

        for (int row = 0; row < 3; row++) {
            string.append("   ");
            for (int col = 0; col < 3; col++) {
                string.append(cube[0][row][col]);
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
                string.append(cube[5][row][col]);
            }
            string.append("\n");
        }

        return string.toString();
    }

    public boolean isSolved(){
        return this.toString().equals(solvedState);
    }

    public char[] getRow(int face, int row) {
        return cube[face][row];
    }

    public void setRow(int face, int row, char[] newRow) {
        for (int i = 0; i < 3; i++) {
            cube[face][row][i] = newRow[i];
        }
    }

    public char[] getCol(int face, int col) {
        char[] column = new char[3];
        for (int i = 0; i < 3; i++) {
            column[i] = cube[face][i][col];
        }
        return column;
    }

    public char[] setCol(int face, int col, char[] newCol) {
        for (int i = 0; i < 3; i++) {
            cube[face][i][col] = newCol[i];
        }
        return newCol;
    }

    public void rotateFace(char[][] face){
        char temp;

        //corner rotations
        temp = face[0][0];
        face[0][0] = face[2][0];
        face[2][0] = face[2][2];
        face[2][2] = face[0][2];
        face[0][2] = temp;

        //edge rotations
        temp = face[0][1];
        face[0][1] = face[1][0];
        face[1][0] = face[2][1];
        face[2][1] = face[1][2];
        face[1][2] = temp;
    }

}
