package net.picklepark.discord.adaptor.impl;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.audio.AudioReceiveHandler;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.managers.AudioManager;
import net.picklepark.discord.adaptor.DiscordActions;
import net.picklepark.discord.audio.AudioContext;
import net.picklepark.discord.audio.GuildPlayer;
import net.picklepark.discord.exception.AmbiguousUserException;
import net.picklepark.discord.exception.NoOwnerException;
import net.picklepark.discord.exception.NoSuchUserException;
import net.picklepark.discord.exception.UserIdentificationException;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JdaDiscordActions implements DiscordActions {

    private final GuildMessageReceivedEvent event;
    private final AudioContext audioContext;

    private Matcher matcher;

    public JdaDiscordActions(GuildMessageReceivedEvent event,
                             AudioContext audioContext) {
        this.event = event;
        this.audioContext = audioContext;
    }

    @Override
    public void send(String message) {
        this.event.getChannel().sendMessage(message).queue();
    }

    @Override
    public void send(MessageEmbed embed) {
        this.event.getChannel().sendMessageEmbeds(embed).queue();
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
    public User getAuthor() {
        return event.getAuthor();
    }

    @Override
    public User lookupUser(String user) throws UserIdentificationException {
        if (isTag(user))
            return lookupTag(user);
        else
            return lookupAmbiguousName(user);
    }

    private User lookupTag(String user) throws NoSuchUserException {
        Member member = event.getGuild().getMemberByTag(user);
        if (member == null)
            throw new NoSuchUserException(user);
        else
            return member.getUser();
    }

    boolean isTag(String user) {
        return user.matches(".*#\\d{4}");
    }

    private User lookupAmbiguousName(String user) throws UserIdentificationException{
        Set<Member> members = new HashSet<>(event.getGuild().getMembersByName(user, true));
        members.addAll(event.getGuild().getMembersByNickname(user, true));
        members.addAll(event.getGuild().getMembersByEffectiveName(user, true));

        if (members.isEmpty())
            throw new NoSuchUserException(user);
        else if (members.size() > 1)
            throw new AmbiguousUserException(user);
        else
            return members.stream().findFirst().get().getUser();
    }

    @Override
    public String userInput() {
        return event.getMessage().getContentRaw();
    }

    @Override
    public String getArgument(String arg) {
        return matcher.group(arg);
    }

    @Override
    public void setVolume(int volume) {
        audioContext.guildPlayer.player.setVolume(volume);
    }

    @Override
    public void disconnect() {
        AudioManager manager = audioContext.channel.getGuild().getAudioManager();
        if (manager.isConnected())
            manager.closeAudioConnection();
    }

    @Override
    public int getVolume() {
        return audioContext.guildPlayer.player.getVolume();
    }

    @Override
    public void pause() {
        audioContext.guildPlayer.player.setPaused(true);
    }

    @Override
    public void skip() {
        audioContext.guildPlayer.scheduler.nextTrack();
    }

    @Override
    public void queue(String uri) {
        audioContext.playerManager.loadItemOrdered(
                audioContext.guildPlayer,
                uri,
                new ResultHandler(audioContext.guildPlayer, uri));
    }

    @Override
    public void initMatches(String regex, String message) {
        matcher = Pattern.compile(regex).matcher(message);
        if (!matcher.matches())
            throw new RuntimeException("Pattern " + regex + " does not match" + message);
    }

    @Override
    public String getGuildName() {
        return event.getGuild().getName();
    }

    @Override
    public User getOwner() throws NoOwnerException {
        Member owner = event.getGuild().getOwner();
        if (owner == null)
            throw new NoOwnerException(event.getGuild().getName());
        else
            return owner.getUser();
    }

    @Override
    public void unpause() {
        audioContext.guildPlayer.player.setPaused(false);
    }


    private class ResultHandler implements AudioLoadResultHandler {

        private final GuildPlayer guildPlayer;
        private final String uri;

        public ResultHandler(GuildPlayer guildPlayer, String uri) {
            this.guildPlayer = guildPlayer;
            this.uri = uri;
        }

        @Override
        public void trackLoaded(AudioTrack track) {
            guildPlayer.queue(track);
        }

        @Override
        public void playlistLoaded(AudioPlaylist playlist) {
            AudioTrack firstTrack = getFirstTrack(playlist);
            guildPlayer.queue(firstTrack);
        }

        private AudioTrack getFirstTrack(AudioPlaylist playlist) {
            AudioTrack firstTrack = playlist.getSelectedTrack();
            if (firstTrack == null) {
                firstTrack = playlist.getTracks().get(0);
            }
            return firstTrack;
        }

        @Override
        public void noMatches() {
            send("Nothing found by " + uri);
        }

        @Override
        public void loadFailed(FriendlyException exception) {
            send("Could not play: " + exception.getMessage());
        }
    }
}
