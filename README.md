# MatchMate

A small matrimonial-style matching app built for a take-home assignment. Shows profile cards (photo, name, age, location) with Accept/Decline actions, using randomuser.me for profile data.

## Features

- Fetches profiles from randomuser.me and shows them as cards in a list.
- Accept/Decline updates the card and is saved locally.
- Works offline — cached profiles and decisions are still there with no network, and you can still accept/decline.
- Loads more profiles automatically as you scroll down.
- Pull to refresh.
- Refreshes quietly if the app regains connectivity after being offline.

## Tech

Kotlin, Jetpack Compose, Hilt, Retrofit + OkHttp, Room, Coil, Kotlin Flow/StateFlow.

Used Compose + Flow instead of RecyclerView + LiveData, and Coil instead of Glide/Picasso — the more current equivalents for a Compose-first app.

## Running it

Open in Android Studio, sync Gradle, run on a device/emulator with API 24+. No API key needed.

## Notes

- Decisions are stored in their own Room table, separate from cached profile data, so clearing/refreshing the profile cache never wipes what the user already decided.
- No automated tests yet.
- The full match list is loaded into memory from one Room query — fine for this assignment's scale, but would need proper paging for very large lists.
