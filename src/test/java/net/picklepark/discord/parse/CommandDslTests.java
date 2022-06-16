package net.picklepark.discord.parse;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.regex.Pattern;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class CommandDslTests {

    private Pattern pattern;

    @Test
    public void emptyStringMatchesEmptyString() {
        givenCompile("");
        thenMatches("");
    }

    @Test
    public void emptyStringDoesNotMatchNonemptyString() {
        givenCompile("");
        thenDoesNotMatch("foo");
    }

    @Test
    public void constantStringMatchesConstantOnly() {
        givenCompile("foo");
        thenMatches("foo");
        thenDoesNotMatch("bar");
    }

    @Test
    public void matchesExactWhitespace() {
        String s = "foo bar";
        givenCompile(s);
        thenMatches(s);
    }

    @Test
    public void permitsExtraWhitespaceInMessage() {
        givenCompile("foo bar");
        thenMatches("foo \t \n  \rbar");
    }

    private void givenCompile(String dsl) {
        pattern = new CommandDsl(dsl).toPattern();
    }

    private void thenMatches(String message) {
        assertTrue(pattern.matcher(message).matches());
    }

    private void thenDoesNotMatch(String message) {
        assertFalse(pattern.matcher(message).matches());
    }
}
