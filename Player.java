import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Represents a Player of the game OldSchoolRuneScape. A Player has a username,
 * and a set of skills they are progressing in. They can also have Goals and
 * experience rates for each of these skills.
 * 
 * The Player class and all it's underlying instance variables support
 * serialization into a file representation.
 * 
 */
public class Player implements Serializable {
    // Version for Serialization
    private static final long serialVersionUID = 1L;
    // Static URL for API to fetch skills data
    private static final String HISCORE_URL = "https://secure.runescape.com/m=hiscore_oldschool/index_lite.ws?player=%s";

    // In-game username for player
    private final String username;
    // The last time we pulled the skills data from the API
    private LocalDateTime lastRefreshedAt;
    // The Goals for this Player's Skills
    private final HashMap<SkillName, Goal> goals = new HashMap<>();
    // The experience rates for this Player's Skills. Represented in exp/hour
    private final HashMap<SkillName, Integer> experienceRates = new HashMap<>();
    // The list of Skills for this Player
    private ArrayList<Skill> skills;

    // O-arg constructor, defaults to my own character, IronSushi
    public Player() throws Exception {
        this("IronSushi");
    }

    /**
     * Creates a new Player with the provided username.
     * Fetches skill data from the API and initializes Goals for all non-Overall
     * skills
     * to level 99.
     * 
     * @param username in-game name for Player
     * @throws Exception
     */
    public Player(String username) throws Exception {
        this.username = username;
        this.refreshSkills();
        this.initializeGoals();
    }

    /**
     * initializeGoals initializes goals for all non-Overall skills to a
     * LevelGoal of 99.
     * 
     */
    private void initializeGoals() {
        try {
            for (Skill s : skills) {
                if (!s.isOverall()) {
                    this.goals.put(s.getName(), new LevelGoal());
                }
            }
        } catch (InvalidGoalException e) {
            // This should never happen since we are using the 0-arg constructor
        }
    }

    /**
     * Writes the Player to a file that can be stored and used in the future
     * 
     * @param filepath the file path to save the Player to
     * @throws IOException
     */
    public void saveToFile(String filepath) throws IOException {
        // My IDE was compaining I wasn't using try-with-resource syntax
        // So I used the auto-fix feature here
        try (ObjectOutputStream out = new ObjectOutputStream(
                new FileOutputStream(filepath))) {
            out.writeObject(this);
        }
    }

    /**
     * Reads and creates a Player from a file that can be stored and used in the
     * future
     * 
     * @param filepath the file path to load the Player to
     * @throws IOException
     */
    public static Player loadFromFile(String filepath) throws IOException, ClassNotFoundException {
        // My IDE was compaining I wasn't using try-with-resource syntax
        // So I used the auto-fix feature here
        try (ObjectInputStream in = new ObjectInputStream(
                new FileInputStream(filepath))) {
            return (Player) in.readObject();
        }
    }

    // Skills Getter
    public ArrayList<Skill> getSkills() {
        return this.skills;
    }

    // Skill by SkillName Accessor
    // Relys on the order of the SkillName enum matching the order in this.skills
    // This works because the API returns skills in a pre-determined order.
    public Skill getSkill(SkillName skillName) {
        return this.skills.get(skillName.ordinal());
    }

    // Specific Goal by SkillName Accessor
    public Goal getGoal(SkillName skillName) {
        return this.goals.get(skillName);
    }

    // Experience Rate by SkillName Accessor
    public Integer getExperienceRate(SkillName skillName) {
        return this.experienceRates.get(skillName);
    }

    // lastRefreshedAt Getter
    public LocalDateTime getLastRefreshedAt() {
        return this.lastRefreshedAt;
    }

    // Username Getter
    public String getUsername() {
        return this.username;
    }

    /**
     * Calculates the progress towards a specific Skill's Goal for the Player.
     * The progress is returned as the percentage of experience earned towards
     * the goal's target experience.
     * 
     * @param skillName The skill to calculate progress for
     * @return an integer between 0 and 100. -1 if no Goal was set
     */
    public int getProgressToGoal(SkillName skillName) {
        Goal goal = this.goals.get(skillName);
        Skill skill = getSkill(skillName);

        // Return -1 if no Goal for the skill was found
        if (goal == null) {
            return -1;
        }

        int currentExp = skill.getExperience();
        int targetExp = goal.getTargetExperience();

        int progress = (int) ((currentExp * 100.0) / targetExp);

        return Math.min(progress, 100);
    }

