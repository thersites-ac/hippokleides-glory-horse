package tools;

import net.picklepark.discord.command.DiscordCommandRegistry;
import net.picklepark.discord.service.AuthManager;
import net.picklepark.discord.service.impl.ClipManagerImpl;

public class StubDiscordCommandRegistry extends DiscordCommandRegistry {

    public StubDiscordCommandRegistry(AuthManager authManager) {
        super(new ClipManagerImpl(null), authManager, null);
    }

    public static StubDiscordCommandRegistry withRubberstampAuthManager() {
        return new StubDiscordCommandRegistry(new RubberstampAuthManager());
    }
}
