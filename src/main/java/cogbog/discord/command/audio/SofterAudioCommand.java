package cogbog.discord.command.audio;

import cogbog.discord.adaptor.MessageReceivedActions;
import cogbog.discord.command.DiscordCommand;
import cogbog.discord.model.AuthLevel;

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
