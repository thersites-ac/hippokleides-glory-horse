# Pathfinder Bot
Hobby bot with miscellaneous functionality. Sit and spin.

# To Do
* important:
    * rename
    * modularize (yes, going back to that pattern)
    * sort out bugs related to clip ID/title collision
    * error handling in the Python Lambda
* other
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
    * handle error when user tries to delete a nonexistent clip
    * `DeleteClipCommand` has an unidentified bug (think this was AWS permissions)
    * update terraform/other infra files
    * functional testing (set up another bot for this)
    * more tags for randomized selections
    * random youtube playback (search and play back audio from first result) - Eli

# Feature requests
    * hall of fame clips to avoid accidental deletion
    * track speaker of clips in order to create `~impersonate` command
