package net.picklepark.discord.command.audio;

import net.picklepark.discord.adaptor.DiscordActions;
import net.picklepark.discord.command.audio.DiscordAudioCommand;
import net.picklepark.discord.command.audio.util.AudioContext;

public class ChangeVolumeAudioCommand extends DiscordAudioCommand {

  public ChangeVolumeAudioCommand(String volume, AudioContext context) {
    super(context);
    this.volume = Integer.parseInt(volume);
  }

  @Override
  public void execute(DiscordActions actions) {
    guildPlayer.player.setVolume(volume);
    actions.send("OK");
  }

  private int volume;

}
