# Pathfinder Bot
This project began as experimentation with the [lavaplayer](https://github.com/sedmelluq/lavaplayer/tree/master/demo-jda)
demo for adding audio playback to a Discord bot. Over time, I've been adding the ability to scrape rule information from
the [legacy PRD](https://legacy.aonprd.com/) as well.

# To Do
* ####major refactoring all around
* add some degree of fuzzy id matching or in-text search
* different authorization for different commands
  * in general, abstract the data hardcoded in the command factory into annotations
* better error messages/handling (e.g. not found)
* Lambda should assign a new id to each generated clip to avoid collision
* error handling in the Python Lambda
* add CORS headers to API Gateway error responses
* add loading/processing/error screens to UI
* inform channel members after the clip has been loaded
* fetch cached clips outside of the SQS poller
* clip persistence if bot restarts
* clean up the local filesystem