package net.picklepark.discord.command;

import net.picklepark.discord.command.audio.AudioContext;
import net.picklepark.discord.command.audio.impl.*;
import net.picklepark.discord.command.audio.util.GuildPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.*;

public class DiscordCommandFactory {

    private static final String RAM_RANCH_URL = "https://www.youtube.com/watch?v=MADvxFXWvwE";

    private static final DiscordCommand NOOP = new NoopCommand();

    private final AudioPlayerManager playerManager;
    private final Map<Long, GuildPlayer> guildPlayers;
    private final List<String> authorizedUsers;

    public DiscordCommandFactory() {
        playerManager = new DefaultAudioPlayerManager();
        guildPlayers = new HashMap<>();
        AudioSourceManagers.registerRemoteSources(playerManager);
        AudioSourceManagers.registerLocalSource(playerManager);
        this.authorizedUsers = Arrays.asList("pvhagg#7133", "pvhagg#1387");
    }

    public DiscordCommand buildAuthorizedCommand(GuildMessageReceivedEvent event) {
        if (isAuthorized(event))
            return buildCommand(event);
        else
            return NOOP;
    }

    private boolean isAuthorized(GuildMessageReceivedEvent event) {
        return authorizedUsers.contains(event.getAuthor().getAsTag());
    }

    private DiscordCommand buildCommand(GuildMessageReceivedEvent event) {

        String[] command = event.getMessage().getContentRaw().split(" ");
        AudioContext context = getContext(event);

        if ("~queue".equals(command[0]) && command.length == 2) {
            return new QueueAudioCommand(command[1], context);
        } else if ("~skip".equals(command[0])) {
            return new SkipAudioCommand(context);
        } else if ("~volume".equals(command[0]) && command.length == 1) {
            return new GetVolumeAudioCommand(context);
        } else if ("~volume".equals(command[0]) && command.length == 2) {
            return new ChangeVolumeAudioCommand(command[1], context);
        } else if ("~louder".equals(command[0])) {
            return new LouderAudioCommand(context);
        } else if ("~softer".equals(command[0])) {
            return new SofterAudioCommand(context);
        } else if ("~pause".equals(command[0])) {
            return new PauseAudioCommand(context);
        } else if ("~unpause".equals(command[0])) {
            return new UnpauseAudioCommand(context);
        } else if ("~ramranch".equals(command[0])) {
            return new QueueAudioCommand(RAM_RANCH_URL, context);
        } else {
            return NOOP;
        }
    }

    private AudioContext getContext(GuildMessageReceivedEvent event) {
        return new AudioContext(event.getChannel(), getGuildPlayer(event.getGuild()), playerManager);
    }

    private GuildPlayer getGuildPlayer(Guild guild) {
        long guildId = Long.parseLong(guild.getId());
        GuildPlayer guildPlayer = guildPlayers.get(guildId);
        if (guildPlayer == null) {
            guildPlayer = new GuildPlayer(playerManager);
            guildPlayers.put(guildId, guildPlayer);
        }
        return guildPlayer;
    }
}
