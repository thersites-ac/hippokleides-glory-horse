package tools;

import cogbog.discord.adaptor.MessageReceivedActions;
import cogbog.discord.model.AuthLevel;
import cogbog.discord.exception.AuthLevelConflictException;
import cogbog.discord.service.AuthManager;

public class TestAuthManager implements AuthManager {

    private boolean answer;
    private boolean throwAuthException;

    public void setAuthDecision(boolean answer) {
        this.answer = answer;
    }

    @Override
    public boolean isActionAuthorized(MessageReceivedActions actions, AuthLevel level) {
        return answer;
    }

    @Override
    public void addAdmin(String channelName, long user) {
    }

    @Override
    public void demote(long user, MessageReceivedActions actions) throws AuthLevelConflictException {
        if (throwAuthException)
            throw new AuthLevelConflictException(user);
    }

    public void throwAuthException() {
        throwAuthException = true;
    }

    @Override
    public void ban(String guildId, long userId) {
    }

    @Override
    public void unban(String guildId, long userId) {
    }
}
