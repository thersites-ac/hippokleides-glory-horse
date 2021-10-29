package net.picklepark.discord.adaptor;

import net.dv8tion.jda.api.audio.AudioReceiveHandler;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.picklepark.discord.exception.NoSuchUserException;

import java.util.ArrayList;
import java.util.List;

public class SpyDiscordActions implements DiscordActions {

    private List<String> sentMessages;
    private String userInput;

    public SpyDiscordActions() {
        sentMessages = new ArrayList<>();
    }

    @Override
    public void send(String message) {
        sentMessages.add(message);
    }
    @Override
    public void send(MessageEmbed embed) {
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
        return null;
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

    public void setUserInput(String userInput) {
        this.userInput = userInput;
    }
}
