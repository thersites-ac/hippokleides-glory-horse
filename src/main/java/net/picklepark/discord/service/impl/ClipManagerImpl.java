package net.picklepark.discord.service.impl;

import net.picklepark.discord.command.audio.PlayClipCommand;
import net.picklepark.discord.model.LocalClip;
import net.picklepark.discord.service.ClipManager;

import javax.inject.Singleton;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class ClipManagerImpl implements ClipManager {

    private final ConcurrentHashMap<String, PlayClipCommand> localStore;

    public ClipManagerImpl() {
        localStore = new ConcurrentHashMap<>();
    }

    @Override
    public PlayClipCommand lookup(String command) {
        return localStore.get(command);
    }

    @Override
    public void put(LocalClip clip) {
        // FIXME: ideally we'd notify the guild where the clip originated
        String name = clip.getTitle();
        localStore.put(name, new PlayClipCommand(clip.getPath()));
    }

    @Override
    public Collection<String> getAllCommandNames() {
        return localStore.keySet();
    }

    @Override
    public void delete(String clip) {
        localStore.remove(clip);
    }

    @Override
    public void clear() {
        localStore.clear();
    }
}
