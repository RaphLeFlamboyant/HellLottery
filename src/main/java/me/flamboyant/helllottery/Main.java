package me.flamboyant.helllottery;

import me.flamboyant.FlamboyantPlugin;

public final class Main  extends FlamboyantPlugin {

    @Override
    public void onEnable() {
        super.onEnable();

        CommandsDispatcher commandDispatcher = new CommandsDispatcher();

        getCommand("f_hell_lottery").setExecutor(commandDispatcher);
    }
}
