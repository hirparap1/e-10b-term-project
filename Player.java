import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.util.*;

public class Player {
    private static final String HISCORE_URL = "https://secure.runescape.com/m=hiscore_oldschool/index_lite.ws?player=%s";

    private final String username;
    private final LocalDateTime createdAt;
    private ArrayList<Skill> skills;
    private HashMap<SkillName, Goal> goals = new HashMap<>();
    private HashMap<SkillName, Integer> experienceRates = new HashMap<>();

    public Player() throws Exception {
        this("IronSushi");
    }

    public Player(String username) throws Exception {
        this.username = username;
        this.createdAt = LocalDateTime.now();
        fetchSkills();
    }

    public void updateExperienceRate(SkillName skill, int rate) throws InvalidExperienceRate {
        if (rate < 0) {
            throw new InvalidExperienceRate();
        }

        this.experienceRates.put(skill, rate);
    }

    @Override
    public String toString() {
        String headers = String.format(
                "%-16s%-10s%-16s%s\n", "Skill", "Level", "Experience", "Rank");

        StringBuilder result = new StringBuilder();
        result.append("Username: ").append(this.username).append("\n");
        result.append("Created At: ").append(this.createdAt).append("\n");
        result.append(headers);
        for (Skill s : this.skills) {
            result.append(s.toString()).append("\n");
        }

        return result.toString();
    }

    private void fetchSkills() throws Exception {
        this.skills = new ArrayList<>();

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

        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

        String line;
        while ((line = reader.readLine()) != null) {
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
        reader.close();
        conn.disconnect();
    }
}
