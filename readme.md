# CME

To get up and running quickly I suggest using the Docker image:

```bash
> docker run -p 8080:8080 -d granvillebarnett/cme:latest
```

_Note._ the Docker image is published by a Github on push to `main`.

Or,

```bash
> ./mvnw spring-boot:run
```

At this point you can interact with the sole API: `/api/v1/palindrome`. For example,

```bash
> curl -X POST -H "Content-Type: application/json" -d '{"username": "gbarnett", "text": "kayak"}'  http://localhost:8080/api/v1/palindrome
```

## Tests

```bash
> ./mvnw test
```

## Overview

```
                               +-----------------------------------------------------------------------------+
                               |+------------------------+                                                   |
                               ||      API Request       |                                                   |
                               |+------------------------+                                                   |
                               |             |                                                               |
                               |             v                                                               |
                               |+-------------------------+                                                  |
                               ||  PalindromeController   |                                                  |
                               |+-------------------------+                                                  |
                               |             |                                                               |
                               |             v                                                               |
                               |+-------------------------+                                                  |
                               ||    PalindromeService    |                                                  |
+------------+                 |+-------------------------+                                                  |
|(1) Startup |                 ||CachingPalindromeService |                                                  |
+------------+----------------+|+-------------------------+                                                  |
|                             ||             |                                                               |
|                             ||          writes                                                             |
|                             ||             |                                                               |
|                             ||             v                                                               |
|                             ||     +---------------+                             +------------------------+|
|            writes-----------++---->|     Cache     |                             |  RepositoryWriteQueue  ||
|             |               ||     +---------------+                             +------------------------+|
|             |               ||     |    EhCache    |-----event(CREATED)--------->|InMemoryRepositoryQueue ||
| +-----------------------+   ||     +---------------+                             +------------------------+|
| |   CacheInitialiser    |   ||                                                                ^            |
| +-----------------------+   ||                +-----------+ +------polls----------------------+            |
| |  EhcacheInitialiser   |---++------+         |I/O Thread | |                                              |
| +-----------------------+   ||      |         +-----------+---------------+                                |
|             ^               ||      |         |     RepositoryWriter      |                                |
|             |               ||      |         +---------------------------+                                |
|         Triggers            ||      |         |  BatchedRepositoryWriter  |                                |
|             |               ||      |         +---------------------------+                                |
|             |               ||      |                       |                                              |
| +------------------------+  ||   reads                      |                                              |
| |     CacheBootstrap     |  ||      |                    writes                                            |
| +------------------------+  ||      |                       |                                              |
|              ^              ||      |                       |                                              |
|              |              ||      |                       v                                              |
|           Event             ||      |           +-----------------------+                                  |
|              |              ||      +---------->| PalindromeRepository  |                                  |
| +------------------------+  ||                  +-----------------------+                                  |
| | ApplicationReadyEvent  |  ||                  |   RocksDbRepository   |                                  |
| +------------------------+  ||                  +-----------------------+                                  |
+-----------------------------++------------------+----------------------------------------------------------+
                               |  (2) Operation   |                                                           
                               +------------------+                                                           
```