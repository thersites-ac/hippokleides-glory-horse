package cogbog.discord.config;

import cogbog.discord.service.DocumentFetcher;
import cogbog.discord.service.ElementScraper;
import cogbog.discord.service.EmbedRenderer;
import cogbog.discord.service.Transformer;
import cogbog.discord.service.impl.*;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import cogbog.discord.model.Feat;
import cogbog.discord.model.Spell;

public class EmbedModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(DocumentFetcher.class).to(DefaultDocumentFetcher.class);
        bind(ElementScraper.class).to(DefaultElementScraper.class);
    }

    @Provides
    EmbedRenderer<Feat> featEmbedRenderer() {
        return new FeatRenderer();
    }

    @Provides EmbedRenderer<Spell> spellEmbedRenderer() {
        return new SpellRenderer();
    }

    @Provides
    Transformer<Feat> featTransformer() {
        return new DefaultFeatTransformer();
    }

    @Provides Transformer<Spell> spellTransformer() {
        return new DefaultSpellTransformer();
    }
}
