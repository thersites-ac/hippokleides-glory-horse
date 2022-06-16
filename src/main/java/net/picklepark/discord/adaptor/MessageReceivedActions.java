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
    long getAuthorId();
    long lookupUserId(String user) throws UserIdentificationException;
    String canonicalUsername(String user) throws UserIdentificationException;
    long getOwnerId() throws NoOwnerException;
    // fixme: this seems unnecessary given `getArgument`
    String userInput();
    String getArgument(String arg);
    // fixme: this shouldn't be generally exposed
    void initMatches(String regex, String message);
    void setVolume(int volume);
    void disconnect();
    int getVolume();
    void pause();
    void unpause();
    void skip();
    void nuke();
    void queue(String uri) throws NotEnoughQueueCapacityException;
    String getGuildId();
    int getAudioQueueSize();
}
