package net.picklepark.discord.model;

public enum AuthLevel implements Comparable<AuthLevel> {
    BANNED,
    ANY,
    USER,
    ADMIN,
    OWNER;

    public boolean satisfies(AuthLevel level) {
        return compareTo(level) >= 0;
    }
}