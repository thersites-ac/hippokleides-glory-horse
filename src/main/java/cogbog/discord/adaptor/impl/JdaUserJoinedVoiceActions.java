package cogbog.discord.adaptor.impl;

import cogbog.discord.adaptor.UserJoinedVoiceActions;
import cogbog.discord.audio.AudioContext;
import cogbog.discord.exception.NotEnoughQueueCapacityException;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;

public class JdaUserJoinedVoiceActions extends AudioActions implements UserJoinedVoiceActions {

    private final GuildVoiceJoinEvent event;

    public JdaUserJoinedVoiceActions(AudioContext context, GuildVoiceJoinEvent event) {
        super(context);
        this.event = event;
    }

    @Override
    public long user() {
        return event.getMember().getUser().getIdLong();
    }

    @Override
    public String guildName() {
        return event.getChannelJoined().getGuild().getName();
    }

    @Override
    public String guildId() {
        return event.getChannelJoined().getGuild().getId();
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
