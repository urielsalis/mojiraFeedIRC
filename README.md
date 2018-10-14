#How to run:
-
- Download the latest release, extract and run java -jar mojiraFeedIRC-2.0.0.jar


#How to compile:
-
- Run mvn package
- Run java -jar target/mojiraFeedIRC-2.0.0-jar-with-dependencies.jar 

#How to use:
-
- Connect using a IRC client to localhost:6667 using the same username as in MOJIRA to prevent getting updates from your username
- Register with /msg FeedServer REGISTER password
- Login with /msg FeedServer LOGIN password
- Join the #feed channel if your client didnt join automatically!


#Commands:
-
- /msg FeedServer IGNORE ignore regex - adds "regex" to your ignore list, it must be a full match for it to be ignored

   
