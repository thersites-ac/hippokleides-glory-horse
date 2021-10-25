package net.picklepark.discord.service.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class AwsStorageServiceTests {

    @Test
    public void canCreate() {
        new AwsStorageService(null, null, null, null, null);
    }
}
