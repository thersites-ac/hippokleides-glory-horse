package net.picklepark.discord.parse;

import java.util.regex.Pattern;

public class CommandDsl {

    private final String dsl;

    public CommandDsl(String dsl) {
        this.dsl = dsl;
    }

    public Pattern toPattern() {
        return Pattern.compile(dsl);
    }
}
