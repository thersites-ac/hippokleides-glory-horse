# Hippokleides, Glory Horse

# To Do
* important:
    * rename
    * modularize (yes, going back to that pattern)
    * sort out bugs related to clip ID/title collision
      * handle invalid command names (in UI, lambda, and bot)
    * error handling in the Python Lambda
    * improve trimmer UI
    * combine repos
    * qa/prod environments
* other
    * add CORS headers to API Gateway error responses
    * inform channel members after the clip has been loaded (feature/chatty-polling)
    * any unsanitized user input risks?
    * it would be nice if `ClipManager::sync` were atomic, to roll back the deletion in the case of a download error
    * `DeleteClipCommand` has an unidentified bug (think this was AWS permissions; this todo may be moot)
    * update terraform/other infra files (do they need it?)
    * functional testing (set up another bot for this)
    * better work/todo tracking
    * fill out this readme?
    * is the trimmer lambda worth it? why not make that an endpoint on the same VM as hippo?

# Feature requests
    * hall of fame clips to avoid accidental deletion
    * track speaker of clips in order to create `~impersonate` command
    * feature request intake endpoint
    * image search by keyword (from deviantart, wikihow, google images, etc.)
    * random youtube playback (search and play back audio from first result)
    * more tags for randomized selections
    * share clips between channels
    * record only individual users, or allow users to opt out of recording
