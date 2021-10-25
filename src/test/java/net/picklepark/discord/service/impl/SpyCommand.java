package net.picklepark.discord.service.impl;

import net.picklepark.discord.adaptor.DiscordActions;
import net.picklepark.discord.command.DiscordCommand;
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
