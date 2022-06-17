package net.picklepark.discord.service.impl;

import net.picklepark.discord.command.audio.PlayClipCommand;
import net.picklepark.discord.model.LocalClip;
import net.picklepark.discord.service.ClipManager;
import net.picklepark.discord.service.RemoteStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class ClipManagerImpl implements ClipManager {

    private static final Logger logger = LoggerFactory.getLogger(ClipManagerImpl.class);

    private final Map<String, Map<String, PlayClipCommand>> localStore;
    private final RemoteStorageService storageService;

    @Inject
    public ClipManagerImpl(RemoteStorageService storageService) {
        localStore = new ConcurrentHashMap<>();
        this.storageService = storageService;
    }

    @Override
    public PlayClipCommand lookup(String guild, String command) {
        Map<String, PlayClipCommand> titles = localStore.get(guild);
        return titles == null? null: titles.get(command);
    }

    @Override
    public void put(LocalClip clip) {
        // FIXME: ideally we'd notify the guild where the clip originated
        // UPDATE: we're a little closer on this
        logger.info("Putting " + clip.toString());
        localStore.computeIfAbsent(clip.getGuild(), g -> new ConcurrentHashMap<>());
        localStore.get(clip.getGuild()).put(clip.getTitle(), new PlayClipCommand(storageService, clip.getPath()));
    }

    @Override
    public Collection<String> getAllCommandNames(String guild) {
        Map<String, PlayClipCommand> titles = localStore.get(guild);
        return titles == null? Collections.emptySet(): titles.keySet();
    }

    @Override
    public void delete(String guild, String clip) {
        Map<String, PlayClipCommand> titles = localStore.get(guild);
        if (titles != null)
            titles.remove(clip);
    }

    @Override
    public void clear(String guild) {
        Map<String, PlayClipCommand> titles = localStore.get(guild);
        if (titles != null)
            titles.clear();
    }
}