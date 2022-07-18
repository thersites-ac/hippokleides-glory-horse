package cogbog.discord.adaptor;

public interface Messager {
    void send(String guild, String message);
    void send(String guild, String channelId, String message);
}
