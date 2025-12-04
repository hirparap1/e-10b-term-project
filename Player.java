import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.util.*;

public class Player {
    private static final String HISCORE_URL = "https://secure.runescape.com/m=hiscore_oldschool/index_lite.ws?player=%s";

    private final String username;
    private final LocalDateTime createdAt;
    private final HashMap<SkillName, Goal> goals = new HashMap<>();
    private final HashMap<SkillName, Integer> experienceRates = new HashMap<>();
    private ArrayList<Skill> skills;

    public Player() throws Exception {
        this("IronSushi");
    }

    public Player(String username) throws Exception {
        this.username = username;
        this.createdAt = LocalDateTime.now();
        fetchSkills();
    }

    @Override
    public String toString() {
        String headers = String.format(
                "%-16s%-12s%-12s%-16s%s\n", "Skill", "Rank", "Level", "Experience", "XP Rate");

        StringBuilder result = new StringBuilder();
        result.append("Username: ").append(this.username).append("\n");
        result.append("Created At: ").append(this.createdAt).append("\n");
        result.append(headers);
        for (Skill s : this.skills) {
            int xpRate = experienceRates.getOrDefault(s.getName(), 0);
            result.append(s.toString()).append(Integer.toString(xpRate)).append("\n");
        }

        return result.toString();
    }

    public void updateExperienceRate(SkillName skill, int rate) throws InvalidExperienceRate {
        if (rate < 0) {
            throw new InvalidExperienceRate();
        }

        this.experienceRates.put(skill, rate);
    }

    private void fetchSkills() throws Exception {
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
