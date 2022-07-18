package tools;

import net.dv8tion.jda.api.audio.AudioReceiveHandler;
import net.dv8tion.jda.api.entities.MessageEmbed;
import cogbog.discord.adaptor.MessageReceivedActions;
import cogbog.discord.adaptor.impl.AudioActions;
import cogbog.discord.exception.NotEnoughQueueCapacityException;
import cogbog.discord.exception.UserIdentificationException;

import java.util.*;

public class SpyMessageReceivedActions implements MessageReceivedActions {

    private final List<String> sentMessages;
    private final List<MessageEmbed> sentEmbeds;
    private final Map<String, String> args;
    private long owner;
    private long author;
    private String guildName;
    private final HashMap<String, Long> members;
    private final Queue<String> queuedAudio;
    private boolean connected;
    private boolean wasNuked = false;
    private String userInput;

    public SpyMessageReceivedActions() {
        sentMessages = new ArrayList<>();
        sentEmbeds = new ArrayList<>();
        args = new HashMap<>();
        members = new HashMap<>();
        queuedAudio = new LinkedList<>();
        connected = false;
    }

    @Override
    public void send(String message) {
        sentMessages.add(message);
    }
    @Override
    public void send(MessageEmbed embed) {
        sentEmbeds.add(embed);
    }
    @Override
    public void setReceivingHandler(AudioReceiveHandler handler) {
    }
    @Override
    public void connect() {
        connected = true;
    }

    @Override
    public long getAuthorId() {
        return author;
    }

    @Override
    public String getAuthorUsername() {
        return author + "";
    }

    @Override
    public long lookupUserId(String user) throws UserIdentificationException {
        Long userId = members.get(user);
        if (userId == null)
            throw new UserIdentificationException(user);
        else
            return userId;
    }

    @Override
    public String lookupUserTag(String user) throws UserIdentificationException {
        return user;
    }

    @Override
    public String getArgument(String arg) {
        return args.get(arg);
    }
    @Override
    public void setVolume(int volume) {
    }
    @Override
    public void disconnect() {
        connected = false;
    }
    @Override
    public int getVolume() {
        return 0;
    }
    @Override
    public void pause() {
    }
    @Override
    public void unpause() {
    }
    @Override
    public void skip() {
    }

    @Override
    public void nuke() {
        wasNuked = true;
        queuedAudio.clear();
    }

    @Override
    public void queue(String uri) throws NotEnoughQueueCapacityException {
        if (queuedAudio.size() >= AudioActions.MAX_QUEUE_SIZE)
            throw new NotEnoughQueueCapacityException(AudioActions.MAX_QUEUE_SIZE + "");
        else
            queuedAudio.add(uri);
    }

    @Override
    public void initMatches(String regex, String message) {
    }

    @Override
    public String getGuildId() {
        return guildName;
    }

    @Override
    public long getGuildOwnerId() {
        return owner;
    }

    @Override
    public int getAudioQueueSize() {
        return queuedAudio.size();
    }

    @Override
    public long getOriginatingTextChannelId() {
        return 0L;
    }

    public List<String> getSentMessage() {
        return sentMessages;
    }

    public List<MessageEmbed> getSentEmbeds() {
        return sentEmbeds;
    }

    public void setUserInput(String userInput) {
        this.userInput = userInput;
    }

    public void setArg(String key, String value) {
        args.put(key, value);
    }

    public void setGuildOwner(long i) {
        owner = i;
    }

    public void setAuthor(long i) {
        author = i;
    }

    public void setGuildName(String guildName) {
        this.guildName = guildName;
    }

    public void addGuildMember(String user, long id) {
        members.put(user, id);
    }

    public boolean isConnected() {
        return connected;
    }

    public Queue<String> getQueuedAudio() {
        return new LinkedList<>(queuedAudio);
    }

    public String getGuildName() {
        return guildName;
    }
}
