package net.picklepark.discord.command;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.io.IOException;

public class HelpCommand implements DiscordCommand {

    private final GuildMessageReceivedEvent event;
    private static final String instructions = "Commands: ~queue [url], ~skip, ~volume (to get current), ~volume [n] (to set)," +
            " ~louder, ~softer, ~pause, ~unpause, ~gtfo, ~feat [feat name], ~spell [spell name], ~help";
    private static final String hint = "When you find a feat or spell, click the citation at top (e.g. Core Rulebook, " +
            "Advanced Player's Guide, etc.) to go to the site.";

    public HelpCommand(GuildMessageReceivedEvent event) {
        this.event = event;
    }

    @Override
    public void execute() throws IOException {
        event.getChannel().sendMessage(instructions).queue();
        event.getChannel().sendMessage(hint).queue();
    }
}
