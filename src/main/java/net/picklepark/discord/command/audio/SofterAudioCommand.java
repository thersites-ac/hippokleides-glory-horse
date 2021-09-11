package net.picklepark.discord.command.audio;

import net.picklepark.discord.command.audio.DiscordAudioCommand;
import net.picklepark.discord.command.audio.util.AudioContext;

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
