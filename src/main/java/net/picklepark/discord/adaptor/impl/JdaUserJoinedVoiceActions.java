package net.picklepark.discord.adaptor.impl;

import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.picklepark.discord.adaptor.UserJoinedVoiceActions;
import net.picklepark.discord.audio.AudioContext;
import net.picklepark.discord.exception.NotEnoughQueueCapacityException;

public class JdaUserJoinedVoiceActions extends AudioActions implements UserJoinedVoiceActions {

    private final GuildVoiceJoinEvent event;

    public JdaUserJoinedVoiceActions(AudioContext context, GuildVoiceJoinEvent event) {
        super(context);
        this.event = event;
    }

    @Override
    public String user() {
        return event.getMember().getUser().getAsTag();
    }

    @Override
    public String channel() {
        return event.getChannelJoined().getGuild().getName();
    }

    @Override
    public boolean isConnected() {
        return audioContext.audioManager.isConnected();
    }

    @Override
    public void play(String path) throws NotEnoughQueueCapacityException {
        addToQueue(path);
    }
}
