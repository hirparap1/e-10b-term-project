public class Skill {
    private final SkillName name;
    private final int level;
    private final int experience;
    private final int rank;
    private final int virtualLevel;

    public Skill() {
        this(SkillName.OVERALL, 32, 1154, 1);
    }

    public Skill(SkillName name, int level, int experience, int rank) {
        this.name = name;
        this.level = level;
        this.experience = experience;
        this.rank = rank;
        this.virtualLevel = calculateVirtualLevel();
    }

    public SkillName getName() {
        return this.name;
    }

    public int getLevel() {
        return this.level;
    }

    public int getExperience() {
        return this.experience;
    }

    public int getRank() {
        return this.rank;
    }

    public int getVirtualLevel() {
        return this.virtualLevel;
    }

    @Override
    public String toString() {
        String levelDisplay;

        if (this.level == 99 && this.name != SkillName.OVERALL) {
            levelDisplay = this.level + " (" + this.virtualLevel + ")";
        } else {
            levelDisplay = String.valueOf(this.level);
        }

        String formattedString = String.format(
                "%-16s%-12d%-12s%-16d",
                this.name,
                this.rank,
                levelDisplay,
                this.experience);

        return formattedString;
    }

    private int calculateVirtualLevel() {
        // If level is below 99, just return the actual level
        if (this.level < 99) {
            return this.level;
        }

        // For level 99+, calculate virtual level
        int currentLevel = 99;

        while (currentLevel <= 126) {
            int expForNextLevel = getExperienceForLevel(currentLevel + 1);
            if (this.experience < expForNextLevel) {
                break;
            }
            currentLevel++;
        }

        return currentLevel;
    }

    private int getExperienceForLevel(int targetLevel) {
        int sum = 0;
        for (int l = 1; l < targetLevel; l++) {
            sum += Math.floor(l + 300.0 * Math.pow(2.0, l / 7.0));
        }
        return (int) Math.floor(sum / 4);
    }
}
