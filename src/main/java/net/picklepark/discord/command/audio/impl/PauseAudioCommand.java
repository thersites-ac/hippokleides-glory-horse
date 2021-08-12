package net.picklepark.discord.command.audio.impl;

import net.picklepark.discord.command.audio.DiscordAudioCommand;
import net.picklepark.discord.command.audio.util.AudioContext;

public class PauseAudioCommand extends DiscordAudioCommand {
  @Override
  public void execute() {
    guildPlayer.player.setPaused(true);
    acknowledge("Paused");
  }

  public PauseAudioCommand(AudioContext context) {
    super(context);
  }
}
