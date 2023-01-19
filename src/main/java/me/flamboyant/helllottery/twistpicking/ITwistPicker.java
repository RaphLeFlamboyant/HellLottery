package me.flamboyant.helllottery.twistpicking;

public interface ITwistPicker {
    void pickAndRunTwist(float difficulty, int roundNumber);
    void stopCurrentTwists();
}
