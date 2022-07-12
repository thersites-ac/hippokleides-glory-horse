# blockers to beta:
  * turn off non-playback commands in standard hippo

# blockers to full release
  * payment detection/feature toggle
  * help lines are getting out of date

# other
  * combine repos
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
  * the S3 notifications should happen after the new clip is tagged, not after the clip is created
  * process to move artifacts to validated/artifacts
  * track (and log) running bot version
  * automate promotion of good artifacts
  * tag builds with version
  * combine secrets in secretsmanager (to save $0.80/mo...)
  * once clip title and key are the same, remove the tag
  * is the lambda endpoint worth it? why not just run a server off of Hippo?
  * Discord OAuth?
  * distinct prod bitly access key?
  * security review for UI/lambda
    * any unsanitized user input risks?
  * handle invalid clip names (in UI, lambda, and bot)
  * error handling in the Python Lambda
    * 403s from S3
    * 404s from S3
  * trimmer:
    * ui is ugly
    * not showing 5xx errors?
  * add some description, tags to the Hippo's profile(s): https://discord.com/developers/applications/996373324072493056/information

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
  * join command: get link to join hippo to another channel
