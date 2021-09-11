package net.picklepark.discord.command.audio;

import net.picklepark.discord.adaptor.DiscordActions;
import net.picklepark.discord.command.audio.DiscordAudioCommand;
import net.picklepark.discord.command.audio.util.AudioContext;

public class UnpauseAudioCommand extends DiscordAudioCommand {

  public UnpauseAudioCommand(AudioContext context) {
    super(context);
  }

  @Override
  public void execute(DiscordActions actions) {
    guildPlayer.player.setPaused(false);
    actions.send("Unpaused");
  }

}
