import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.util.*;

public class Player {
    private static final String HISCORE_URL = "https://secure.runescape.com/m=hiscore_oldschool/index_lite.ws?player=%s";

    private final String username;
    private LocalDateTime lastRefreshedAt;
    private final HashMap<SkillName, Goal> goals = new HashMap<>();
    private final HashMap<SkillName, Integer> experienceRates = new HashMap<>();
    private ArrayList<Skill> skills;

    public Player() throws Exception {
        this("IronSushi");
    }

    public Player(String username) throws Exception {
        this.username = username;
        this.refreshSkills();
        this.initializeGoals();
    }

    // Initialize all goals to level 99
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

    public ArrayList<Skill> getSkills() {
        return this.skills;
    }

    public Skill getSkill(SkillName skillName) {
        return this.skills.get(skillName.ordinal());
    }

    public Goal getGoal(SkillName skillName) {
        return this.goals.get(skillName);
    }

    public LocalDateTime getLastRefreshedAt() {
        return this.lastRefreshedAt;
    }

    public String getUsername() {
        return this.username;
    }

    public int getProgressToGoal(SkillName skillName) {
        Goal goal = this.goals.get(skillName);
        Skill skill = getSkill(skillName);

        int currentExp = skill.getExperience();
        int targetExp = goal.getTargetExperience();

        int progress = (int) ((currentExp * 100.0) / targetExp);

        return Math.min(progress, 100);
    }

    public void updateGoal(SkillName skillName, boolean isLevel, int target) throws InvalidGoalException {
        Goal newGoal;
        if (isLevel) {
            newGoal = new LevelGoal(target);
        } else {
            newGoal = new ExperienceGoal(target);
        }

        this.goals.put(skillName, newGoal);
    }

    public void updateExperienceRate(SkillName skillName, int rate) throws InvalidExperienceRateException {
        if (rate < 0) {
            throw new InvalidExperienceRateException();
        }

        this.experienceRates.put(skillName, rate);
    }

    public final void refreshSkills() throws Exception {
        this.lastRefreshedAt = LocalDateTime.now();
        this.skills = new ArrayList<>();

        HttpURLConnection conn = establishConnection();
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;

        do {
            line = reader.readLine();
            if (line != null) {
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    Skill s = new Skill(
                            SkillName.values()[skills.size()],
                            Integer.parseInt(parts[1]),
                            Integer.parseInt(parts[2]),
                            Integer.parseInt(parts[0]));
                    skills.add(s);
                }
            }
        } while (line != null);

        reader.close();
        conn.disconnect();
    }

    private HttpURLConnection establishConnection() throws Exception {
        String urlStr = String.format(HISCORE_URL, this.username);
        URL url = new URL(urlStr);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);

        int status = conn.getResponseCode();
        if (status != 200) {
            throw new PlayerNotFoundException();
        }

        return conn;
    }
}
