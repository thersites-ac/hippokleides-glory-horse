package net.picklepark.discord.command.general;

import net.picklepark.discord.command.DiscordCommand;
import net.picklepark.discord.adaptor.DiscordActions;

import java.io.IOException;

public class HelpCommand implements DiscordCommand {

    private static final String instructions = "Commands: ~queue [url], ~skip, ~volume (to get current), ~volume [n] (to set)," +
            " ~louder, ~softer, ~pause, ~unpause, ~gtfo, ~feat [feat name], ~spell [spell name], ~help";
    private static final String hint = "When you find a feat or spell, click the citation at top (e.g. Core Rulebook, " +
            "Advanced Player's Guide, etc.) to go to the site.";

    @Override
    public void execute(DiscordActions actions) {
        actions.send(instructions);
        actions.send(hint);
    }
}
