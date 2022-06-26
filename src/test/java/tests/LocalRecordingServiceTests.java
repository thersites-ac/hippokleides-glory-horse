package tests;

import net.dv8tion.jda.api.audio.UserAudio;
import net.dv8tion.jda.api.entities.User;
import net.picklepark.discord.exception.InvalidAudioPacketException;
import net.picklepark.discord.exception.NotRecordingException;
import net.picklepark.discord.service.impl.RecordingServiceImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Arrays;

import static net.picklepark.discord.constants.AudioConstants.PACKET_SIZE;

@RunWith(JUnit4.class)
public class LocalRecordingServiceTests {

    private static final String GUILD = "testGuild";

    private static final int MAX_SEC_TO_STORE = 60;
    private static final int PACKET_SIZE_IN_SHORTS = PACKET_SIZE / 2;

    private RecordingServiceImpl recordingService;
    private UserAudio audio;
    private User user;
    private short[] data;
    private byte[] out;

    @Before
    public void setup() {
        recordingService = new RecordingServiceImpl(MAX_SEC_TO_STORE);
        user = User.fromId(42L);
        data = new short[PACKET_SIZE_IN_SHORTS];
        audio = new UserAudio(user, data);
    }

    @Test(expected = NotRecordingException.class)
    public void mustPrepareToRecord() throws NotRecordingException, InvalidAudioPacketException {
        whenReceiveOnePacket();
    }

    @Test(expected = NotRecordingException.class)
    public void mustPrepareToFetch() throws NotRecordingException {
        thenFetchRecording();
    }

    @Test
    public void storesAudio() throws NotRecordingException, InvalidAudioPacketException {
        givenRecordingStarted();
        whenReceiveOnePacket();
        thenSavedStreamMatchesInput();
    }

    private void thenSavedStreamMatchesInput() throws NotRecordingException {
        thenFetchRecording();
        Assert.assertArrayEquals(audio.getAudioData(1), out);
    }

    @Test
    public void lastInFirstOut() throws NotRecordingException, InvalidAudioPacketException {
        givenRecordingStarted();
        whenReceiveData((short) 1);
        whenReceiveData((short) 2);
        thenResultOrderIs((short) 1, (short) 2);
    }

    @Test(expected = NotRecordingException.class)
    public void cannotReceiveAfterStopRecording() throws NotRecordingException, InvalidAudioPacketException {
        givenRecordingStarted();
        givenRecordingStopped();
        whenReceiveOnePacket();
    }

    private void givenRecordingStopped() {
        recordingService.stopRecording(GUILD);
    }

    private void givenRecordingStarted() {
        recordingService.beginRecording(GUILD);
    }

    private void whenReceiveOnePacket() throws NotRecordingException, InvalidAudioPacketException {
        recordingService.receive(GUILD, audio);
    }

    private void whenReceiveData(short i) throws NotRecordingException, InvalidAudioPacketException {
        var copy = Arrays.copyOf(data, data.length);
        Arrays.fill(copy, i);
        audio = new UserAudio(user, copy);
        whenReceiveOnePacket();
    }

    private void thenFetchRecording() throws NotRecordingException {
        out = recordingService.getUser(GUILD, user.getIdLong());
    }

    private void thenResultOrderIs(short i, short j) throws NotRecordingException, InvalidAudioPacketException {
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
