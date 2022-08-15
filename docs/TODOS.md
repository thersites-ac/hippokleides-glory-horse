# blockers to full release
  * trimmer:
    * ui is ugly
    * the url shouldn't say "pickle park"
    * https
  * trimmer waveforms:
    * remove dead query params (this will be 0.3.0)
    * ensure waveform is ready before sending user to the trimmer
  * payment detection/feature toggle
  * rename prod: SoundboardMixerPro or something
  * SQS polling thread cannot handle clips with spaces in the name: they get replaced with + signs in the S3 notification
  * hypothetical users / design thinking

# other
  * combine repos
  * modularize (yes, going back to that pattern)
  * add CORS headers to API Gateway error responses
  * functional testing (set up another bot for this)
  * better work/todo tracking
    * script to grep for TODOs and FIXMEs
  * reorganize clip buckets
  * perf testing, more scaling consideration
  * resilience
  * the S3 notifications that Hippo receives should happen after the new clip is tagged, not after the clip is created
  * automate promotion of good artifacts
  * tag builds with version
  * combine secrets in secretsmanager (to save $0.80/mo...)
  * once clip title and key are the same, remove the tag
  * Discord OAuth for user login?
  * distinct prod bitly account?
  * security review for UI/lambda
    * any unsanitized user input risks?
  * handle invalid clip names (in UI, lambda, and bot)
  * error handling in POST /trim
    * 403s from S3
    * 404s from S3
  * trimmer:
    * not showing 5xx errors?
  * add some description, tags to the Hippo's profile(s): https://discord.com/developers/applications/996373324072493056/information
  * get off of LavaPlayer
  * tracking users, channels, metrics for Hippo
  * enable/disable features based on registered commands
  * boot is getting uglier by the day
  * joins correct voice channel
  * text voice channels break it
  * cross region replication for clips; the storage fees are trivial
  * lookup user by @ mention
    * literal string appears to have syntax <@385291954516393984> which the DSL does not recognize as a valid variable
  * IAM permissions are degenerating to "do whatever" for Lambda functions; review these
  * it's gross that stuff is spread across us-east-1 and us-east-2
  * handle warnings in waveform generator lambda
  * post-clip creation is still broken - Hippo sends "I know how to x" but doesn't load the clip
  * automated dynamo deletion for hippokleides_recordings table
    * also, convert table to an in-memory cache
  * consider alternative backend architectures
  * should dynamo records for recordings really contain presigned URLs? (this seems better when I recall it's meant to be a short-lived cache)

# Feature requests
  * hall of fame clips to avoid accidental deletion
  * track speaker of clips
    * ~impersonate command
  * feature request intake endpoint
  * optional tags for randomized selections
  * share clips between channels
  * record only individual users, or allow users to opt out of recording
  * opt-in recording: has `AuthLevel.USER`, but requires people in the voice chat to respond to a bot message to be recorded
  * multichannel welcome over regular clip playback
  * join command: get link to join hippo to another channel
