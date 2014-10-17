# HttpMultiplexer 


Fork repo of java http proxy to duplicate request to another server instead of proxy request only.
This application is useful when you have 2 versions of web application:

-- The current one is production ready and in used without problem
-- The development one is developing and you want to test it with real user data/request

Then set it up like this:

## Config
    [main]
    logging = true
    forward_ip = true
    http.protocol.handle-redirects = false
    targetUri = http://test.com:81
    secondTargetUri = http://test.com



User's request will be duplicate, one proxied to production server, wait for response and reply to client. another to developement server, but close connection immediately without waiting for response.
This's tested and work well even if one target is unavailable. 


###### Prerequisites:

* Java 1.6 or later
* Apache Common pool, Apache Http Client, jetty server, log4j
