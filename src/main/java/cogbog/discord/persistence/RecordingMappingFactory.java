package cogbog.discord.persistence;

import cogbog.discord.exception.DataMappingException;
import cogbog.discord.model.Recording;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class RecordingMappingFactory implements MappingFactory<Recording> {

    public static final String KEY = "key";
    public static final String PREFIX = "prefix";
    public static final String RECORDING_ID = "recording_id";
    public static final String RECORDING_URI = "recording_uri";
    public static final String WAVEFORM_URI = "waveform_uri";

    private final String table;

    public RecordingMappingFactory(String table) {
        this.table = table;
    }

    @Override
    public Map<String, String> toMap(Recording object) {
        Map<String, String> map = new HashMap<>(Map.of(
                KEY, object.getKey(),
                PREFIX, object.getPrefix(),
                RECORDING_ID, object.getRecordingId()
        ));
        var recordingUri = object.getRecordingUri();
        var waveformUri = object.getWaveformUri();
        if (recordingUri != null) map.put(RECORDING_URI, recordingUri.toString());
        if (waveformUri != null) map.put(WAVEFORM_URI, waveformUri.toString());
        return map;
    }

    @Override
    public Recording fromMap(Map<String, String> map) throws DataMappingException {
        try {
            var builder = Recording.builder()
                    .key(map.get(KEY))
                    .prefix(map.get(PREFIX))
                    .recordingId(map.get(RECORDING_ID));
            var recordingUri = map.get(RECORDING_URI);
            var waveformUri = map.get(WAVEFORM_URI);
            if (recordingUri != null) builder.recordingUri(new URL(recordingUri));
            if (waveformUri != null) builder.waveformUri(new URL(waveformUri));
            return builder.build();
        } catch (MalformedURLException e) {
            throw new DataMappingException(map, e);
        }
    }

    @Override
    public String getTable() {
        return table;
    }
}