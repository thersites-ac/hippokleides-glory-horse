package cogbog.discord.adaptor.impl;

import cogbog.discord.adaptor.Messager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;

import java.util.List;
import java.util.Optional;

import static java.lang.String.format;

@Singleton
public class MessagerImpl implements Messager {

    private static final Logger logger = LoggerFactory.getLogger(MessagerImpl.class);

    private final JDA jda;

    @Inject
    public MessagerImpl(JDA jda) {
        this.jda = jda;
    }

    @Override
    public void send(String guild, String message) {
        logger.info(format("Sending %s to %s", message, guild));
        guildTextChannels(guild)
                .map(channels -> channels.get(0))
                .ifPresent(c -> c.sendMessage(message).queue());
    }

    @Override
    public void send(String guild, String channelId, String message) {
        logger.info(format("Sending %s to %s/%s", message, guild, channelId));
        guildTextChannels(guild).flatMap(channels -> channels.stream()
                .filter(c -> c.getId().equals(channelId))
                .findFirst())
                .ifPresentOrElse(c -> c.sendMessage(message).queue(), () -> send(guild, message));
    }

    private Optional<List<TextChannel>> guildTextChannels(String guild) {
        return jda.getGuilds().stream()
                        .filter(g -> g.getId().equalsIgnoreCase(guild))
                        .findFirst()
                        .map(Guild::getTextChannels);
    }
}
