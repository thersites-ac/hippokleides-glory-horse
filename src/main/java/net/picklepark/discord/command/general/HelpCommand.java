package net.picklepark.discord.command.general;

import net.picklepark.discord.command.DiscordCommand;
import net.picklepark.discord.adaptor.DiscordActions;
import net.picklepark.discord.command.DiscordCommandRegistry;
import net.picklepark.discord.constants.AuthLevel;

import javax.inject.Inject;
import java.util.Collection;

public class HelpCommand implements DiscordCommand {

    private static final String HINT = "Also, when I find a feat or spell, click the citation at top (e.g. Core Rulebook, " +
            "Advanced Player's Guide, etc.) to go to the site.";

    private DiscordCommandRegistry registry;

    @Inject
    public HelpCommand(DiscordCommandRegistry registry) {
        this.registry = registry;
    }

    @Override
    public void execute(DiscordActions actions) {
        Collection<DiscordCommand> commands = registry.getCommands();
        commands.stream()
                .map(this::commandHelpLine)
                .sorted()
                .reduce((s, t) -> s + "\n\t" + t)
                .ifPresentOrElse(body -> actions.send("I know these commands:\n\t" + body),
                        () -> actions.send("I don't know anything :("));
        actions.send(HINT);
    }

    @Override
    public AuthLevel requiredAuthLevel() {
        return AuthLevel.ANY;
    }

    @Override
    public String example() {
        return "help";
    }

    @Override
    public String helpMessage() {
        return "See this message again.";
    }

    @Override
    public String userInput() {
        return "help";
    }

    private String commandHelpLine(DiscordCommand command) {
        return command.example() + ": "  + command.helpMessage();
    }
}
