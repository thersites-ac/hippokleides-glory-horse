# trimmer-lambda

Unless you're the me, this will fail:

Install the AWS SAM CLI and do
        
        sam build
        sam local invoke -e events/event.json
        
This will fire up a Docker container with the Python in it, then run the contents
of `events/event.json` through it.
