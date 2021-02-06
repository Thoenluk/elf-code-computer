package thoenluk.elfcode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

/**
 *
 * @author Lukas Thöni lukas.thoeni@gmx.ch
 */
public class ECCRunner {
    public static void main(String[] args) {
        Scanner user = new Scanner(System.in);
        System.out.println("Enter file name of program (I can't be bothered making a"
                + " more complex solution at this moment.)");
        String filename = user.next();
        try {
            String program = Files.readString(Paths.get(filename));
            new ElfCodeComputer(program.split("\r\n")).runProgram();
        } catch (IOException ex) {
            System.out.println("oh noes");
            ex.printStackTrace();
        }
    }
}
