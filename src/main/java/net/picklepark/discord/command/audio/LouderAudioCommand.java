package net.picklepark.discord.command.audio;

import net.picklepark.discord.adaptor.DiscordActions;
import net.picklepark.discord.command.audio.util.AudioContext;

public class LouderAudioCommand extends DiscordAudioCommand {

  public LouderAudioCommand(AudioContext context) {
    super(context);
  }

  @Override
  public void execute(DiscordActions actions) {
    int volume = (int) (1.25 * guildPlayer.player.getVolume());
    guildPlayer.player.setVolume(volume);
    actions.send("The volume is now " + volume);
  }

}
