package me.flamboyant.helllottery.twistbuilding;

public enum TwistType {
    MULTI_CONSEQUENCES, // 1 cause, X consequences
    MULTI_CAUSES, // 1 consequence, X causes
    MULTI_CROSSED, // X causes, Y consequences
    SHARED_CONSEQUENCES, // X causes with all the common consequences
    SHARED_CAUSES // X consequences with all the common causes
}
