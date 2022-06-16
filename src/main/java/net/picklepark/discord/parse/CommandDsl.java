package net.picklepark.discord.parse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Pattern;

public class CommandDsl {

    private static final Logger logger = LoggerFactory.getLogger(CommandDsl.class);

    private static final String WHITESPACE = "\\s+";

    private final String dsl;
    private final String[] chunks;
    private final String regex;

    public CommandDsl(String dsl) {
        this.dsl = dsl;
        chunks = dsl.split(" ");
        regex = String.join(WHITESPACE, chunks);
    }

    public Pattern toPattern() {
        logger.info("regex: " + regex);
        return Pattern.compile(regex);
    }
}
