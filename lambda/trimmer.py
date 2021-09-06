import wave

class Trimmer:

    def __init__(self, filename):
        self._input = wave.open(filename, 'rb')
        self._channels, self._samplewidth, self._framerate, self._numframes, _, _ = self._input.getparams()

        self.init_output_file(filename)

        self._length = self._numframes / (self._channels * self._samplewidth * self._framerate)
        print(self._length)
        self._frames_per_ms = self._framerate / 1000
        print(self._frames_per_ms)

    def init_output_file(self, filename):
        self._output = wave.open(self.output_file_name(filename), 'wb')
        self._output.setnchannels(self._channels)
        self._output.setsampwidth(self._samplewidth)
        self._output.setframerate(self._framerate)
        self._output.setnframes(0)
        self._output.setcomptype(self._input.getcomptype(), self._input.getcompname())

    def output_file_name(self, input_name):
        parts = input_name.split('.')
        return parts[0] + '-trimmed.wav'

    def count_frames(self, ms):
        result = int(ms * self._frames_per_ms)
        return result

    def skip(self, ms):
        frames_to_skip = self.count_frames(ms)
        self._input.readframes(frames_to_skip)

    def copy(self, ms):
        frames_to_copy = self.count_frames(ms)
        data = self._input.readframes(frames_to_copy)
        self._output.writeframes(data)

    def close(self):
        self._input.close()
        self._output.close()
