package net.picklepark.discord.adaptor;

import net.dv8tion.jda.api.audio.AudioReceiveHandler;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.picklepark.discord.exception.NoOwnerException;
import net.picklepark.discord.exception.NotEnoughQueueCapacityException;
import net.picklepark.discord.exception.UserIdentificationException;

public interface MessageReceivedActions {
    void send(String message);
    void send(MessageEmbed embed);
    // fixme: this is a bit different from the others...
    void setReceivingHandler(AudioReceiveHandler handler);
    void connect();
    boolean isConnected();
    // fixme: don't return Users
    User getAuthor();
    User lookupUser(String user) throws UserIdentificationException;
    User getOwner() throws NoOwnerException;
    String userInput();
    String getArgument(String arg);
    void setVolume(int volume);
    void disconnect();
    int getVolume();
    void pause();
    void unpause();
    void skip();
    void nuke();
    void queue(String uri) throws NotEnoughQueueCapacityException;
    void initMatches(String regex, String message);
    String getGuildId();
    int getAudioQueueSize();
}
