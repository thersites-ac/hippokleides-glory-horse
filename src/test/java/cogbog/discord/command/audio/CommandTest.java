package cogbog.discord.command.audio;

import org.junit.Before;
import tools.SpyMessageReceivedActions;

abstract public class CommandTest {

    protected SpyMessageReceivedActions actions;

    @Before
    public void setup() {
        actions = new SpyMessageReceivedActions();
        actions.setUserInput(input());
        actions.setGuildName(guild());
        onSetup();
    }

    abstract String input();
    abstract void onSetup();
    abstract String guild();
}
