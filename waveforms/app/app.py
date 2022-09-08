import boto3
import waveform

def lambda_handler(event, context):
    recordings = boto3.resource('s3').Bucket('discord-recordings')
    config = boto3.session.Config(region_name = 'us-east-1', signature_version = 's3v4')
    s3 = boto3.client('s3', config = config)
    dynamo = boto3.client('dynamodb', region_name = 'us-east-2')
    
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
        signature_params = { 'Bucket': 'discord-recordings', 'Key': remote_waveform }
        signed_waveform_url = s3.generate_presigned_url(
                'get_object', 
                Params = signature_params, 
                ExpiresIn = 600)
        print('generated signed URL for waveform', signed_waveform_url)
        tagging_response = s3.get_object_tagging(Key = key, Bucket = 'discord-recordings')
        tags = transform(tagging_response)
        recording_id = tags['recording_id'] 
        print('looked up recording_id', recording_id)
        keyd = { 'recording_id': { 'S': recording_id} }
        updates = { 'waveform_uri': { 'Value': { 'S': signed_waveform_url }, 'Action': 'PUT' } }
        dynamo.update_item(TableName = 'hippokleides_recordings', Key = keyd, AttributeUpdates = updates)
        print('added signed URL to dynamo record', recording_id)


def remote_waveform_key(key):
    parts = key.split('/')
    prefix = parts[0]
    name = parts[1].split('.')[0]
    return prefix + '/waveforms/' + name + '.png'

# ought to be a library function
def transform(tagging_response):
    tags = tagging_response.get('TagSet')
    result = {}
    for pair in tags:
        result[pair['Key']] = pair['Value']
    return result
