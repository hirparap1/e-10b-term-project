import java.util.*;

public class PlayerTest {
    private final static Scanner KEYBOARD = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.print("Enter a Player username to instantiate: ");

        String username = KEYBOARD.nextLine();

        try {
            Player player = new Player(username);
            System.out.println(player.toString());

            OUTER: while (true) {
                System.out.println("\nOptions:");
                System.out.println("1. Set experience rate for a skill");
                System.out.println("2. Display player stats");
                System.out.println("3. Exit");
                System.out.print("Enter your choice: ");
                String choice = KEYBOARD.nextLine();

                switch (choice) {
                    case "1":
                        setExperienceRate(player);
                        break;
                    case "2":
                        System.out.println(player.toString());
                        break;
                    case "3":
                        System.out.println("Exiting...");
                        break OUTER;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                        break;
                }
            }

        } catch (Exception e) {
            System.out.println("There was an error while fetching player data...");
            e.printStackTrace();
        }
    }

    private static void setExperienceRate(Player player) {
        System.out.println("\nAvailable skills:");
        SkillName[] skills = SkillName.values();
        for (int i = 0; i < skills.length; i++) {
            System.out.println((i + 1) + ". " + skills[i]);
        }

        System.out.print("Enter skill number: ");
        try {
            int skillIndex = Integer.parseInt(KEYBOARD.nextLine()) - 1;

            if (skillIndex < 0 || skillIndex >= skills.length) {
                System.out.println("Invalid skill number.");
                return;
            }

            System.out.print("Enter experience rate (XP per hour): ");
            int rate = Integer.parseInt(KEYBOARD.nextLine());

            player.updateExperienceRate(skills[skillIndex], rate);
            System.out.println("Experience rate set successfully for " + skills[skillIndex]);

        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a valid number.");
        } catch (InvalidExperienceRate e) {
            System.out.println("Invalid experience rate. Rate must be non-negative.");
        }
    }
}
