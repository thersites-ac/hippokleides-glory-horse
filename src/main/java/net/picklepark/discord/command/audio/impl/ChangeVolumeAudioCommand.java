package net.picklepark.discord.command.audio.impl;

import net.picklepark.discord.command.audio.AudioContext;
import net.picklepark.discord.command.audio.DiscordAudioCommand;

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
