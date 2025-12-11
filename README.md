# e-10b-term-project
The OldSchool RuneScape Experience Tracker in Java

## Running the Tracker

In order to run the application, run the following commands:

```bash
# At the root of the repository with all the java source files
javac ExperienceTracker.java
java ExperienceTracker
```

## Loading Saved Players

The GUI allows for loading saved players from `.osrs` files.
Samples of these files have been provided in the `./saved_players/` directory.
The files are separated into days which represent when the player was created.
This should allow for viewing historical data for a variety of players without
having to wait.