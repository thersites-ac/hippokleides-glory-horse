package net.picklepark.discord.service;

import net.picklepark.discord.command.audio.PlayClipCommand;
import net.picklepark.discord.model.LocalClip;

import java.util.Collection;

public interface ClipManager {
    PlayClipCommand lookup(String command);
    void put(LocalClip clip);
    Collection<String> getAllCommandNames();
    void delete(String clip);
    void clear();
}
