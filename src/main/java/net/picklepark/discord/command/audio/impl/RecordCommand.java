package net.picklepark.discord.command.audio.impl;

import net.dv8tion.jda.api.audio.AudioReceiveHandler;
import net.dv8tion.jda.api.audio.CombinedAudio;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.picklepark.discord.command.DiscordCommand;
import net.picklepark.discord.exception.NotRecordingException;
import net.picklepark.discord.service.RecordingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

public class RecordCommand implements DiscordCommand {

    private final GuildMessageReceivedEvent event;
    private final RecordingService recordingService;
    private final Logger logger = LoggerFactory.getLogger(RecordCommand.class);

    public RecordCommand(GuildMessageReceivedEvent event, RecordingService recordingService) {
        this.event = event;
        this.recordingService = recordingService;
    }

    @Override
    public void execute() {
        ensureConnected();
        recordingService.beginRecording();

        AudioReceiveHandler handler = new AudioReceiveHandler() {
            @Override
            public boolean canReceiveCombined() {
                return true;
            }

            @Override
            public void handleCombinedAudio(@Nonnull CombinedAudio combinedAudio) {
                try {
                    logger.info("Received combined audio");
                    recordingService.receive(combinedAudio);
                } catch (NotRecordingException e) {
                    throw new RuntimeException("Recording should have been started, but got a NotRecordingException in the AudioReceiveHandler");
                }
            }
        };

        event.getGuild().getAudioManager().setReceivingHandler(handler);
    }

    private void ensureConnected() {
        if (!event.getGuild().getAudioManager().isConnected())
            event.getGuild().getAudioManager().openAudioConnection(event.getGuild().getVoiceChannels().get(0));
    }
}
