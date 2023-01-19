package me.flamboyant.helllottery.difficulty;

public class DummyDifficultyManager extends ADifficultyManager {
    @Override
    protected void checkDifficultyLevel(int roundNumber) {
        if (difficultyLevel < 1) difficultyLevel += 0.01;
    }
}
