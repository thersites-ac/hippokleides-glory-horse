package net.picklepark.discord.service;

import net.picklepark.discord.command.audio.ClipCommand;
import net.picklepark.discord.model.LocalClip;

import java.util.Collection;

public interface ClipManager {
    ClipCommand lookup(String command);
    void put(LocalClip clip);
    Collection<String> getAllCommandNames();
    void delete(String clip);
}
