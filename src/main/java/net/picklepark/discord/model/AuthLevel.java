package net.picklepark.discord.model;

public enum AuthLevel implements Comparable<AuthLevel> {
    BANNED,
    ANY,
    USER,
    ADMIN,
    OWNER
}

class demo {
    public static void main(String[] args) {
        System.out.println(AuthLevel.BANNED.compareTo(AuthLevel.ANY));
        System.out.println(AuthLevel.ANY.compareTo(AuthLevel.OWNER));
        System.out.println(AuthLevel.ADMIN.compareTo(AuthLevel.USER));
    }
}