    /**
     * Calculates the number of hours needed to play to reach the Goal for the
     * given skill. Uses the Skill's experience rate to calculate how many hours
     * will be required.
     * 
     * @param skillName The skill to calculate time for
     * @return the number of hours needed to reach the goal. -1 if an experience
     *         rate or goal was not set.
     */
    public double getTimeToGoal(SkillName skillName) {
        Integer expRate = this.experienceRates.get(skillName);

        // If no experience rate is set, return -1 to indicate it can't be calculated
        if (expRate == null) {
            return -1.0;
        }

        Goal goal = this.goals.get(skillName);
        Skill skill = getSkill(skillName);

        // If no goal is set, return -1 to indicate it can't be calculated
        if (goal == null) {
            return -1.0;
        }

        int currentExp = skill.getExperience();
        int targetExp = goal.getTargetExperience();

        // Calculate remaining experience needed
        int remainingExp = targetExp - currentExp;

        // If already at or past the goal, return 0
        if (remainingExp <= 0) {
            return 0.0;
        }

        // Calculate hours: remaining exp / exp per hour
        return remainingExp / (double) expRate;
    }

    /**
     * Updates the Player's Goal for a specific Skill
     * 
     * @param skillName the Skill to set the Goal for
     * @param isLevel   whether the Goal will be a LevelGoal or ExperienceGoal
     * @param target    the target level or experience for the Goal
     * @throws InvalidGoalException if an invalid target is specified
     */
    public void updateGoal(SkillName skillName, boolean isLevel, int target) throws InvalidGoalException {
        Goal newGoal;
        if (isLevel) {
            newGoal = new LevelGoal(target);
        } else {
            newGoal = new ExperienceGoal(target);
        }

        this.goals.put(skillName, newGoal);
    }

    /**
     * Updates the Player's experience rate for a specific Skill
     * 
     * @param skillName the Skill to set the experience rate for
     * @param rate      The rate at which the Player expects to gain experience (in
     *                  experience/hour)
     * @throws InvalidExperienceRateException if an invalid rate is specified
     */
    public void updateExperienceRate(SkillName skillName, int rate) throws InvalidExperienceRateException {
        // Rate must be non-zero and not greater than the max experience value
        if (rate < 1 || rate > 200_000_000) {
            throw new InvalidExperienceRateException();
        }

        this.experienceRates.put(skillName, rate);
    }

    /**
     * Refreshes the current Player's skill data from the API. This will clear out
     * skills but not affect goals or experience rates.
     * 
     * @throws Exception on an unexpected API error or when the username does not
     *                   exist
     */
    public final void refreshSkills() throws Exception {
        this.lastRefreshedAt = LocalDateTime.now();
        this.skills = new ArrayList<>();

        // Establish connection and initialize reader for API response
        HttpURLConnection conn = establishConnection();
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;

        do {
            line = reader.readLine();
            if (line != null) {
                // API returns skill data as a CSV
                String[] parts = line.split(",");
                // Skill rows have a length of >= 3, this is the only way to
                // determine whether the current line is for a skill
                if (parts.length >= 3) {
                    // Order of skills is always returned the same way,
                    // So follow the ordering defined in the SkillName enum
                    Skill s = new Skill(
                            SkillName.values()[skills.size()],
                            Integer.parseInt(parts[1]), // Level
                            Integer.parseInt(parts[2]), // Experience
                            Integer.parseInt(parts[0])); // Rank
                    skills.add(s);
                }
            }
        } while (line != null);

        // Close and disconnect resources
        reader.close();
        conn.disconnect();
    }

    /**
     * Inserts the current player's username into HISCORE_URL and establishes a
     * connection to the API.
     * 
     * @return HttpURLConnection to the skills data API at HISCORE_URL
     * @throws Exception on an unexpected API error or when the username does not
     *                   exist
     */
    private HttpURLConnection establishConnection() throws Exception {
        // Build URL string for the API for current username
        String urlStr = String.format(HISCORE_URL, this.username);
        URL url = new URL(urlStr);

        // Open connection and specify method/timeouts
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);

        int status = conn.getResponseCode();
        // If the status is not successful, we could not find the player
        if (status != 200) {
            throw new PlayerNotFoundException();
        }

        return conn;
    }
}
