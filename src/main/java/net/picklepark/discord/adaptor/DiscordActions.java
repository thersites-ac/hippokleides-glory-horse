package net.picklepark.discord.adaptor;

import net.dv8tion.jda.api.audio.AudioReceiveHandler;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.picklepark.discord.exception.NoOwnerException;
import net.picklepark.discord.exception.UserIdentificationException;

public interface DiscordActions {
    void send(String message);
    void send(MessageEmbed embed);
    void setReceivingHandler(AudioReceiveHandler handler);
    void connect();
    User getAuthor();
    User lookupUser(String user) throws UserIdentificationException;
    String userInput();
    String getArgument(String arg);
    void setVolume(int volume);
    void disconnect();
    int getVolume();
    void pause();
    void unpause();
    void skip();
    void queue(String uri);
    void initMatches(String regex, String message);
    String getGuildName();
    User getOwner() throws NoOwnerException;
}
