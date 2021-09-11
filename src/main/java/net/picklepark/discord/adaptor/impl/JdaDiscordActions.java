package net.picklepark.discord.adaptor.impl;

import net.dv8tion.jda.api.audio.AudioReceiveHandler;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.picklepark.discord.adaptor.DiscordActions;
import net.picklepark.discord.exception.NoSuchUserException;

import java.util.List;

public class JdaDiscordActions implements DiscordActions {
    private final GuildMessageReceivedEvent event;

    public JdaDiscordActions(GuildMessageReceivedEvent event) {
        this.event = event;
    }

    @Override
    public void send(String message) {
        this.event.getChannel().sendMessage(message).queue();;
    }

    @Override
    public void send(MessageEmbed embed) {
        this.event.getChannel().sendMessageEmbeds(embed).queue();;
    }

    @Override
    public void setReceivingHandler(AudioReceiveHandler handler) {
        this.event.getGuild().getAudioManager().setReceivingHandler(handler);
    }

    @Override
    public void connect() {
        if (!event.getGuild().getAudioManager().isConnected())
            event.getGuild().getAudioManager().openAudioConnection(event.getGuild().getVoiceChannels().get(0));
    }

    @Override
    public User lookupUser(String user) throws NoSuchUserException {
        List<Member> users = event.getChannel().getGuild().getMembersByNickname(user, true);
        if (users.isEmpty()) {
            event.getChannel().sendMessage("No one is named " + user).queue();
            throw new NoSuchUserException(user);
        } else if (users.size() > 1) {
            event.getChannel().sendMessage("Too many damn users named " + user).queue();
            throw new NoSuchUserException(user);
        } else
            return users.get(0).getUser();
    }

}
