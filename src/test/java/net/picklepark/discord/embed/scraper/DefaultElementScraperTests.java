package net.picklepark.discord.embed.scraper;

import net.picklepark.discord.embed.scraper.net.DocumentFetcher;
import net.picklepark.discord.exception.NotFoundException;
import net.picklepark.discord.exception.NullDocumentException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;
import java.util.List;

@RunWith(JUnit4.class)
public class DefaultElementScraperTests {

    private Document fetcherOutput;
    private Exception exception;
    private ElementScraper scraper;
    private List<Element> result;

    @Test(expected = IOException.class)
    public void propagatesBadUrlException() throws IOException {
        DefaultElementScraper failure = new DefaultElementScraper(new ThrowFetcher());
        exception = new IOException();
        failure.scrapeFeatNodes("foo", "internet");
    }

    @Test(expected = NotFoundException.class)
    public void throwsExceptionWhenElementNotFound() throws IOException {
        DefaultElementScraper mock = new DefaultElementScraper(new MockFetcher());
        fetcherOutput = new Document("foo");
        mock.scrapeFeatNodes("foo", "internet");
    }

    @Test(expected = NullDocumentException.class)
    public void throwsExceptionWhenFetcherReturnsNull() throws IOException {
        DefaultElementScraper mock = new DefaultElementScraper(new MockFetcher());
        fetcherOutput = null;
        mock.scrapeFeatNodes("foo", "internet");
    }

    @Test
    public void scrapesSpell() throws IOException {
        givenMockFetcherReturnsCoreSpellPageSnippets();
        whenScrapeMagicMissile();
        thenResultComesFromSpellPage();
    }

    private void thenResultComesFromSpellPage() {
        String spellContent = magicMissileHtml().text();
        String scrapedContent = new Elements(result).text();
        Assert.assertEquals(spellContent, scrapedContent);
    }

    private void givenMockFetcherReturnsCoreSpellPageSnippets() {
        scraper = new DefaultElementScraper(new MockSpellFetcher());
    }

    private void whenScrapeMagicMissile() throws IOException {
        result = scraper.scrapeCoreSpell("magic missile");
    }

    private class ThrowFetcher implements DocumentFetcher {
        @Override
        public Document fetch(String url) throws IOException {
            try {
                throw exception;
            } catch (IOException | RuntimeException e) {
                throw e;
            } catch (Exception ignored) {
            }
            return null;
        }
    }

    private class MockFetcher implements DocumentFetcher {
        @Override
        public Document fetch(String url) throws IOException {
            return fetcherOutput;
        }
    }

    private class MockSpellFetcher implements DocumentFetcher {
        @Override
        public Document fetch(String url) throws IOException {
            if (url.contains("spellLists.html"))
                return coreSpellListHtml();
            else
                return magicMissileHtml();
        }
    }

    private Document magicMissileHtml() {
        String magicMissile = "\t\t\t\t<p id=\"magic-missile\" class=\"stat-block-title\"><b>Magic Missile</b></p>\n" +
                "\t\t\t\t<p class=\"stat-block-1\"><b>School</b> evocation [force]; <b>Level</b> sorcerer/wizard 1</p>\n" +
                "\t\t\t\t<p class=\"stat-block-1\"><b>Casting Time</b> 1 standard action</p>\n" +
                "\t\t\t\t<p class=\"stat-block-1\"><b>Components</b> V, S</p>\n" +
                "\t\t\t\t<p class=\"stat-block-1\"><b>Range </b>medium (100 ft. + 10 ft./level)</p>\n" +
                "\t\t\t\t<p class=\"stat-block-1\"><b>Targets</b> up to five creatures, no two of which can be more than 15 ft. apart</p>\n" +
                "\t\t\t\t<p class=\"stat-block-1\"><b>Duration</b> instantaneous</p>\n" +
                "\t\t\t\t<p class=\"stat-block-1\"><b>Saving Throw</b> none; <b><a href = \"../glossary.html#spell-resistance\" >Spell Resistance</a></b> yes</p>\n" +
                "\t\t\t\t<p>A missile of magical energy darts forth from your fingertip and strikes its target, dealing 1d4+1 points of force damage.</p>\n" +
                "\t\t\t\t<p>The missile strikes unerringly, even if the target is in melee combat, so long as it has less than total cover or total concealment. Specific parts of a creature can't be singled out. Objects are not damaged by the spell.</p>\n" +
                "\t\t\t\t<p>For every two caster levels beyond 1st, you gain an additional missile&mdash;two at 3rd level, three at 5th, four at 7th, and the maximum of five missiles at 9th level or higher. If you shoot multiple missiles, you can have them strike a single creature or several creatures. A single missile can strike only one creature. You must designate targets before you check for <a href = \"../glossary.html#spell-resistance\" >spell resistance</a> or roll damage.</p>\n";
        return Jsoup.parse(magicMissile);
    }

    private Document coreSpellListHtml() {
        String spellListSnippet = "\t\t\t\t<p><b><a href=\"spells/floatingDisk.html#floating-disk\" >Floating Disk</a></b>: Creates 3-ft.-diameter horizontal disk that holds 100 lbs./level.</p>\n" +
                "\t\t\t\t<p><b><a href=\"spells/magicMissile.html#magic-missile\" >Magic Missile</a></b>: 1d4+1 damage; +1 missile per two levels above 1st (max 5).</p>\n" +
                "\t\t\t\t<p><b><a href=\"spells/shockingGrasp.html#shocking-grasp\" >Shocking Grasp</a></b>: Touch delivers 1d6/level electricity damage (max 5d6).</p>\n";
        return Jsoup.parse(spellListSnippet);
    }

}