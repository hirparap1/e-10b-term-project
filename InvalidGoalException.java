/**
 * Exception for when an invalid goal is provided. A valid
 * goal is either
 * - LevelGoal: Target between 2 and 126 inclusive
 * - ExperienceGoal: Target between 1 and 200_000_000 inclusive
 */
public class InvalidGoalException extends Exception {

}
