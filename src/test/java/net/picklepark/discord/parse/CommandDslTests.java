package net.picklepark.discord.parse;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.regex.Pattern;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class CommandDslTests {

    private Pattern pattern;

    @Test
    public void emptyStringMatchesEmptyString() {
        whenCompile("");
        thenMatches("");
    }

    @Test
    public void emptyStringDoesNotMatchNonemptyString() {
        whenCompile("");
        thenDoesNotMatch("foo");
    }

    @Test
    public void constantStringMatchesConstantOnly() {
        whenCompile("foo");
        thenMatches("foo");
        thenDoesNotMatch("bar");
    }

    @Test
    public void matchesExactWhitespace() {
        String s = "foo bar";
        whenCompile(s);
        thenMatches(s);
    }

    @Test
    public void permitsExtraWhitespaceInMessage() {
        whenCompile("foo bar");
        thenMatches("foo \t \n  \rbar");
    }

    @Test
    public void requiresWhitespace() {
        whenCompile("foo bar");
        thenDoesNotMatch("foobar");
    }

    @Test
    public void matchesVariables() {
        fail();
    }

    @Test
    public void requiresVariables() {
        fail();
    }
    
    @Test
    public void variablesStartAndEndWithNonWhitespace() {
        fail();
    }

    private void whenCompile(String dsl) {
        pattern = new CommandDsl(dsl).toPattern();
    }

    private void thenMatches(String message) {
        assertTrue(pattern.matcher(message).matches());
    }

    private void thenDoesNotMatch(String message) {
        assertFalse(pattern.matcher(message).matches());
    }
}
