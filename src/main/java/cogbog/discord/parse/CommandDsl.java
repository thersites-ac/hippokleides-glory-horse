package cogbog.discord.parse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Pattern;

// TODO: absorb the Pattern/Matcher functionality into this so it's hidden
public class CommandDsl {

    private static final Logger logger = LoggerFactory.getLogger(CommandDsl.class);

    private static final String WHITESPACE = "\\s+";
    private static final String VARIABLE_DECLARATION = "<(\\w+)>";
    // fixme: this is a little too permissive--it'll accept "<@joe" or "bob dole>"--but that's a regex limitation
    // todo: type variables, so I can apply a different pattern when trying to match a User vs. a generic clip title
    private static final String VARIABLE_SEMANTICS = "(\\<\\@)?(?<$1>(\\\\w|\\\\w.*\\\\w|\\\\d+))\\>?";

    private final String dsl;
    private final String regex;

    public CommandDsl(String dsl) {
        this.dsl = dsl;
        regex = dsl
                .replace(" ", WHITESPACE)
                .replaceAll(VARIABLE_DECLARATION, VARIABLE_SEMANTICS);
    }

    public Pattern toPattern() {
        logger.debug("dsl: " + dsl + "; regex: " + regex);
        return Pattern.compile(regex);
    }
}
