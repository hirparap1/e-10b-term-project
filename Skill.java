import java.io.Serializable;

/**
 * Skill represents one of the skills in the game OSRS (as well as the sum skill
 * Overall). It has an amount of experience which correlates to a level, as well
 * as a ranking on the leaderboards of the game.
 */
public class Skill implements Serializable {
    // Version for Serialization
    private static final long serialVersionUID = 1L;

    // The name of the skill
    private final SkillName name;
    // The current level of the skill
    private final int level;
    // The current experience value of the skill
    private final int experience;
    // The current rank of the associated Player based on experience value.
    // -1 if the Player has no experience in the Skill.
    private final int rank;
    // The theoretical level past 99 for the experience value
    // Follows the formula: https://oldschool.runescape.wiki/w/Experience#Formula
    private final int virtualLevel;

    // 0-arg constructor, defaults to an Overall skill
    public Skill() {
        this(SkillName.OVERALL, 32, 1154, 1);
    }

    /**
     * Creates a new Skill with the provided arguments
     * 
     * @param name       a SkillName
     * @param level      the level based on the experience value
     * @param experience the experience received so far in the skill
     * @param rank       the rank on the leaderboard (who has most experience)
     */
    public Skill(SkillName name, int level, int experience, int rank) {
        this.name = name;
        this.level = level;
        this.experience = experience;
        this.rank = rank;
        this.virtualLevel = calculateVirtualLevel();
    }

    // Name Getter
    public SkillName getName() {
        return this.name;
    }

    // Level Getter
    public int getLevel() {
        return this.level;
    }

    // Experience Getter
    public int getExperience() {
        return this.experience;
    }

    // Rank Getter
    public int getRank() {
        return this.rank;
    }

    // Virtual Level Getter
    public int getVirtualLevel() {
        return this.virtualLevel;
    }

    /**
     * Returns whether the current skill is OVERALL or not. Useful for special
     * UI treatment.
     * 
     * @return whether the Skill is an Overall skill or not
     */
    public boolean isOverall() {
        return this.name == SkillName.OVERALL;
    }

    /**
     * Returns the formatted level. If level is 99 and not OVERALL, include
     * the virtual level in parenthesis. Otherwise, just return the level.
     * 
     * @return a String representing the formatted level value
     */
    public String formattedLevelString() {
        if (this.level == 99 && !isOverall()) {
            return this.level + " (" + getVirtualLevel() + ")";
        } else {
            return String.valueOf(this.level);
        }
    }

    /**
     * Calculates this skill's virtual level based on the experience value.
     * To calculate, we find the necessary experience of each level above 99
     * to determine what is our current virtual level.
     * 
     * If the level is below 99, just return the raw level.
     * 
     * @return the skill's virtual level
     */
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

    /**
     * Helper used for calculating the necessary experience for the given
     * level. Uses the formula in:
     * https://oldschool.runescape.wiki/w/Experience#Formula
     * 
     * @param targetLevel the level to calculate the necessary experience for
     * @return How many experience points are needed to reach targetLevel
     */
    private int getExperienceForLevel(int targetLevel) {
        int sum = 0;
        for (int l = 1; l < targetLevel; l++) {
            sum += Math.floor(l + 300.0 * Math.pow(2.0, l / 7.0));
        }
        return (int) Math.floor(sum / 4);
    }
}
