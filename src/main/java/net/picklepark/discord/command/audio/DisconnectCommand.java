package net.picklepark.discord.command.audio;

import net.dv8tion.jda.api.managers.AudioManager;
import net.picklepark.discord.command.audio.DiscordAudioCommand;
import net.picklepark.discord.command.audio.util.AudioContext;

public class DisconnectCommand extends DiscordAudioCommand {

    public DisconnectCommand(AudioContext context) {
        super(context);
    }

    @Override
    public void execute() {
        AudioManager manager = channel.getGuild().getAudioManager();
        if (manager.isConnected())
            manager.closeAudioConnection();
    }
}
