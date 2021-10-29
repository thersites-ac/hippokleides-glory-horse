package net.picklepark.discord.command;

import net.picklepark.discord.adaptor.DiscordActions;
import net.picklepark.discord.exception.DiscordCommandException;

public class SpyCommand implements DiscordCommand {
    private boolean executed;

    @Override
    public void execute(DiscordActions actions) throws DiscordCommandException {
        this.executed = true;
    }

    public boolean isExecuted() {
        return executed;
    }
}
