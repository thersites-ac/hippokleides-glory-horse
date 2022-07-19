import json
import boto3
import os
from trimmer import Trimmer
from urllib.parse import urlencode

class NonUniqueKeyError(Exception):
    pass

def lambda_handler(event, context):
    if event['httpMethod'] == 'OPTIONS':
        print('options call')
        return {
            'statusCode': 200,
            'headers': {
                'Access-Control-Allow-Origin': '*',
                'Access-Control-Allow-Methods': 'OPTIONS, POST',
                'Access-Control-Allow-Headers': '*'
            }
        }

    body = json.loads(event['body'])

    print('invoked with payload:', body)

    key = body['key']
    prefix = body['prefix']
    start = body['start']
    end = body['end']
    title = body['title']

    try:
        trimmed_key = trim_and_upload(key, prefix, start, end, title)

        return {
            'statusCode': 200,
            'body': json.dumps({
                'key': trimmed_key,
                'title': title
            }),
            'headers': {
                'Access-Control-Allow-Origin': '*',
                'Access-Control-Allow-Methods': 'OPTIONS, POST',
                'Access-Control-Allow-Headers': '*'
            }
        }
    except NonUniqueKeyError:
        return {
            'statusCode': 400,
            'body': 'The title ' + title + ' is already taken.',
            'headers': {
                'Access-Control-Allow-Origin': '*',
                'Access-Control-Allow-Methods': 'OPTIONS, POST',
                'Access-Control-Allow-Headers': '*'
            }
        }

def trim_and_upload(key, prefix, start, end, title):
    client = boto3.client('s3')
    s3 = boto3.resource('s3')
    recordings = s3.Bucket('discord-recordings')
    output = s3.Bucket('discord-output')
    
    dest = '/tmp/' + key

    full_key = prefix + '/' + key
    recordings.download_file(full_key, dest)
    tagging_response = client.get_object_tagging(Key = full_key, Bucket = 'discord-recordings')
    tags = transform(tagging_response)
    tags['title'] = title

    skip_ms = int(start * 1000)
    copy_ms = int((end - start) * 1000)

    trimmer = Trimmer(dest, title)
    trimmer.skip(skip_ms)
    trimmer.copy(copy_ms)
    result = trimmer.finish()

    trimmed_key = prefix + '/' + os.path.basename(result)

    s3_collision = list(output.objects.filter(Prefix = trimmed_key))
    if len(s3_collision) != 0:
        raise NonUniqueKeyError(trimmed_key)

    output.upload_file(result, trimmed_key, ExtraArgs = { 'Tagging': urlencode(tags) })

    print('successfully uploaded as', trimmed_key, 'with title', title)
    return trimmed_key

def transform(tagging_response):
    tags = tagging_response.get('TagSet')
    result = {}
    for pair in tags:
        result[pair['Key']] = pair['Value']
    return result
