package net.picklepark.discord.command.audio.impl;

import net.picklepark.discord.command.audio.AudioContext;
import net.picklepark.discord.command.audio.DiscordAudioCommand;

public class SofterAudioCommand extends DiscordAudioCommand {
  @Override
  public void execute() {
    int volume = (int) (0.8 * guildPlayer.player.getVolume());
    guildPlayer.player.setVolume(volume);
    acknowledge("The volume is now " + volume);
  }

  public SofterAudioCommand(AudioContext context) {
    super(context);
  }
}
