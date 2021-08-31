package net.picklepark.discord.embed.service;

import net.picklepark.discord.exception.NotRecordingException;
import net.picklepark.discord.service.impl.LocalRecordingService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class LocalRecordingServiceTests {

    private static final int packetsPerSec = 50;
    private static final int secToProvide = 70;
    private static final int maxSecToStore = 60;
    private static final int totalPacketsProvided = packetsPerSec * secToProvide;
    private static final int maxPacketsStored = packetsPerSec * maxSecToStore;

    private LocalRecordingService localRecordingService;

    @Before
    public void setup() {
        localRecordingService = new LocalRecordingService();
    }

    @Test(expected = NotRecordingException.class)
    public void mustPrepareToRecord() throws NotRecordingException {
        Assert.fail();
//        whenReceiveOnePacket();
    }

    @Test(expected = NotRecordingException.class)
    public void mustPrepareToFetch() throws NotRecordingException {
        Assert.fail();
//        whenFetchRecording();
    }

    @Test
    public void storesAudio() throws NotRecordingException {
        givenRecordingStarted();
        Assert.fail();
//        whenReceiveOnePacket();
//        thenSavedStreamMatchesInput();
    }

    @Test
    public void storesAtMostOneMinAudioPerUser() throws NotRecordingException {
        givenRecordingStarted();
        Assert.fail();
//        whenProvideExcessAudio();
//        thenOnlyLastMinuteSaved();
    }

    @Test
    public void lastInFirstOut() throws NotRecordingException {
        givenRecordingStarted();
        Assert.fail();
//        whenReceiveData((short) 1);
//        whenReceiveData((short) 2);
//        thenResultOrderIs((short) 1, (short) 2);
    }

    private void givenRecordingStarted() {
        localRecordingService.beginRecording();
    }

}
