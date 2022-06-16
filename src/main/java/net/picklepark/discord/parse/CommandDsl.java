package net.picklepark.discord.parse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Pattern;

public class CommandDsl {

    private static final Logger logger = LoggerFactory.getLogger(CommandDsl.class);

    private static final String WHITESPACE = "\\s+";
    private static final String VARIABLE_DECLARATION = "<(\\w+)>";
    private static final String VARIABLE_EXPRESSION = "\\(?<$1>\\\\w.*\\\\w\\)";

    private final String dsl;
    private final String regex;

    public CommandDsl(String dsl) {
        this.dsl = dsl;
        regex = dsl
                .replace(" ", WHITESPACE)
                .replaceAll(VARIABLE_DECLARATION, VARIABLE_EXPRESSION);
    }

    public Pattern toPattern() {
        logger.info("dsl: " + dsl + "; regex: " + regex);
        return Pattern.compile(regex);
    }
}
