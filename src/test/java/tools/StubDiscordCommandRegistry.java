package tools;

import cogbog.discord.command.DiscordCommandRegistry;
import cogbog.discord.service.AuthManager;
import cogbog.discord.service.impl.ClipManagerImpl;

public class StubDiscordCommandRegistry extends DiscordCommandRegistry {

    public StubDiscordCommandRegistry(AuthManager authManager) {
        super(new ClipManagerImpl(null), authManager, null, "~");
    }

    public static StubDiscordCommandRegistry withRubberstampAuthManager() {
        return new StubDiscordCommandRegistry(new RubberstampAuthManager());
    }
}
