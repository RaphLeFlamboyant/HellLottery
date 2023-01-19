package me.flamboyant.helllottery.difficulty;

public class WithBreaksDifficultyManager extends ADifficultyManager {
    private int breakRoundInterval;
    private boolean breakProportionalToDifficulty;
    private float velocity;

    public WithBreaksDifficultyManager(float velocity, int breakRoundInterval, boolean breakProportionalToDifficulty) {
        this.breakRoundInterval = breakRoundInterval;
        this.breakProportionalToDifficulty = breakProportionalToDifficulty;
        this.velocity = velocity;
    }

    @Override
    protected void checkDifficultyLevel(int roundNumber) {
        int factor = 1;
        if (roundNumber % breakRoundInterval == 0) {
            if (!breakProportionalToDifficulty) {
                difficultyLevel = -0.2f;
                return;
            }

            factor = -1;
        }

        difficultyLevel = factor * (Math.min(roundNumber * velocity * 0.1f, 1f));
    }
}
