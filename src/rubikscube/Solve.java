package rubikscube;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;

public class Solve {

    private char[][][] cube = new char[6][3][3];
    private char[][][] solvedCube = new char[6][3][3];
    private static final int UP = 0, LEFT = 1, FRONT = 2, RIGHT = 3, BACK = 4, DOWN = 5;
    private String solved
            = "   OOO\n"
            + "   OOO\n"
            + "   OOO\n"
            + "GGGWWWBBBYYY\n"
            + "GGGWWWBBBYYY\n"
            + "GGGWWWBBBYYY\n"
            + "   RRR\n"
            + "   RRR\n"
            + "   RRR\n";

    /*
              UP
        LEFT FRONT RIGHT BACK
             DOWN
     */
    public Solve() {
        this.cube = null;
        this.solvedCube = createCube(solved);
    }

    public Solve(File file) {
        this.cube = readFile(file);
        this.solvedCube = createCube(solved);
    }

    public char[][][] readFile(File file) {
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
        return createCube(output);
    }

    public char[][][] createCube(String output) {
        char[][][] temp = new char[6][3][3];
        output = output.replaceAll("[ \n]", "");

        int k = 0;

        for (int i = 0; i < 9; i++, k++) {
            temp[0][i / 3][i % 3] = output.charAt(k);
        }

        for (int row = 0; row < 3; row++) {
            for (int face = 1; face <= 4; face++) {
                for (int col = 0; col < 3; col++, k++) {
                    temp[face][row][col] = output.charAt(k);
                }
            }
        }

        for (int i = 0; i < 9; i++, k++) {
            temp[5][i / 3][i % 3] = output.charAt(k);
        }
        return temp;
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

    public boolean isSolved() {
        return Arrays.deepEquals(this.cube, solvedCube);
    }

    public boolean checkLayerOne() {
        char[][][] temp = new char[6][3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                temp[FRONT][i][j] = 'W';
            }
        }

        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 3; j++) {
                for (int k = 0; k < 3; k++) {
                    System.out.print(temp[i][j][k]);
                }
            }
            System.out.println(i);
        }
        return true;
    }

    public void applyMoves(String input) {
        String[] moves = input.trim().split("\\s+");

        for (String m : moves) {
            switch (m) {
                case "F":
                    moveFront(false);
                    break;
                case "F'":
                    moveFront(true);
                    break;
                case "B":
                    moveBack(false);
                    break;
                case "B'":
                    moveBack(true);
                    break;
                case "R":
                    moveRight(false);
                    break;
                case "R'":
                    moveRight(true);
                    break;
                case "L":
                    moveLeft(false);
                    break;
                case "L'":
                    moveLeft(true);
                    break;
                case "U":
                    moveUp(false);
                    break;
                case "U'":
                    moveUp(true);
                    break;
                case "D":
                    moveDown(false);
                    break;
                case "D'":
                    moveDown(true);
                    break;
            }
        }
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

    public char[] reverse(char[] array) {
        char temp = array[0];
        array[0] = array[2];
        array[2] = temp;
        return array;
    }

    public void rotateFace(char[][] face, boolean CC) {
        char temp;
        temp = face[0][0];
        if (CC) {
            //corner
            face[0][0] = face[0][2];
            face[0][2] = face[2][2];
            face[2][2] = face[2][0];
            face[2][0] = temp;
            //edge
            temp = face[0][1];
            face[0][1] = face[1][2];
            face[1][2] = face[2][1];
            face[2][1] = face[1][0];
            face[1][0] = temp;
        } else {
            //corner
            face[0][0] = face[2][0];
            face[2][0] = face[2][2];
            face[2][2] = face[0][2];
            face[0][2] = temp;
            //edge
            temp = face[0][1];
            face[0][1] = face[1][0];
            face[1][0] = face[2][1];
            face[2][1] = face[1][2];
            face[1][2] = temp;
        }
    }

    public void moveFront(boolean CC) {
        char[] u = getRow(UP, 2).clone();
        char[] r = getCol(RIGHT, 0).clone();
        char[] d = getRow(DOWN, 0).clone();
        char[] l = getCol(LEFT, 2).clone();

        if (CC) {
            setRow(UP, 2, r);
            setCol(RIGHT, 0, reverse(d));
            setRow(DOWN, 0, l);
            setCol(LEFT, 2, reverse(u));
        } else {
            setRow(UP, 2, reverse(l));
            setCol(RIGHT, 0, u);
            setRow(DOWN, 0, reverse(r));
            setCol(LEFT, 2, reverse(d));
        }

        rotateFace(cube[FRONT], CC);

    }

    public void moveBack(boolean CC) {
        char[] u = getRow(UP, 0).clone();
        char[] r = getCol(RIGHT, 2).clone();
        char[] d = getRow(DOWN, 2).clone();
        char[] l = getCol(LEFT, 0).clone();

        if (CC) {
            setRow(UP, 0, l);
            setCol(RIGHT, 2, u);
            setRow(DOWN, 2, r);
            setCol(LEFT, 0, d);
        } else {

            setRow(UP, 0, r);
            setCol(RIGHT, 2, d);
            setRow(DOWN, 2, l);
            setCol(LEFT, 0, u);
        }

        rotateFace(cube[BACK], CC);

    }

    public void moveRight(boolean CC) {
        char[] u = getCol(UP, 2).clone();
        char[] f = getCol(FRONT, 2).clone();
        char[] d = getCol(DOWN, 2).clone();
        char[] b = getCol(BACK, 0).clone();

        if (CC) {
            setCol(UP, 2, b);
            setCol(FRONT, 2, u);
            setCol(DOWN, 2, f);
            setCol(BACK, 0, d);

        } else {
            setCol(UP, 2, f);
            setCol(FRONT, 2, d);
            setCol(DOWN, 2, reverse(b));
            setCol(BACK, 0, reverse(u));
        }

        rotateFace(cube[RIGHT], CC);
    }

    public void moveLeft(boolean CC) {
        char[] u = getCol(UP, 0).clone();
        char[] f = getCol(FRONT, 0).clone();
        char[] d = getCol(DOWN, 0).clone();
        char[] b = getCol(BACK, 2).clone();

        if (CC) {
            setCol(UP, 0, reverse(f));
            setCol(BACK, 2, u);
            setCol(DOWN, 0, reverse(b));
            setCol(FRONT, 0, d);
        } else {
            setCol(UP, 0, reverse(b));
            setCol(BACK, 2, reverse(d));
            setCol(DOWN, 0, f);
            setCol(FRONT, 0, u);
        }

        rotateFace(cube[LEFT], CC);

    }

    public void moveUp(boolean CC) {
        char[] l = getRow(LEFT, 0).clone();
        char[] f = getRow(FRONT, 0).clone();
        char[] r = getRow(RIGHT, 0).clone();
        char[] b = getRow(BACK, 0).clone();

        if (CC) {
            setRow(FRONT, 0, l);
            setRow(RIGHT, 0, f);
            setRow(BACK, 0, r);
            setRow(LEFT, 0, b);
        } else {
            setRow(FRONT, 0, r);
            setRow(RIGHT, 0, b);
            setRow(BACK, 0, l);
            setRow(LEFT, 0, f);
        }

        rotateFace(cube[UP], CC);
    }

    public void moveDown(boolean CC) {
        char[] f = getRow(FRONT, 2).clone();
        char[] r = getRow(RIGHT, 2).clone();
        char[] b = getRow(BACK, 2).clone();
        char[] l = getRow(LEFT, 2).clone();

        if (CC) {
            setRow(RIGHT, 2, b);
            setRow(BACK, 2, reverse(l));
            setRow(LEFT, 2, reverse(f));
            setRow(FRONT, 2, r);
        } else {
            setRow(RIGHT, 2, f);
            setRow(BACK, 2, reverse(r));
            setRow(LEFT, 2, reverse(b));
            setRow(FRONT, 2, l);
        }

        rotateFace(cube[DOWN], CC);

    }

}
