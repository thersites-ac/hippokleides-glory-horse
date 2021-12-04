package net.picklepark.discord.config;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import net.picklepark.discord.model.Feat;
import net.picklepark.discord.model.Spell;
import net.picklepark.discord.service.*;
import net.picklepark.discord.service.impl.*;

public class EmbedModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(DocumentFetcher.class).to(DefaultDocumentFetcher.class);
        bind(ElementScraper.class).to(DefaultElementScraper.class);
    }

    @Provides EmbedRenderer<Feat> featEmbedRenderer() {
        return new FeatRenderer();
    }

    @Provides EmbedRenderer<Spell> spellEmbedRenderer() {
        return new SpellRenderer();
    }

    @Provides Transformer<Feat> featTransformer() {
        return new DefaultFeatTransformer();
    }

    @Provides Transformer<Spell> spellTransformer() {
        return new DefaultSpellTransformer();
    }
}
