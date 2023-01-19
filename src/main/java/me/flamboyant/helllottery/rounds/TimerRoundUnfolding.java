package me.flamboyant.helllottery.rounds;

import me.flamboyant.common.utils.Common;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class TimerRoundUnfolding implements IRoundUnfolding {
    private int timerFrameInTicks;
    private BukkitTask timerTask;
    private int framesToUnfold = 0;
    private int framesRemaining = 0;
    private IRoundUnfoldingListener listener;
    private BossBar timerBar;

    public TimerRoundUnfolding(int timerFrameDurationInTicks) {
        timerFrameInTicks = timerFrameDurationInTicks;
    }

    @Override
    public void startRound(IRoundUnfoldingListener listener, String roundName, int quantityToUnfold, BarColor color, BarStyle barStyle, BarFlag... flags) {
        this.listener = listener;
        framesToUnfold = quantityToUnfold;
        framesRemaining = framesToUnfold;

        timerBar = Common.server.createBossBar(roundName, color, barStyle, flags);
        timerBar.setVisible(true);
        timerBar.setProgress(1.0);

        for (Player p : Common.server.getOnlinePlayers()) {
            timerBar.addPlayer(p);
        }

        timerTask = Bukkit.getScheduler().runTaskTimer(Common.plugin, () -> doTimerFrame(), timerFrameInTicks, timerFrameInTicks);
    }

    @Override
    public void stop() {
        framesRemaining = 0;
        timerBar.setVisible(false);
        timerBar.setProgress(0);
        timerBar = null;
        Bukkit.getScheduler().cancelTask(timerTask.getTaskId());
    }

    private void doTimerFrame() {
        if (--framesRemaining == 0) {
            timerBar.setVisible(false);
            timerBar.setProgress(0);
            timerBar = null;
            Bukkit.getScheduler().cancelTask(timerTask.getTaskId());
            listener.onRoundEnd();
            return;
        }

        timerBar.setProgress(framesRemaining / (double) framesToUnfold);
    }
}
