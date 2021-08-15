# Pathfinder Bot
This project began as experimentation with the [lavaplayer](https://github.com/sedmelluq/lavaplayer/tree/master/demo-jda)
demo for adding audio playback to a Discord bot. Over time, I've been adding the ability to scrape rule information from
the [legacy PRD](https://legacy.aonprd.com/) as well.

# To Do
* test with feats like `whirlwind-attack`
* add some degree of fuzzy id matching or in-text search
* different authorization for different commands
* better error messages (e.g. not found)
* consider de-lomboking the model objects
    * public fields and @ToString?