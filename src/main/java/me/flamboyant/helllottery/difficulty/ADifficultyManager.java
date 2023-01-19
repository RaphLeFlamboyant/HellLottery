package me.flamboyant.helllottery.difficulty;

public abstract class ADifficultyManager implements IDifficultyManager {
    protected float difficultyLevel = 0;

    @Override
    public float getNextDifficultyLevel(int roundNumber) {
        checkDifficultyLevel(roundNumber);
        return difficultyLevel;
    }

    protected abstract void checkDifficultyLevel(int roundNumber);
}
