package net.picklepark.discord.tests;

import net.picklepark.discord.model.ScrapeResult;
import net.picklepark.discord.exception.ResourceNotFoundException;
import net.picklepark.discord.exception.NullDocumentException;
import net.picklepark.discord.service.DocumentFetcher;
import net.picklepark.discord.service.ElementScraper;
import net.picklepark.discord.service.impl.DefaultElementScraper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;

@RunWith(JUnit4.class)
public class DefaultElementScraperTests {

    private Document fetcherOutput;
    private Exception exception;
    private ElementScraper scraper;
    private ScrapeResult result;
    private String magicMissileHtml;

    @Before
    public void setup() {
        scraper = new DefaultElementScraper(new MockFetcher());
    }

    @Test(expected = IOException.class)
    public void propagatesBadUrlException() throws IOException, ResourceNotFoundException {
        givenScraperThrowsIOException();
        whenScrapeFeatFromPage("");
    }

    @Test(expected = ResourceNotFoundException.class)
    public void throwsExceptionWhenElementNotFound() throws IOException, ResourceNotFoundException {
        givenFetcherReturnsEmptyDocument();
        whenScrapeFeatFromPage("");
    }

    @Test(expected = NullDocumentException.class)
    public void throwsExceptionWhenFetcherReturnsNull() throws IOException, ResourceNotFoundException {
        givenFetcherReturnsNull();
        whenScrapeFeatFromPage("");
    }

    @Test
    public void doesNotRecordFeatSource() throws IOException, ResourceNotFoundException {
        givenMockFetcherReturnsFeat("arcane-armor-mastery");
        whenScrapeFeatFromPage("internet");
        thenResultHasNullSource();
    }

    @Test
    public void recordsFeatUrl() throws IOException, ResourceNotFoundException {
        givenMockFetcherReturnsFeat("arcane-armor-mastery");
        whenScrapeFeatFromPage("internet");
        thenResultHasUrl("internet#arcane-armor-mastery");
    }

    @Test
    public void scrapesSpell() throws IOException, ResourceNotFoundException {
        givenMockFetcherReturnsCoreSpellPageSnippets();
        whenScrapeMagicMissile();
        thenResultComesFromSpellPage();
    }

    @Test
    public void stopsScrapingAtDiv() throws IOException, ResourceNotFoundException {
        givenMockFetcherReturnsExtendedSpellPage();
        whenScrapeMagicMissile();
        thenResultDoesNotHaveDiv();
    }

    private void thenResultDoesNotHaveDiv() {
        for (Element e: result.getElements())
            Assert.assertNotEquals("div", e.tagName());
    }

    @Test
    public void recordsSpellUrl() throws IOException, ResourceNotFoundException {
        givenMockFetcherReturnsCoreSpellPageSnippets();
        whenScrapeMagicMissile();
        thenUrlIsMagicMissilePage();
    }

    @Test
    public void recordsSpellSouce() throws IOException, ResourceNotFoundException {
        givenMockFetcherReturnsCoreSpellPageSnippets();
        whenScrapeMagicMissile();
        thenSourceIsCoreRulebook();
    }

    private void givenFetcherReturnsNull() {
        fetcherOutput = null;
    }

    private void givenFetcherReturnsEmptyDocument() {
        fetcherOutput = new Document("foo");
    }

    private void givenScraperThrowsIOException() {
        scraper = new DefaultElementScraper(new ThrowFetcher());
        exception = new IOException();
    }

    private void thenResultHasNullSource() {
        Assert.assertNull(result.getSource());
    }

    private void thenResultHasUrl(String url) {
        Assert.assertEquals(url, result.getUrl());
    }

    private void whenScrapeFeatFromPage(String url) throws IOException, ResourceNotFoundException {
        result = scraper.scrapeFeatNodes("arcane-armor-mastery", url);
    }

    private void givenMockFetcherReturnsFeat(String name) {
        String html = "\t\t\t\t<h2 id=\"" + name + "\">Arcane Armor Mastery (Combat)</h2>\n" +
                "\t\t\t\t<p>You have mastered the ability to cast spells while wearing armor.</p>\n" +
                "\t\t\t\t<p class=\"stat-block-1\"><b>Prerequisites:</b> <a href =\"#arcane-armor-training\" >Arcane Armor Training</a>, Medium Armor Proficiency, caster level 7th.</p>\n" +
                "\t\t\t\t<p class=\"stat-block-1\"><b>Benefit:</b> As a swift action, reduce the arcane spell failure chance due to the armor you are wearing by 20% for any spells you cast this round. This bonus replaces, and does not stack with, the bonus granted by <a href =\"#arcane-armor-training\" >Arcane Armor Training</a>.</p>\n";
        fetcherOutput = Jsoup.parse(html);
    }

    private void thenSourceIsCoreRulebook() {
        Assert.assertEquals("Core Rulebook", result.getSource());
    }

    private void thenUrlIsMagicMissilePage() {
        Assert.assertEquals("https://legacy.aonprd.com/coreRulebook/spells/magicMissile.html#magic-missile", result.getUrl());
    }

    private void thenResultComesFromSpellPage() {
        String spellContent = magicMissileHtml().text();
        String scrapedContent = new Elements(result.getElements()).text();
        Assert.assertEquals(spellContent, scrapedContent);
    }

    private void givenMockFetcherReturnsCoreSpellPageSnippets() {
        magicMissileHtml = "\t\t\t\t<p id=\"magic-missile\" class=\"stat-block-title\"><b>Magic Missile</b></p>\n" +
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
        scraper = new DefaultElementScraper(new MockSpellFetcher());
    }

    private void givenMockFetcherReturnsExtendedSpellPage() {
        givenMockFetcherReturnsCoreSpellPageSnippets();
        magicMissileHtml += "<div class=\"footer\">foo</div>";
    }

    private void whenScrapeMagicMissile() throws IOException, ResourceNotFoundException {
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
        public Document fetch(String url) {
            return fetcherOutput;
        }
    }

    private class MockSpellFetcher implements DocumentFetcher {
        @Override
        public Document fetch(String url) {
            if (url.contains("spellLists.html"))
                return coreSpellListHtml();
            else
                return magicMissileHtml();
        }
    }

    private Document magicMissileHtml() {
        return Jsoup.parse(magicMissileHtml);
    }

    private Document coreSpellListHtml() {
        String spellListSnippet = "\t\t\t\t<p><b><a href=\"spells/floatingDisk.html#floating-disk\" >Floating Disk</a></b>: Creates 3-ft.-diameter horizontal disk that holds 100 lbs./level.</p>\n" +
                "\t\t\t\t<p><b><a href=\"spells/magicMissile.html#magic-missile\" >Magic Missile</a></b>: 1d4+1 damage; +1 missile per two levels above 1st (max 5).</p>\n" +
                "\t\t\t\t<p><b><a href=\"spells/shockingGrasp.html#shocking-grasp\" >Shocking Grasp</a></b>: Touch delivers 1d6/level electricity damage (max 5d6).</p>\n";
        return Jsoup.parse(spellListSnippet);
    }

}