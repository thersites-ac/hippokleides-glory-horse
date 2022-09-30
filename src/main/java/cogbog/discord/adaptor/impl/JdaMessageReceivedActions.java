package cogbog.discord.adaptor.impl;

import cogbog.discord.audio.AudioContext;
import cogbog.discord.exception.*;
import net.dv8tion.jda.api.audio.AudioReceiveHandler;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.managers.AudioManager;
import cogbog.discord.adaptor.MessageReceivedActions;
import cogbog.discord.parse.CommandDsl;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;

public class JdaMessageReceivedActions extends AudioActions implements MessageReceivedActions {

    private final GuildMessageReceivedEvent event;

    private Matcher matcher;

    public JdaMessageReceivedActions(GuildMessageReceivedEvent event,
                                     AudioContext audioContext) {
        super(audioContext);
        this.event = event;
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
        if (!isConnected())
            event.getGuild().getAudioManager().openAudioConnection(event.getGuild().getVoiceChannels().get(0));
    }

    @Override
    public boolean isConnected() {
        return event.getGuild().getAudioManager().isConnected();
    }

    @Override
    public long getAuthorId() {
        return event.getAuthor().getIdLong();
    }

    @Override
    public String getAuthorUsername() {
        return event.getAuthor().getName();
    }

    @Override
    public String lookupUserTag(String user) throws UserIdentificationException {
        return lookupUser(user).getAsTag();
    }

    @Override
    public long lookupUserId(String user) throws UserIdentificationException {
        return lookupUser(user).getIdLong();
    }

    private User lookupUser(String user) throws UserIdentificationException {
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
        try {
            members.add(event.getGuild().getMemberById(user));
        } catch (NumberFormatException ignored) {}

        if (members.isEmpty())
            throw new NoSuchUserException(user);
        else if (members.size() > 1)
            throw new AmbiguousUserException(user);
        else
            return members.stream().findFirst().get().getUser();
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
        AudioManager manager = audioContext.audioManager;
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
    public void nuke() {
        audioContext.guildPlayer.scheduler.empty();
        skip();
    }

    @Override
    public void queue(String uri) throws NotEnoughQueueCapacityException {
        addToQueue(uri);
    }

    @Override
    public void initMatches(String dsl, String message) {
        matcher = new CommandDsl(dsl).toPattern().matcher(message);
        if (!matcher.matches())
            throw new RuntimeException("DSL " + dsl + " does not match" + message);
    }

    @Override
    public String getGuildId() {
        return event.getGuild().getId();
    }

    @Override
    public String getGuildName() {
        return event.getGuild().getName();
    }

    @Override
    public long getGuildOwnerId() throws NoOwnerException {
        Member owner = event.getGuild().getOwner();
        if (owner == null)
            throw new NoOwnerException(event.getGuild().getName());
        else
            return owner.getUser().getIdLong();
    }

    @Override
    public void unpause() {
        audioContext.guildPlayer.player.setPaused(false);
    }

    @Override
    public int getAudioQueueSize() {
        return audioContext.guildPlayer.scheduler.size();
    }

    @Override
    public long getOriginatingTextChannelId() {
        return event.getChannel().getIdLong();
    }

    @Override
    public void respond(File upload, String name) {
        event.getMessage().reply(upload, name).queue();
    }

    @Override
    public void respond(String message) {
        event.getMessage().reply(message).queue();
    }
}
