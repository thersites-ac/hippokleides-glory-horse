package net.picklepark.discord.command.audio.impl;

import net.picklepark.discord.command.audio.AudioContext;
import net.picklepark.discord.command.audio.DiscordAudioCommand;

public class UnpauseAudioCommand extends DiscordAudioCommand {
  @Override
  public void execute() {
    guildPlayer.player.setPaused(false);
    acknowledge("Unpaused");
  }

  public UnpauseAudioCommand(AudioContext context) {
    super(context);
  }
}
