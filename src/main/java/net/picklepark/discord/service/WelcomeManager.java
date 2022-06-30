package net.picklepark.discord.service;

import net.picklepark.discord.model.LocalClip;

import java.io.IOException;

public interface WelcomeManager {
    LocalClip welcome(long user, String guild);
    void set(long user, String guild, LocalClip clip) throws IOException;
}
