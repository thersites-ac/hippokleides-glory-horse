package net.picklepark.discord.adaptor;

import net.dv8tion.jda.api.audio.AudioReceiveHandler;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.picklepark.discord.exception.NoSuchUserException;

import java.util.*;

public class SpyDiscordActions implements DiscordActions {

    private List<String> sentMessages;
    private List<MessageEmbed> sentEmbeds;
    private String userInput;
    private Map<String, String> args;

    public SpyDiscordActions() {
        sentMessages = new ArrayList<>();
        sentEmbeds = new ArrayList<>();
        args = new HashMap<>();
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
    }
    @Override
    public User lookupUser(String user) throws NoSuchUserException {
        return null;
    }
    @Override
    public String userInput() {
        return userInput;
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
    public void queue(String uri) {
    }

    @Override
    public void initMatches(String regex, String message) {
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
}
