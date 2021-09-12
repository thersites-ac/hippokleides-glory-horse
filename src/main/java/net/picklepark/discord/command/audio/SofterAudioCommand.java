package net.picklepark.discord.command.audio;

import net.picklepark.discord.adaptor.DiscordActions;
import net.picklepark.discord.annotation.UserInput;
import net.picklepark.discord.command.DiscordCommand;

@UserInput("softer")
public class SofterAudioCommand implements DiscordCommand {

  @Override
  public void execute(DiscordActions actions) {
    int volume = (int) (0.8 * actions.getVolume());
    actions.setVolume(volume);
    actions.send("The volume is now " + volume);
  }

}
