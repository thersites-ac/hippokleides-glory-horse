#!/bin/bash
TOKEN=`aws secretsmanager get-secret-value --secret-id token --region us-east-2 --query SecretString --output text`
BITLY_TOKEN=`aws secretsmanager get-secret-value --secret-id bitly_secret --region us-east-2 --query SecretString --output text`
(java -jar -Dtoken=$TOKEN -Dshortener.auth.token=$BITLY_TOKEN /hippokleides-glory-horse-uber.jar) &

