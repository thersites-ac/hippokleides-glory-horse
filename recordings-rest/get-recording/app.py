import json
import boto3


HEADERS = {
    'Access-Control-Allow-Origin': '*',
    'Access-Control-Allow-Methods': 'OPTIONS, GET',
    'Access-Control-Allow-Headers': '*'
}
PREFLIGHT = {
    'statusCode': 200,
    'headers': HEADERS
}

def lambda_handler(event, context):
    if event['httpMethod'] == 'GET':
        return get(event)
    elif event['httpMethod'] == 'OPTIONS':
        return PREFLIGHT

def get(event):
    dynamo = boto3.client('dynamodb', region_name = 'us-east-2')
    key = event['pathParameters']['key']
    keyd = { 'recording_id': { 'S': key } }
    print('fetching', keyd)
    try:
        resp = dynamo.get_item(TableName = 'hippokleides_recordings', Key = keyd)
        body = resp['Item']

        return {
            'statusCode': 200,
            'body': json.dumps({
                'recordingId': body['recording_id']['S'],
                'recordingUri': body['recording_uri']['S'],
                'waveformUri': body['waveform_uri']['S'],
                'prefix': body['prefix']['S'],
                'key': body['key']['S']
            }),
            'headers': HEADERS
        }
    # TODO: actually inspect the exception and return 404 only in case of ResourceNotFound
    except:
        return {
            'statusCode': 404,
            'body': json.dumps('Not found'),
            'headers': HEADERS
        }
