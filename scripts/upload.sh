KEY=$1
TITLE=$2

aws s3 cp $KEY s3://discord-output

aws s3api put-object-tagging --key $KEY --bucket discord-output --tagging "{\"TagSet\": [{\"Key\": \"title\",\"Value\": \"${TITLE}\"}]}"
