package net.picklepark.discord.command.audio;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.picklepark.discord.adaptor.DiscordActions;
import net.picklepark.discord.command.DiscordCommand;
import net.picklepark.discord.handler.DemultiplexingHandler;
import net.picklepark.discord.service.RecordingService;

public class RecordCommand implements DiscordCommand {

    private final RecordingService recordingService;

    public RecordCommand(GuildMessageReceivedEvent event, RecordingService recordingService) {
        this.recordingService = recordingService;
    }

    @Override
    public void execute(DiscordActions actions) {
        actions.connect();
        if (recordingService.isRecording())
            actions.send("I'm already recording. Leave me alone.");
        else {
            recordingService.beginRecording();
            actions.setReceivingHandler(new DemultiplexingHandler(recordingService));
        }
    }

}
