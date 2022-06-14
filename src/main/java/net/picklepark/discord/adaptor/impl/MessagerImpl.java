package net.picklepark.discord.adaptor.impl;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.picklepark.discord.adaptor.Messager;

import javax.inject.Inject;

public class MessagerImpl implements Messager {

    private final JDA jda;

    @Inject
    public MessagerImpl(JDA jda) {
        this.jda = jda;
    }

    @Override
    public void send(String guild, String message) {
        jda.getGuilds().stream()
                .filter(g -> g.getId().equals(guild))
                .findFirst()
                .map(g -> g.getTextChannels().get(0))
                .ifPresent(c -> c.sendMessage(message).queue());
    }
}
