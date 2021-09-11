package net.picklepark.discord.command.audio;

import net.picklepark.discord.adaptor.DiscordActions;
import net.picklepark.discord.command.audio.DiscordAudioCommand;
import net.picklepark.discord.command.audio.util.AudioContext;

public class PauseAudioCommand extends DiscordAudioCommand {

  public PauseAudioCommand(AudioContext context) {
    super(context);
  }

  @Override
  public void execute(DiscordActions actions) {
    guildPlayer.player.setPaused(true);
    actions.send("Paused");
  }

}
