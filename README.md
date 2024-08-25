# peecko-api

## Project Structure


## Development

Before you can build this project, you must install and configure the following dependencies on your machine:

1. Java JDK 17
2. Maven 3.8.1

```
./mvnw
```

REST API Document
https://github.com/japleitez/peecko-api-doc


TODO
- create a Java program that randomly selects N videos from a given category, giving more weight to recently published videos that have been chosen less frequently,

TODO
- playlist must be unique by user id and name

TODO
- playlist.name.required

TODO
- video code must be unique
- video code naming convention: code + language code
- VideoItem must be unique by video code and playlist id
- use the video name to generate a mnemonic code of 10 upper case characters in java language (chatgpt)
- check that all properties used in the program are listed in the messages.properties
- create VideoItem in database 
- apsMembership must have Long customerId because we need it to retrieve notifications;
- remove jwt from ApsUser
- think about returning HTTP STATUS 400 when user provides wrong inputs and return 
- provide a custom error message in java when returning a http status 400 (chatgpt)
- {
  "status": 400,
  "error": "Bad Request",
  "message": "Invalid email format and password must be at least 8 characters long."
  }
- complete valid chars in NameValidator