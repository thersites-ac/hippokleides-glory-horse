package net.picklepark.discord.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class AwsRemoteStorageServiceTests {

    @Test
    public void canCreate() {
        new AwsRemoteStorageService(null, null, null, null, null, null, null, null);
    }
}
