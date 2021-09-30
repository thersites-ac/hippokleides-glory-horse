package net.picklepark.discord.service;

import net.picklepark.discord.service.impl.AwsStorageService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class AwsStorageServiceTests {

    @Test
    public void canCreate() {
        new AwsStorageService();
    }
}
