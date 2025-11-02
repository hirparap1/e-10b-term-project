import java.io.*;
import java.net.*;
import java.util.*;

class Player {
    private static final String HISCORE_URL = "https://secure.runescape.com/m=hiscore_oldschool/index_lite.ws?player=%s";

    private String username;
    private ArrayList<Skill> skills;
    private HashMap<SkillName, Goal> goals;
    private HashMap<SkillName, Integer> experienceRates;

    public Player() throws Exception {
        this.username = "IronSushi";
        updateSkills();
    }

    public Player(String username) throws Exception {
        this.username = username;
        updateSkills();
    }

    public String toString() {
        String headers = String.format(
            "%-16s%-8s%-16s%s\n", "Skill", "Level", "Experience", "Rank"
        );

        StringBuilder result = new StringBuilder();
        result.append("Username: " + this.username + "\n");
        result.append(headers);
        for (Skill s : this.skills) {
            result.append(s.toString() + "\n");
        }

        return result.toString();
    }

    private void updateSkills() throws Exception {
        this.skills = new ArrayList<Skill>();

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
                    Integer.parseInt(parts[0])
                );
                skills.add(s);
            }
        }
        reader.close();
        conn.disconnect();
    }
}

class PlayerNotFoundException extends Exception {}