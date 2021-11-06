sudo yum install git -y
sudo amazon-linux-extras install java-openjdk11 -y
git clone https://github.com/thersites-ac/pathfinder-bot
cd pathfinder-bot
./gradlew jar
export TOKEN=`aws secretsmanager get-secret-value --secret-id token --region us-east-2 --query SecretString --output text`
java -jar -Dtoken=${TOKEN} build/libs/bot-uber.jar
