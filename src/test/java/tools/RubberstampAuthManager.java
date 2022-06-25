package tools;

import net.picklepark.discord.adaptor.MessageReceivedActions;
import net.picklepark.discord.model.AuthLevel;
import net.picklepark.discord.service.AuthManager;

public class RubberstampAuthManager implements AuthManager {
    @Override
    public boolean isActionAuthorized(MessageReceivedActions actions, AuthLevel level) {
        return true;
    }

    @Override
    public void addAdmin(String channelName, long user) {
    }

    @Override
    public void demote(long user, MessageReceivedActions actions) {
    }

    @Override
    public void ban(String guild, long user) {
    }

    @Override
    public void unban(String guildId, long userId) {
    }
}
