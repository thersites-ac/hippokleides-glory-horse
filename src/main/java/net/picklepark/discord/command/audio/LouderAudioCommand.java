package net.picklepark.discord.command.audio;

import net.picklepark.discord.adaptor.DiscordActions;
import net.picklepark.discord.command.DiscordCommand;
import net.picklepark.discord.constants.AuthLevel;

public class LouderAudioCommand implements DiscordCommand {

  @Override
  public void execute(DiscordActions actions) {
    int volume = (int) (1.25 * actions.getVolume());
    actions.setVolume(volume);
    actions.send("The volume is now " + volume);
  }

  @Override
  public AuthLevel requiredAuthLevel() {
    return AuthLevel.ADMIN;
  }

  @Override
  public String example() {
    return "louder";
  }

  @Override
  public String helpMessage() {
    return "Crank it up!!!";
  }

  @Override
  public String userInput() {
    return "louder";
  }

}
