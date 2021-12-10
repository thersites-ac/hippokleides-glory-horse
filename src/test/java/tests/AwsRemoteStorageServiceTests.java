package tests;

import net.picklepark.discord.service.impl.AwsRemoteStorageService;
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
