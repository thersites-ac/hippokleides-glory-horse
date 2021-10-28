# Pathfinder Bot
This project began as experimentation with the [lavaplayer](https://github.com/sedmelluq/lavaplayer/tree/master/demo-jda)
demo for adding audio playback to a Discord bot. Over time, I've been adding other features as well.

# Tests to add, in order:
* test that we try to sync local filesystem at startup

# To Do
* important:
    * reduce TTL on audio URL
    * shorten saved audio window
    * see the various fixmes
    * hall of fame clips to avoid accidental deletion
    * delete authorization
    * `help` command improvement:
        * annotate each command with its help message
* other
    * remove the awkward error handling in the commands
    * add some degree of fuzzy id matching or in-text search
    * authorization annotations
    * Lambda should assign a new id to each generated clip to avoid collision
    * error handling in the Python Lambda
    * add CORS headers to API Gateway error responses
    * add loading/processing/error screens to UI
    * inform channel members after the clip has been loaded
    * group clips by channel in the bucket
    * make commands async
    * handle error when user tries to delete a nonexistent clip
    * handle invalid command names (in UI, lambda, and bot)
    * the command title is an injection attack risk; fix that