package net.picklepark.discord.command.audio;

import net.picklepark.discord.command.audio.DiscordAudioCommand;
import net.picklepark.discord.command.audio.util.AudioContext;

public class ChangeVolumeAudioCommand extends DiscordAudioCommand {
  @Override
  public void execute() {
    guildPlayer.player.setVolume(volume);
    acknowledge("OK");
  }

  private int volume;

  public ChangeVolumeAudioCommand(String volume, AudioContext context) {
    super(context);
    this.volume = Integer.parseInt(volume);
  }
}
