abstract class Goal {
    abstract public int getTargetExperience();
}

class ExperienceGoal extends Goal {
    private int targetExperience;

    public ExperienceGoal() throws InvalidGoalException {
        this(200_000_000);
    }

    public ExperienceGoal(int targetExperience) throws InvalidGoalException {
        if (targetExperience < 0 || targetExperience > 200_000_000) {
            throw new InvalidGoalException();
        }

        this.targetExperience = targetExperience;
    }

    @Override
    public int getTargetExperience() {
        return this.targetExperience;
    }
}

class LevelGoal extends Goal {
    private int targetLevel;

    public LevelGoal() throws InvalidGoalException {
        this(99);
    }

    public LevelGoal(int targetLevel) throws InvalidGoalException {
        if (targetLevel < 1 || targetLevel > 126) {
            throw new InvalidGoalException();
        }

        this.targetLevel = targetLevel;
    }

    @Override
    public int getTargetExperience() {
        int sum = 0;
        for (int l = 1; l < this.targetLevel; l++) {
            sum += Math.floor(l + 300.0 * Math.pow(2.0, l / 7.0));
        }
        return (int) Math.floor(sum / 4);
    }
}