package net.picklepark.discord.command.audio;

import net.picklepark.discord.adaptor.DiscordActions;
import net.picklepark.discord.annotation.Auth;
import net.picklepark.discord.annotation.Help;
import net.picklepark.discord.annotation.UserInput;
import net.picklepark.discord.command.DiscordCommand;

@UserInput("louder")
@Help(name = "louder", message = "Crank it up!!!")
@Auth(Auth.Level.ADMIN)
public class LouderAudioCommand implements DiscordCommand {

  @Override
  public void execute(DiscordActions actions) {
    int volume = (int) (1.25 * actions.getVolume());
    actions.setVolume(volume);
    actions.send("The volume is now " + volume);
  }

}
