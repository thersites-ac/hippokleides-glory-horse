package net.picklepark.discord.command.audio;

import net.picklepark.discord.adaptor.DiscordActions;
import net.picklepark.discord.command.audio.DiscordAudioCommand;
import net.picklepark.discord.command.audio.util.AudioContext;

public class SofterAudioCommand extends DiscordAudioCommand {

  public SofterAudioCommand(AudioContext context) {
    super(context);
  }

  @Override
  public void execute(DiscordActions actions) {
    int volume = (int) (0.8 * guildPlayer.player.getVolume());
    guildPlayer.player.setVolume(volume);
    actions.send("The volume is now " + volume);
  }

}
