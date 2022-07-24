import wave
import numpy
import matplotlib.pyplot as plt

def prepare_plot(input_file, output_file):
    audio = wave.open(input_file)
    audio_data = audio.readframes(audio.getnframes())

    plot_data = numpy.frombuffer(audio_data, dtype=numpy.int16)
    
    plt.figure(figsize=(30, 10))
    plt.margins(0, 0)
    plt.plot(plot_data)
    plt.axis('off')


    print('saving to', output_file)
    plt.savefig(output_file, bbox_inches='tight', pad_inches=0, dpi=100)
