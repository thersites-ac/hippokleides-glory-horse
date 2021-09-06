import json
import boto3
import os
from trimmer import Trimmer

def lambda_handler(event, context):
    body = json.loads(event['body'])

    key = body['key']
    start = body['start']
    end = body['end']

    dest = '/tmp/' + key

    s3 = boto3.resource('s3')
    s3.meta.client.download_file('discord-recordings', key, dest)

    skip_ms = int(start * 1000)
    copy_ms = int((end - start) * 1000)

    trimmer = Trimmer(dest)
    trimmer.skip(skip_ms)
    trimmer.copy(copy_ms)
    result = trimmer.close()

    trimmed_key = os.path.basename(result)

    print(result, trimmed_key)
    s3.meta.client.upload_file(result, 'discord-output', trimmed_key)

    return {
        "statusCode": 200,
        "body": json.dumps({
            key: trimmed_key
        })
    }
