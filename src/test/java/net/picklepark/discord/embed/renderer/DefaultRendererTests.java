package net.picklepark.discord.embed.renderer;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.picklepark.discord.embed.model.Feat;
import net.picklepark.discord.embed.model.FeatDetail;
import net.picklepark.discord.embed.model.Subrule;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RunWith(JUnit4.class)
public class DefaultRendererTests {

    private Feat feat;
    private MessageEmbed embed;
    private DefaultRenderer renderer;

    @Before
    public void setup() {
        renderer = new DefaultRenderer();
    }

    @Test
    public void canCreate() {
        new DefaultRenderer();
    }

    @Test
    public void nameBecomesTitle() {
        Feat feat = Feat.builder()
                .name("foo")
                .featDetails(new ArrayList<>())
                .build();
        MessageEmbed embed = renderer.renderFeat(feat);
        Assert.assertEquals("foo", embed.getTitle());
    }

    @Test
    public void featDetailsBecomeFields() {
        List<FeatDetail> details = Arrays.asList(
                makeFeatDetail("foo", "bar"),
                makeFeatDetail("baz", "quux"));
        Feat feat = Feat.builder()
                .featDetails(details)
                .build();
        MessageEmbed embed = renderer.renderFeat(feat);
        Assert.assertEquals("foo", embed.getFields().get(0).getName());
        Assert.assertEquals("bar", embed.getFields().get(0).getValue());
        Assert.assertEquals("baz", embed.getFields().get(1).getName());
        Assert.assertEquals("quux", embed.getFields().get(1).getValue());
    }

    @Test
    public void footerBecomesFooter() {
        Feat feat = Feat.builder()
                .footer("foo")
                .featDetails(new ArrayList<>())
                .build();
        MessageEmbed embed = renderer.renderFeat(feat);
        Assert.assertEquals("foo", embed.getFooter().getText());
    }

    @Test
    public void rendersClasslessDescription() {
        Feat feat = Feat.builder()
                .featDetails(new ArrayList<>())
                .description("foo")
                .build();
        MessageEmbed embed = renderer.renderFeat(feat);
        Assert.assertEquals("foo", embed.getDescription());
    }

    @Test
    public void sourceBecomesAuthor() {
        Feat feat = Feat.builder()
                .source("foo")
                .featDetails(new ArrayList<>())
                .build();
        MessageEmbed embed = renderer.renderFeat(feat);
        Assert.assertEquals("foo", embed.getAuthor().getName());
    }

    @Test
    public void addsUrl() {
        Assert.fail();
    }

    @Test
    public void subrulesBecomeInlineFields() {
        givenFeatContainsSubrule();
        whenRender();
        thenEmbedContainsInlineFields();
    }

    private void thenEmbedContainsInlineFields() {
        List<Subrule> subrules = feat.getFeatDetails().get(0).getSubrules();
        List<MessageEmbed.Field> inlineFields = embed.getFields().stream()
                .filter(MessageEmbed.Field::isInline)
                .collect(Collectors.toList());
        Assert.assertEquals(subrules.size(), inlineFields.size());
        for (int i = 0; i < subrules.size(); i++) {
            Assert.assertEquals(subrules.get(i).getName(), inlineFields.get(i).getName());
            Assert.assertEquals(subrules.get(i).getText(), inlineFields.get(i).getValue());
        }
    }

    private void whenRender() {
        embed = renderer.renderFeat(feat);
    }

    private void givenFeatContainsSubrule() {
        feat = Feat.builder()
                .featDetails(Collections.singletonList(FeatDetail.builder()
                        .name("name")
                        .text("text")
                        .subrules(Collections.singletonList(Subrule.builder()
                                .name("subrule name")
                                .text("subrule text")
                                .build()))
                        .build()))
                .build();
    }

    private FeatDetail makeFeatDetail(String name, String text) {
        return FeatDetail.builder()
                .name(name)
                .text(text)
                .build();
    }

}
