package net.picklepark.discord.command.audio;

import net.picklepark.discord.adaptor.MessageReceivedActions;
import net.picklepark.discord.command.DiscordCommand;
import net.picklepark.discord.model.AuthLevel;

public class UnpauseAudioCommand implements DiscordCommand {

  @Override
  public void execute(MessageReceivedActions actions) {
    actions.unpause();
  }

  @Override
  public AuthLevel requiredAuthLevel() {
    return AuthLevel.ADMIN;
  }

  @Override
  public String example() {
    return "unpause";
  }

  @Override
  public String helpMessage() {
    return "Unpause the current track.";
  }

  @Override
  public String userInput() {
    return "unpause";
  }

}
