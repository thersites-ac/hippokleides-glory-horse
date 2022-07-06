# important (blockers to release):
  * any unsanitized user input risks?
  * sort out bugs related to clip ID/title collision
    * handle invalid clip names (in UI, lambda, and bot)
    * when I create multiple clips off a single recording, they should all be valid
    * when I create two clips with the same name, I should get an error the second time around and be able to try again
  * error handling in the Python Lambda
  * trimmer:
    * ui is ugly
    * security
  * qa/prod environments
    * prod bitly token?
  * initial command sets for launch
  * create a new discord app
  * payment detection/feature toggle
  * help lines are getting out of date

# other (post-release maintenance)
  * the various classes that fetch stored config have turned into a mess
  * combine repos
  * rename
  * modularize (yes, going back to that pattern)
  * add CORS headers to API Gateway error responses
  * inform channel members after the clip has been loaded (feature/chatty-polling)
  * functional testing (set up another bot for this)
  * better work/todo tracking
  * is the trimmer lambda worth it? why not make that an endpoint on the same VM as hippo?
  * make injection names constants in the Names.java file
  * the clip polling worker still seems broken
  * reorganize clip buckets
  * perf testing, more scaling consideration
  * resilience

# Feature requests
  * hall of fame clips to avoid accidental deletion
  * track speaker of clips in order to create `~impersonate` command
  * feature request intake endpoint
  * image search by keyword (from deviantart, wikihow, google images, etc.)
  * random youtube playback (search and play back audio from first result)
  * more tags for randomized selections
  * share clips between channels
  * record only individual users, or allow users to opt out of recording
  * it would be nice if `ClipManager::sync` were atomic, to roll back the deletion in the case of a download error
  * opt-in recording: has `AuthLevel.USER`, but requires people in the voice chat to respond to a bot message to be recorded
  * multichannel welcome over regular clip playback
