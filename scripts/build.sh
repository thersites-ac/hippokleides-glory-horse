./gradlew test
if [ $? == 0 ]
then
    aws codebuild start-build --project-name Hippokleides --region us-east-2
else
    echo "Local tests failed; build skipped"
fi
