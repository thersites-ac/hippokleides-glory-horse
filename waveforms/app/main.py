import sys
import waveform

def main():
    print(sys.argv[1], sys.argv[2])
    waveform.prepare_plot(sys.argv[1], sys.argv[2])

main()
