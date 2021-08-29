package net.picklepark.discord.command;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.io.IOException;

public class HelpCommand implements DiscordCommand {

    private final GuildMessageReceivedEvent event;
    private static final String instructions = "Commands: ~queue [url], ~skip, ~volume (to get current), ~volume [n] (to set)," +
            " ~louder, ~softer, ~pause, ~unpause, ~gtfo, ~feat [feat name], ~spell [spell name], ~help";

    public HelpCommand(GuildMessageReceivedEvent event) {
        this.event = event;
    }

    @Override
    public void execute() throws IOException {
        event.getChannel().sendMessage(instructions).queue();
    }
}
