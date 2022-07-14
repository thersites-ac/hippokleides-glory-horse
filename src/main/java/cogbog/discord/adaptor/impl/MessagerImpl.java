package cogbog.discord.adaptor.impl;

import cogbog.discord.adaptor.Messager;
import net.dv8tion.jda.api.JDA;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;

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
        jda.getGuilds().stream()
                .filter(g -> g.getId().equals(guild))
                .findFirst()
                .map(g -> g.getTextChannels().get(0))
                .ifPresent(c -> c.sendMessage(message).queue());
    }
}
