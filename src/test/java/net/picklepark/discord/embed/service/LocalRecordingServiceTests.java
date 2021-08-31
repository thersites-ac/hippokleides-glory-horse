package net.picklepark.discord.embed.service;

import net.dv8tion.jda.api.audio.CombinedAudio;
import net.dv8tion.jda.api.entities.User;
import net.picklepark.discord.exception.NotRecordingException;
import net.picklepark.discord.service.impl.LocalRecordingService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.List;

@RunWith(JUnit4.class)
public class LocalRecordingServiceTests {

    private static final int packetsPerSec = 50;
    private static final int secToProvide = 70;
    private static final int maxSecToStore = 60;
    private static final int totalPacketsProvided = packetsPerSec * secToProvide;
    private static final int maxPacketsStored = packetsPerSec * maxSecToStore;

    private LocalRecordingService localRecordingService;
    private CombinedAudio audio;
    private List<User> users;
    private short[] data;
    private byte[] out;

    @Before
    public void setup() {
        localRecordingService = new LocalRecordingService();
        users = new ArrayList<>();
        data = new short[]{0};
        audio = new CombinedAudio(users, data);
    }

    @Test(expected = NotRecordingException.class)
    public void mustPrepareToRecord() throws NotRecordingException {
        whenReceiveOnePacket();
    }

    @Test(expected = NotRecordingException.class)
    public void mustPrepareToFetch() throws NotRecordingException {
        thenFetchRecording();
    }

    @Test
    public void storesAudio() throws NotRecordingException {
        givenRecordingStarted();
        whenReceiveOnePacket();
        thenSavedStreamMatchesInput();
    }

    private void thenSavedStreamMatchesInput() throws NotRecordingException {
        thenFetchRecording();
        Assert.assertArrayEquals(audio.getAudioData(1), out);
    }

    @Test
    public void storesAtMostOneMinAudioPerUser() throws NotRecordingException {
        givenRecordingStarted();
        whenProvideExcessAudio();
        thenOnlyLastMinuteSaved();
    }

    @Test
    public void lastInFirstOut() throws NotRecordingException {
        givenRecordingStarted();
        whenReceiveData((short) 1);
        whenReceiveData((short) 2);
        thenResultOrderIs((short) 1, (short) 2);
    }

    private void givenRecordingStarted() {
        localRecordingService.beginRecording();
    }

    private void whenReceiveOnePacket() throws NotRecordingException {
        localRecordingService.receive(audio);
    }

    private void whenReceiveData(short i) throws NotRecordingException {
        data = new short[]{i};
        audio = new CombinedAudio(users, data);
        whenReceiveOnePacket();
    }

    private void whenProvideExcessAudio() throws NotRecordingException {
        for (int i = 0; i < totalPacketsProvided; i++)
            whenReceiveOnePacket();
    }

    private void thenFetchRecording() throws NotRecordingException {
        out = localRecordingService.getCombined();
    }

    private void thenOnlyLastMinuteSaved() throws NotRecordingException {
        thenFetchRecording();
        Assert.assertEquals(maxPacketsStored * audio.getAudioData(1).length, out.length);
    }

    private void thenResultOrderIs(short i, short j) throws NotRecordingException {
        thenFetchRecording();
        whenReceiveData(i);
        byte[] first = audio.getAudioData(1);
        whenReceiveData(j);
        byte[] second = audio.getAudioData(1);
        assertOutputConcatenates(first, second);
    }

    private void assertOutputConcatenates(byte[] first, byte[] second) {
        Assert.assertEquals(first.length + second.length, out.length);
        for (int i = 0; i < out.length; i++)
            if (i < first.length)
                Assert.assertEquals(first[i], out[i]);
            else
                Assert.assertEquals(second[i - first.length], out[i]);
    }


}
