package tools;

import net.picklepark.discord.adaptor.DiscordActions;
import net.picklepark.discord.constants.AuthLevel;
import net.picklepark.discord.exception.AuthLevelConflictException;
import net.picklepark.discord.service.AuthService;

public class TestAuthService implements AuthService {

    private boolean answer;
    private boolean throwAuthException;

    public void setAuthDecision(boolean answer) {
        this.answer = answer;
    }

    @Override
    public boolean isActionAuthorized(DiscordActions actions, AuthLevel level) {
        return answer;
    }

    @Override
    public void addAdmin(String channelName, long user) {
    }

    @Override
    public void demote(long user, DiscordActions actions) throws AuthLevelConflictException {
        if (throwAuthException)
            throw new AuthLevelConflictException(user);
    }

    public void throwAuthException() {
        throwAuthException = true;
    }
}
