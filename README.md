# Pathfinder Bot
Hobby bot with miscellaneous functionality. Sit and spin.

# To Do
* important:
    * hall of fame clips to avoid accidental deletion
* other
    * track speaker of clips in order to create `~impersonate` command
    * add some degree of fuzzy id matching or in-text search
    * Lambda should assign a new id to each generated clip to avoid collision
    * error handling in the Python Lambda
    * add CORS headers to API Gateway error responses
    * inform channel members after the clip has been loaded
    * group clips by channel in the bucket
    * handle invalid command names (in UI, lambda, and bot)
    * any unsanitized user input risks?
    * audio backtrack
    * audio multichannel (to layer clips over playlist)
    * it would be nice if `ClipManager::sync` were atomic, to roll back the deletion in the case of a download error
    * feature intake endpoint
    * image search by keyword (from deviantart, wikihow, google images, etc.)
    * better auth groups
    * modularize
    * handle error when user tries to delete a nonexistent clip
    * `DeleteClipCommand` has an unidentified bug (think this was AWS permissions)
    * update terraform/other infra files
    * functional testing (set up another bot for this)
    * more tags for randomized selections
    * random youtube playback (search and play back audio from first result) - Eli