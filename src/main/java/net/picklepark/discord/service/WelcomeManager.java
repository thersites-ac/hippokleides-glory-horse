package net.picklepark.discord.service;

import net.picklepark.discord.model.LocalClip;

import java.io.IOException;

public interface WelcomeManager {
    LocalClip welcome(String user, String guild);
    void set(String user, String guild, LocalClip clip) throws IOException;
}
