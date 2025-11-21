package rubikscube;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Solve {

    private char[][][] cube = new char[6][3][3];
    
    public Solver(File file){
        this.cube = createCube(file);
    }


    public char[][][] createCube(File file){
        String output = "";
        try (Scanner sc = new Scanner(file)) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                output += line;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e){
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

    public void createCube(String output){
        output = output.replaceAll("[ ]", "");


    }

}
