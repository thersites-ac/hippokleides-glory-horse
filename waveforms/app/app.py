import boto3
import waveform

# TODO: fetch the recording ID for the object and write to Dynamo
def lambda_handler(event, context):
    recordings = boto3.resource('s3').Bucket('discord-recordings')
    
    for record in event['Records']:
        key = record['s3']['object']['key']
        local_audio = '/tmp/audio.wav'
        recordings.download_file(key, local_audio)
        print('downloaded', key, 'to', local_audio)
        local_waveform = '/tmp/waveform.png'
        waveform.prepare_plot(local_audio, local_waveform)
        print('generated waveform at', local_waveform)
        remote_waveform = remote_waveform_key(key)
        recordings.upload_file(local_waveform, remote_waveform)
        print('uploaded', local_waveform, 'with key', remote_waveform)

def remote_waveform_key(key):
    parts = key.split('/')
    prefix = parts[0]
    name = parts[1].split('.')[0]
    return prefix + '/waveforms/' + name + '.png'
