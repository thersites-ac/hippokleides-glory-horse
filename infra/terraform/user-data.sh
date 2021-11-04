sudo yum install git -y
sudo amazon-linux-extras install java-openjdk11 -y
git clone https://github.com/thersites-ac/pathfinder-bot
cd pathfinder-bot
./gradlew jar
# todo: get the token
java -jar -Dtoken=TOKEN_GOES_HERE build/libs/bot-uber.jar