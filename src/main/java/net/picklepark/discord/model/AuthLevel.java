package net.picklepark.discord.model;

// TODO (important): add more nuance to this
public enum AuthLevel {
    ANY,
    ADMIN,
    OWNER
}

// something like:
enum Privilege {
    TEXT,
    AUDIO_READ,
    AUDIO_WRITE,
    MODERATOR,
    CHANNEL_OWNER
}
