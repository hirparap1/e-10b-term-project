public class Skill {
    private final SkillName name;
    private final int level;
    private final int experience;
    private final int rank;

    public Skill() {
        this(SkillName.OVERALL, 32, 1154, 1);
    }

    public Skill(SkillName name, int level, int experience, int rank) {
        this.name = name;
        this.level = level;
        this.experience = experience;
        this.rank = rank;
    }

    @Override
    public String toString() {
        int virtualLevel = getVirtualLevel();
        String levelDisplay;

        if (virtualLevel > 99 && this.name != SkillName.OVERALL) {
            levelDisplay = this.level + " (" + virtualLevel + ")";
        } else {
            levelDisplay = String.valueOf(this.level);
        }

        String formattedString = String.format(
                "%-16s%-10s%-16d%d",
                this.name,
                levelDisplay,
                this.experience,
                this.rank);

        return formattedString;
    }

    public int getVirtualLevel() {
        // If level is below 99, just return the actual level
        if (this.level < 99) {
            return this.level;
        }

        // For level 99+, calculate virtual level
        int virtualLevel = 99;

        while (virtualLevel <= 126) {
            int expForNextLevel = getExperienceForLevel(virtualLevel + 1);
            if (this.experience < expForNextLevel) {
                break;
            }
            virtualLevel++;
        }

        return virtualLevel;
    }

    private int getExperienceForLevel(int targetLevel) {
        int sum = 0;
        for (int l = 1; l < targetLevel; l++) {
            sum += Math.floor(l + 300.0 * Math.pow(2.0, l / 7.0));
        }
        return (int) Math.floor(sum / 4);
    }
}
