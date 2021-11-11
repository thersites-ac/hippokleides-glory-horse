package net.picklepark.discord.command.general;

import net.picklepark.discord.annotation.Auth;
import net.picklepark.discord.annotation.Help;
import net.picklepark.discord.annotation.UserInput;
import net.picklepark.discord.command.DiscordCommand;
import net.picklepark.discord.adaptor.DiscordActions;
import net.picklepark.discord.command.DiscordCommandRegistry;

import javax.inject.Inject;
import java.util.Collection;

@UserInput("help")
@Help(name = "help", message = "See this message again.")
@Auth(Auth.Level.ANY)
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
                .filter(command -> command.getClass().isAnnotationPresent(Help.class))
                .map(this::commandHelpLine)
                .sorted()
                .reduce((s, t) -> s + "\n\t" + t)
                .ifPresentOrElse(body -> actions.send("I know these commands:\n\t" + body),
                        () -> actions.send("I don't know anything :("));
        actions.send(HINT);
    }

    private String commandHelpLine(DiscordCommand command) {
        Help help = command.getClass().getAnnotation(Help.class);
        return "* " + help.name() + ": " + help.message();
    }
}
