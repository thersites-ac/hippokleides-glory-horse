import json
import boto3
import os
from trimmer import Trimmer

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

    dest = '/tmp/' + key

    s3 = boto3.client('s3')
    s3.download_file('discord-recordings', prefix + '/' + key, dest)

    skip_ms = int(start * 1000)
    copy_ms = int((end - start) * 1000)

    trimmer = Trimmer(dest)
    trimmer.skip(skip_ms)
    trimmer.copy(copy_ms)
    result = trimmer.close()

    trimmed_key = prefix + '/' + os.path.basename(result)

    s3.upload_file(result, 'discord-output', trimmed_key, 
            ExtraArgs = { 'Tagging': 'title=' + title })

    print('successfully uploaded as', trimmed_key, 'with title', title)

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
