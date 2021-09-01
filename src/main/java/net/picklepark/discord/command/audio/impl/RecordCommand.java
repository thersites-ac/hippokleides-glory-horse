package net.picklepark.discord.command.audio.impl;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.picklepark.discord.command.DiscordCommand;
import net.picklepark.discord.command.audio.impl.handler.DemultiplexingHandler;
import net.picklepark.discord.service.RecordingService;

public class RecordCommand implements DiscordCommand {

    private final GuildMessageReceivedEvent event;
    private final RecordingService recordingService;

    public RecordCommand(GuildMessageReceivedEvent event, RecordingService recordingService) {
        this.event = event;
        this.recordingService = recordingService;
    }

    @Override
    public void execute() {
        ensureConnected();
        recordingService.beginRecording();
        event.getGuild().getAudioManager().setReceivingHandler(new DemultiplexingHandler(recordingService));
    }

    private void ensureConnected() {
        if (!event.getGuild().getAudioManager().isConnected())
            event.getGuild().getAudioManager().openAudioConnection(event.getGuild().getVoiceChannels().get(0));
    }
}
