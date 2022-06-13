package net.picklepark.discord.service;

import net.picklepark.discord.model.LocalClip;

import java.io.IOException;

public interface WelcomeManager {
    LocalClip welcome(String user, String channel);
    void set(String user, String channel, LocalClip clip) throws IOException;
}
