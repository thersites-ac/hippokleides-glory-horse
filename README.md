# Pathfinder Bot
This project began as experimentation with the [lavaplayer](https://github.com/sedmelluq/lavaplayer/tree/master/demo-jda)
demo for adding audio playback to a Discord bot. Over time, I've been adding other features as well.

# To Do
* important:
    * hall of fame clips to avoid accidental deletion
    * delete authorization
* other
    * remove the awkward error handling in the commands
    * add some degree of fuzzy id matching or in-text search
    * authorization annotations
    * Lambda should assign a new id to each generated clip to avoid collision
    * error handling in the Python Lambda
    * add CORS headers to API Gateway error responses
    * inform channel members after the clip has been loaded
    * group clips by channel in the bucket
    * handle error when user tries to delete a nonexistent clip
    * handle invalid command names (in UI, lambda, and bot)
    * the command title is an injection attack risk; fix that
    * bug: sync doesn't respect deletion/name changes
    * user nickname collision when recording/clipping
    * audio backtrack
    * audio multichannel (to layer clips over playlist)
    * fix the volume controls