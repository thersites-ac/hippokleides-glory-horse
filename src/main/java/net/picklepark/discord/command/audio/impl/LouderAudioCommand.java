package net.picklepark.discord.command.audio.impl;

import net.picklepark.discord.command.audio.util.AudioContext;
import net.picklepark.discord.command.audio.DiscordAudioCommand;

public class LouderAudioCommand extends DiscordAudioCommand {
  @Override
  public void execute() {
    int volume = (int) (1.25 * guildPlayer.player.getVolume());
    guildPlayer.player.setVolume(volume);
    acknowledge("The volume is now " + volume);
  }

  public LouderAudioCommand(AudioContext context) {
    super(context);
  }
}
