package net.picklepark.discord.service;

import net.picklepark.discord.model.LocalClip;

public interface WelcomeManager {
    LocalClip welcome(String user, String channel);
}
