package cogbog.discord.persistence;

import cogbog.discord.exception.DataMappingException;
import cogbog.discord.model.Recording;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class RecordingMappingFactoryTest {

    private static final String KEY = "key";
    private static final String PREFIX = "prefix";
    private static final String RECORDING_ID = "recording_id";
    private static final String RECORDING_URI = "http://recording.uri.com";
    private static final String TABLE = "table";

    private Recording recording;
    private RecordingMappingFactory factory;

    @Before
    public void setup() throws MalformedURLException {
        recording = Recording.builder()
                .recordingId(RECORDING_ID)
                .key(KEY)
                .prefix(PREFIX)
                .recordingUri(new URL(RECORDING_URI))
                .build();
        factory = new RecordingMappingFactory(TABLE);
    }

    @Test
    public void toMapHappyPath() {
        var result = factory.toMap(recording);
        assertEquals(KEY, result.get(RecordingMappingFactory.KEY));
        assertEquals(PREFIX, result.get(RecordingMappingFactory.PREFIX));
        assertEquals(RECORDING_ID, result.get(RecordingMappingFactory.RECORDING_ID));
        assertEquals(RECORDING_URI, result.get(RecordingMappingFactory.RECORDING_URI));
    }

    @Test
    public void fromMapHappyPath() throws DataMappingException {
        var map = Map.of(
                RecordingMappingFactory.KEY, KEY,
                RecordingMappingFactory.PREFIX, PREFIX,
                RecordingMappingFactory.RECORDING_URI, RECORDING_URI,
                RecordingMappingFactory.RECORDING_ID, RECORDING_ID
        );
        assertEquals(recording, factory.fromMap(map));
    }

}