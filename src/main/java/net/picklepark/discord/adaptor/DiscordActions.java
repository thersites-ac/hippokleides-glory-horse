package net.picklepark.discord.adaptor;

import net.dv8tion.jda.api.audio.AudioReceiveHandler;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.picklepark.discord.exception.NoSuchUserException;

public interface DiscordActions {
    void send(String message);
    void send(MessageEmbed embed);
    void setReceivingHandler(AudioReceiveHandler handler);
    void connect();
    User lookupUser(String user) throws NoSuchUserException;
    String userInput();
    void setPattern(String pattern);
    String getArgument(String arg);
    void setVolume(int volume);
    void disconnect();
    int getVolume();
    void pause();
    void unpause();
    void skip();
    void queue(String uri);
}
