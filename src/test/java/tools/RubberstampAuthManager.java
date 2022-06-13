package tools;

import net.picklepark.discord.adaptor.MessageReceivedActions;
import net.picklepark.discord.constants.AuthLevel;
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
}
