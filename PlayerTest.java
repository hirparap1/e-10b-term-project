import java.util.*;

public class PlayerTest {
    private final static Scanner KEYBOARD = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.print("Enter a Player username to instantiate: ");

        String username = KEYBOARD.nextLine();

        try {
            Player player = new Player(username);
            System.out.println(player.toString());
        } catch (Exception e) {
            System.out.println("There was an error while fetching player data...");
            e.printStackTrace();
        }
    }
}
