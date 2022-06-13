package net.picklepark.discord.command.audio;

import net.picklepark.discord.adaptor.MessageReceivedActions;
import net.picklepark.discord.command.DiscordCommand;
import net.picklepark.discord.constants.AuthLevel;

public class SofterAudioCommand implements DiscordCommand {

  @Override
  public void execute(MessageReceivedActions actions) {
    int volume = (int) (0.8 * actions.getVolume());
    actions.setVolume(volume);
    actions.send("The volume is now " + volume);
  }

  @Override
  public AuthLevel requiredAuthLevel() {
    return AuthLevel.ADMIN;


  }

  @Override
  public String example() {
      return "softer";
  }

  @Override
  public String helpMessage() {
    return "Turn the volume down.";
  }

  @Override
  public String userInput() {
    return "softer";
  }

}
