package cogbog.discord.command.audio;

import cogbog.discord.adaptor.MessageReceivedActions;
import cogbog.discord.command.DiscordCommand;
import cogbog.discord.model.AuthLevel;

public class LouderAudioCommand implements DiscordCommand {

  @Override
  public void execute(MessageReceivedActions actions) {
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
