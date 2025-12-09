/**
 * A Goal is a target to achieve. It is either a:
 * - ExperienceGoal: A target experience value
 * - LevelGoal: A target level
 */
public abstract class Goal {
    // Abstract method that returns the required experience to satisfy a Goal
    abstract public int getTargetExperience();

    // toString override for displaying GOals in the UI
    @Override
    abstract public String toString();
}

/**
 * An ExperienceGoal is satisfied when the Player reaches the targetExperience
 * for the associated skill
 */
class ExperienceGoal extends Goal {
    // The experience value needed to satisfy the ExperienceGoal
    private int targetExperience;

    // 0-arg constructor, defaults to max targetExperience
    public ExperienceGoal() throws InvalidGoalException {
        this(200_000_000);
    }

    /**
     * Creates a new ExperienceGoal with the provided targetExperience.
     * Throws an InvalidGoalException if targetExperience < 1 || targetExperience >
     * 200M
     * 
     * @param targetExperience The experience value that will satisfy the Goal
     * @throws InvalidGoalException when provided an invalid targetExperience
     */
    public ExperienceGoal(int targetExperience) throws InvalidGoalException {
        if (targetExperience < 1 || targetExperience > 200_000_000) {
            throw new InvalidGoalException();
        }

        this.targetExperience = targetExperience;
    }

    /**
     * getTargetExperience returns the experience needed to meet the goal,
     * which is the same as targetExperience
     */
    @Override
    public int getTargetExperience() {
        return this.targetExperience;
    }

    /**
     * Returns a formatted string representing the ExperienceGoal.
     * In the format: "123,456 XP"
     */
    @Override
    public String toString() {
        return String.format("%,d XP", this.targetExperience);
    }
}

/**
 * A LevelGoal is satisfied when the Player reaches the targetLevel
 * for the associated skill
 */
class LevelGoal extends Goal {
    // The level needed to satisfy the LevelGoal
    private int targetLevel;

    // 0-arg constructor, defaults to targetLevel of 99
    public LevelGoal() throws InvalidGoalException {
        this(99);
    }

    /**
     * Creates a new LevelGoal with the provided targetLevel.
     * Throws an InvalidGoalException if targetLevel < 2 || targetExperience > 126
     * 
     * @param targetLevel The level that will satisfy the Goal
     * @throws InvalidGoalException when provided an invalid targetLevel
     */
    public LevelGoal(int targetLevel) throws InvalidGoalException {
        if (targetLevel < 2 || targetLevel > 126) {
            throw new InvalidGoalException();
        }

        this.targetLevel = targetLevel;
    }

    /**
     * getTargetExperience calculates the experience needed to acheive the
     * LevelGoal's targetLevel. The formula is found on the wiki page:
     * https://oldschool.runescape.wiki/w/Experience#Formula
     * 
     */
    @Override
    public int getTargetExperience() {
        int sum = 0;
        for (int l = 1; l < this.targetLevel; l++) {
            sum += Math.floor(l + 300.0 * Math.pow(2.0, l / 7.0));
        }
        return (int) Math.floor(sum / 4);
    }

    /**
     * Returns a formatted string representing the LevelGoal.
     * In the format: "Level 123"
     */
    @Override
    public String toString() {
        return "Level " + this.targetLevel;
    }
}