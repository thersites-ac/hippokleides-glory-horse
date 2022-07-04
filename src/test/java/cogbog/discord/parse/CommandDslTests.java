package cogbog.discord.parse;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class CommandDslTests {

    private Pattern pattern;
    private Matcher matcher;

    @Test
    public void emptyStringMatchesEmptyString() {
        givenSyntax("");
        thenMatches("");
    }

    @Test
    public void emptyStringDoesNotMatchNonemptyString() {
        givenSyntax("");
        thenDoesNotMatch("foo");
    }

    @Test
    public void constantStringMatchesConstantOnly() {
        givenSyntax("foo");
        thenMatches("foo");
        thenDoesNotMatch("bar");
    }

    @Test
    public void matchesExactWhitespace() {
        String s = "foo bar";
        givenSyntax(s);
        thenMatches(s);
    }

    @Test
    public void permitsExtraWhitespaceBetweenTokens() {
        givenSyntax("foo bar");
        thenMatches("foo \t \n  \rbar");
    }

    @Test
    public void requiresWhitespace() {
        givenSyntax("foo bar");
        thenDoesNotMatch("foobar");
    }

    @Test
    public void matchesVariables() {
        givenSyntax("<foo>");
        thenMatches("bar");
        thenVariableIs("foo", "bar");
    }

    @Test
    public void variablesCannotBeEmpty() {
        givenSyntax("<foo>");
        thenDoesNotMatch("");
    }

    @Test
    public void requiresVariables() {
        givenSyntax("foo <bar>");
        thenDoesNotMatch("foo ");
    }

    @Test
    public void variablesMustStartAndEndWithNonWhitespace() {
        givenSyntax("<foo>");
        thenDoesNotMatch(" foo ");
    }

    @Test
    public void keepsWhitespaceInVariables() {
        givenSyntax("foo <bar> baz");
        thenMatches("foo qu ux baz");
        thenVariableIs("bar", "qu ux");
    }

    @Test
    public void minVariableLengthIsOneCharacter() {
        givenSyntax("repeat <foo> <bar> times");
        thenMatches("repeat wow 9 times");
        thenVariableIs("foo", "wow");
        thenVariableIs("bar", "9");
    }

    @Test
    public void greedilyConsumesWhitespace() {
        givenSyntax("foo <bar> baz");
        thenMatches("foo  bar  baz");
        thenVariableIs("bar", "bar");
    }

    private void givenSyntax(String dsl) {
        pattern = new CommandDsl(dsl).toPattern();
    }

    private void thenMatches(String message) {
        matcher = pattern.matcher(message);
        assertTrue(matcher.matches());
    }

    private void thenDoesNotMatch(String message) {
        assertFalse(pattern.matcher(message).matches());
    }

    private void thenVariableIs(String variable, String value) {
        assertEquals(value, matcher.group(variable));
    }
}
