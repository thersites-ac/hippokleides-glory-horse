package net.picklepark.discord.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Auth {
    Level value();

    enum Level {
        ANY,
        ADMIN,
        OWNER
    }
}
