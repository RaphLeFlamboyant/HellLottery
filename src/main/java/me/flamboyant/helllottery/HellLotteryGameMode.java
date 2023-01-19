package me.flamboyant.helllottery;

import me.flamboyant.common.parameters.AParameter;
import me.flamboyant.common.utils.Common;
import me.flamboyant.common.utils.ILaunchablePlugin;
import me.flamboyant.helllottery.difficulty.IDifficultyManager;
import me.flamboyant.helllottery.difficulty.WithBreaksDifficultyManager;
import me.flamboyant.helllottery.rounds.IRoundUnfolding;
import me.flamboyant.helllottery.rounds.IRoundUnfoldingListener;
import me.flamboyant.helllottery.rounds.TimerRoundUnfolding;
import me.flamboyant.helllottery.twistbuilding.TwistBuilder;
import me.flamboyant.helllottery.twistbuilding.TwistType;
import me.flamboyant.twistmechanisms.TwistCausalityHandler;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HellLotteryGameMode implements ILaunchablePlugin, IRoundUnfoldingListener {
    private IDifficultyManager difficultyManager = new WithBreaksDifficultyManager(0.20f, 5, false);
    private IRoundUnfolding roundUnfolding = new TimerRoundUnfolding(20);
    private TwistBuilder twistBuilder = new TwistBuilder();
    private List<TwistCausalityHandler> currentTwists = new ArrayList<>();

    private int roundCount = 0;
    private boolean isRunning;

    @Override
    public void onRoundEnd() {
        Bukkit.getLogger().info("TOTO");
        for (TwistCausalityHandler twist : currentTwists) {
            twist.stop();
        }

        currentTwists.clear();
        launchNextRound();
    }

    private void launchNextRound() {
        List<TwistType> possibleTwistTypes = Arrays.asList(TwistType.MULTI_CROSSED, TwistType.MULTI_CAUSES, TwistType.MULTI_CONSEQUENCES);
        float difficulty = difficultyManager.getNextDifficultyLevel(++roundCount);
        Bukkit.getLogger().info("Difficulty is " + difficulty);
        TwistCausalityHandler twist = twistBuilder.buildRandomTwist(Math.abs(difficulty),
                possibleTwistTypes.get(Common.rng.nextInt(possibleTwistTypes.size())),
                (int) (difficulty / 0.30f) + 1,
                difficulty < 0);

        currentTwists.add(twist);
        twist.start();

        String title = "Round " + roundCount;
        BarColor barColor = BarColor.YELLOW;
        List<BarFlag> flags = new ArrayList<>();
        if (difficulty < 0) {
            title += " - Pause round";
            barColor = BarColor.GREEN;
        }
        if (difficulty > 0.7) {
            if (Common.rng.nextFloat() > 0.1)
                barColor = BarColor.RED;
            else {
                title += " - Inferno";
                flags.add(BarFlag.CREATE_FOG);
                flags.add(BarFlag.DARKEN_SKY);
                flags.add(BarFlag.PLAY_BOSS_MUSIC);
                barColor = BarColor.WHITE;
                twist = twistBuilder.buildRandomTwist(Math.abs(difficulty),
                        possibleTwistTypes.get(Common.rng.nextInt(possibleTwistTypes.size())),
                        (int) (difficulty / 0.30f) + 1,
                        difficulty < 0);
                currentTwists.add(twist);
                twist.start();
                twist = twistBuilder.buildRandomTwist(Math.abs(difficulty),
                        possibleTwistTypes.get(Common.rng.nextInt(possibleTwistTypes.size())),
                        (int) (difficulty / 0.30f) + 1,
                        difficulty < 0);
                currentTwists.add(twist);
                twist.start();
            }
        }

        roundUnfolding.startRound(this, title, 30, barColor, BarStyle.SEGMENTED_6, flags.toArray(new BarFlag[0]));
    }

    @Override
    public boolean start() {
        isRunning = true;
        roundCount = 0;
        launchNextRound();
        return true;
    }

    @Override
    public boolean stop() {
        for (TwistCausalityHandler twist : currentTwists) {
            twist.stop();
        }

        currentTwists.clear();
        roundUnfolding.stop();
        isRunning = false;
        return true;
    }

    @Override
    public void resetParameters() {
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }

    @Override
    public boolean canModifyParametersOnTheFly() {
        return false;
    }

    @Override
    public List<AParameter> getParameters() {
        return new ArrayList<>();
    }
}
