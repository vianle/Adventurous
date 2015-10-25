import java.util.Scanner;

/** The main program for Adventurous.
 *  @author Vi Le
 */
public class Main {

    public static void main(String[] args) {
        Scanner reader = new Scanner(System.in);
        Interpreter player = new Interpreter(reader);
        player.play();
        System.out.println("The End.");
    }

}
