#! /bin/bash

# TODO: add general system updates to the startup script a la `sudo yum update`

log () {
    echo "`date`: $1" >> startup.log
}

export ENV=`curl 169.254.169.254/latest/meta-data/tags/instance/env`
log "operating in $ENV"

LOCAL_CONFIG_LOCATION=cloudwatch-agent-config.json
LOCAL_ARTIFACT_LOCATION=app.jar
if [ "$ENV" = "prod" ]
then
    REMOTE_CONFIG_LOCATION=s3://hippokleides-cicd/validated/configs/cloudwatch-agent-config.json
    REMOTE_ARTIFACT_LOCATION=s3://hippokleides-cicd/validated/artifacts/hippokleides-glory-horse-uber.jar
    TOKEN_SECRET=discord_bot_token_prod
    BITLY_SECRET=bitly_secret_prod
else
    REMOTE_CONFIG_LOCATION=s3://hippokleides-cicd/configs/cloudwatch-agent-config.json
    REMOTE_ARTIFACT_LOCATION=s3://hippokleides-cicd/artifacts/build/libs/hippokleides-glory-horse-uber.jar
    TOKEN_SECRET=token
    BITLY_SECRET=bitly_secret
fi

aws s3 cp $REMOTE_CONFIG_LOCATION $LOCAL_CONFIG_LOCATION
log "downloaded cloudwatch agent config"
sudo yum install amazon-cloudwatch-agent
wget https://s3.amazonaws.com/amazoncloudwatch-agent/amazon_linux/amd64/latest/amazon-cloudwatch-agent.rpm
sudo rpm -U ./amazon-cloudwatch-agent.rpm
sudo mkdir -p /usr/share/collectd
sudo touch /usr/share/collectd/types.db
log "installed cloudwatch agent"
sudo /opt/aws/amazon-cloudwatch-agent/bin/amazon-cloudwatch-agent-ctl -a fetch-config -m ec2 -s -c file:$LOCAL_CONFIG_LOCATION
log "launched cloudwatch agent"

sudo amazon-linux-extras install java-openjdk11 -y
log "installed java11"

aws s3 cp $REMOTE_ARTIFACT_LOCATION $LOCAL_ARTIFACT_LOCATION
log "downloaded jar"
mkdir clips
mkdir recordings
log "made directories"
export TOKEN=`aws secretsmanager get-secret-value --secret-id $TOKEN_SECRET --region us-east-2 --query SecretString --output text`
export BITLY_TOKEN=`aws secretsmanager get-secret-value --secret-id $BITLY_SECRET --region us-east-2 --query SecretString --output text`
log "read secrets"
(java -jar -Dtoken=$TOKEN -Dshortener.auth.token=$BITLY_TOKEN -Denv=$ENV $LOCAL_ARTIFACT_LOCATION) &
log "started app"
