How to run:
-
- Run mvn package
- Run java -jar target/mojiraFeedIRC-1.0-SNAPSHOT-jar-with-dependencies.jar 

How to use:
-
- Connect using a IRC client to localhost:6667 using the same username as in MOJIRA
- Register with /msg FeedServer REGISTER password
- Login with /msg FeedServer LOGIN password
- Join the #feed channel 


Commands:
-
- /msg FeedServer IGNORE ignore regex - adds "regex" to your ignore list, it must be a full match for it to be ignored

   
