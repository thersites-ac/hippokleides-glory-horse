package cogbog.discord.parse;

import cogbog.discord.exception.InvalidCommandDslException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandDsl {

    private static final Logger logger = LoggerFactory.getLogger(CommandDsl.class);

    private static final String WHITESPACE = "\\s+";
    private static final String VARIABLE_DECLARATION = "<(\\w+)>";
    private static final String VARIABLE_TRANSFORM_A = "!!a:$1a";
    private static final String VARIABLE_TRANSFORM_B = "!!b:$1b";
    private static final String VARIABLE_TRANSFORM = "(" + VARIABLE_TRANSFORM_A + "|" + VARIABLE_TRANSFORM_B + ")";
    private static final String VARIABLE_SEMANTICS_A = "(?<$1>(\\\\w|\\\\w.*\\\\w))";
    private static final String VARIABLE_SEMANTICS_B = "\\<\\@(?<$1>\\\\d+)\\>";
    private static final String VARIABLE_A = "!!a:(\\w+)";
    private static final String VARIABLE_B = "!!b:(\\w+)";

    private final String dsl;
    private final String regex;
    private String text;
    private Matcher matcher;

    public CommandDsl(String dsl) {
        if (dsl.contains("!!"))
            throw new InvalidCommandDslException(dsl);
        this.dsl = dsl;
        regex = dsl
                .replace(" ", WHITESPACE)
                .replaceAll(VARIABLE_DECLARATION, VARIABLE_TRANSFORM)
                .replaceAll(VARIABLE_A, VARIABLE_SEMANTICS_A)
                .replaceAll(VARIABLE_B, VARIABLE_SEMANTICS_B);
   }

    public boolean match(String text) {
        this.text = text;
        matcher = Pattern.compile(regex).matcher(text);
        return matcher.matches();
    }

    public String get(String arg) {
        var arga = matcher.group(arg + "a");
        var argb = matcher.group(arg + "b");
        return arga == null? argb: arga;
    }

    // fixme: do I need to provide a hashCode implementation? is this one any good?
    @Override
    public int hashCode() {
        if (text == null)
            return dsl.hashCode();
        else
            return dsl.hashCode() + text.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CommandDsl) {
            CommandDsl other = (CommandDsl) obj;
            return other.text.equals(text) && other.dsl.equals(dsl);
        } else {
            return false;
        }
    }
}
