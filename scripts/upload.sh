KEY=$1
TITLE=$2
GUILD=$3

aws s3 cp $KEY s3://discord-output/$GUILD/$KEY

aws s3api put-object-tagging --key "$GUILD/$KEY" --bucket discord-output --tagging "{\"TagSet\": [{\"Key\": \"title\",\"Value\": \"${TITLE}\"}]}"
