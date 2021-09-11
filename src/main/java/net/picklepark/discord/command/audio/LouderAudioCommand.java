package net.picklepark.discord.command.audio;

import net.picklepark.discord.command.audio.DiscordAudioCommand;
import net.picklepark.discord.command.audio.util.AudioContext;

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
