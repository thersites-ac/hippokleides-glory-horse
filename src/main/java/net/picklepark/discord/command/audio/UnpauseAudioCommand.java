package net.picklepark.discord.command.audio;

import net.picklepark.discord.adaptor.DiscordActions;
import net.picklepark.discord.annotation.Help;
import net.picklepark.discord.annotation.UserInput;
import net.picklepark.discord.command.DiscordCommand;

@UserInput("unpause")
@Help(name = "unpause", message = "Unpause the current track.")
public class UnpauseAudioCommand implements DiscordCommand {

  @Override
  public void execute(DiscordActions actions) {
    actions.unpause();
  }

}
