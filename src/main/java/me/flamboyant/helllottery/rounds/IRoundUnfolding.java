package me.flamboyant.helllottery.rounds;

import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;

public interface IRoundUnfolding {
    void startRound(IRoundUnfoldingListener listener, String roundName, int quantityToUnfold, BarColor color, BarStyle barStyle, BarFlag... flags);
    void stop();
}
