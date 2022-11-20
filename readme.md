# CME

![Coverage](.github/badges/jacoco.svg)

## Overview

The quickest way to get up and running is to use maven wrapper to run the
application. The following will bind the API port to `:8080` and resp. the
actuator to `:4000`.

```bash
> ./mvnw spring-boot:run
```

API requests can now be submitted, e.g.

```bash
> curl \
>   -X POST \
>   -H "Content-Type: application/json" \
>   -d '{"username": "gbarnett", "text": "kayak"}' \ 
>   http://localhost:8080/api/v1/palindrome
```

_Note._ You can visit `http://localhost:8080/api-docs` to get an Open API 3.0
description of the API.

### Cache Persistence

You can submit a number of API requests, interrupt and restart the server
and the in-memory cache will hydrate from the persistence store (RocksDB). For
example, after issuing the following on a fresh system:

```bash
> curl -X POST -H "Content-Type: application/json" -d '{"username": 
"gbarnett", "text": "data"}'  http://localhost:8080/api/v1/palindrome
> curl -X POST -H "Content-Type: application/json" -d '{"username": 
"gbarnett", "text": "kayak"}'  http://localhost:8080/api/v1/palindrome
> curl -X POST -H "Content-Type: application/json" -d '{"username": 
"gbarnett", "text": "mum"}'  http://localhost:8080/api/v1/palindrome
> curl -X POST -H "Content-Type: application/json" -d '{"username": 
"gbarnett", "text": "o!n!o"}'  http://localhost:8080/api/v1/palindrome
> curl -X POST -H "Content-Type: application/json" -d '{"username": 
"gbarnett", "text": "example"}'  http://localhost:8080/api/v1/palindrome
```

If you interrupt and then restart the service you will observe the following
log messages:

```
2022-11-18 09:08:02.444  INFO 3875 --- [main] com.zynchronized.cme.CmeApplication      : Started CmeApplication in 1.449 seconds (JVM running for 1.566)
2022-11-18 09:08:02.452  INFO 3875 --- [main] c.z.c.cache.ehcache.EhcacheInitialiser   : Loaded 5 persisted cache entries
```

Submitting a request for one of previously submitted inputs yields, e.g. `o!n!o`
, the following log messages (I have left these on for your benefit).

```
2022-11-18 09:09:29.980 TRACE 3875 --- [nio-8080-exec-1] o.s.cache.interceptor.CacheInterceptor   : Cache entry for key 'o!n!o' found in cache 'palindrome'
```

### Metrics

Prometheus metrics are provided to make diagnosing issues with the service as
simple as possible, e.g. one can use the below to construct Prometheus/Grafana
alerts on things like memory usage, cache evictions, etc. I have put this in to
make it clear that observability is important for diagnosing support enquiries.
This mechanism can easily be extended to provide more custom information.

```bash
> curl -X GET http://localhost:4000/metrics
# HELP executor_completed_tasks_total The approximate total number of tasks that have completed execution
# TYPE executor_completed_tasks_total counter
executor_completed_tasks_total{name="applicationTaskExecutor",} 0.0
# HELP cache_removals Cache removals
# TYPE cache_removals gauge
cache_removals{cache="palindrome",cache_manager="cacheManager",name="palindrome",} 0.0
# HELP jvm_memory_max_bytes The maximum amount of memory in bytes that can be used for memory management
# TYPE jvm_memory_max_bytes gauge
jvm_memory_max_bytes{area="heap",id="G1 Survivor Space",} -1.0
...
```

### Docker

There is a Docker image published
by [this](https://github.com/g15ecb/cme/actions)
Github Action on push to `main`. Note that I am overwriting `latest` to not spam
my repository. (The latter is not something I would do for something serious.)

```bash
> # make a directory to store the database file should one not exist; this
> # directory will be mounted to the container so we can persist state beyond
> # the container lifetime
> mkdir /tmp/dockerdb
> docker run \
>   -p 8080:8080 \
>   -p 4000:4000 \
>   -e ROCKSDB_DATABASEFILE=/cme/db/palindrome.db \
>   --mount type=bind,source=/tmp/cme,target=/cme/db \
>   granvillebarnett/cme:latest
```

You can now interact with the service, interrupt it and then use the same
command to have the new container load the persisted entries from previous
session.

## Design

## Performance

I use [k6](https://k6.io) to perform a few basic performance tests.

The server is run as follows:

```
BATCHEDREPOSITORYWRITER_BACKOFF=200 \
BATCHEDREPOSITORYWRITER_BATCHSIZE=10000 \
LOGGING_LEVEL_ORG_SPRINGFRAMEWORK_CACHE=WARN \
java -jar -Xms1G -Xmx2G target/cme-0.0.1-SNAPSHOT.jar
```

And:

```bash
> java -version 
openjdk version "17.0.5" 2022-10-18 LTS
OpenJDK Runtime Environment Corretto-17.0.5.8.1 (build 17.0.5+8-LTS)
OpenJDK 64-Bit Server VM Corretto-17.0.5.8.1 (build 17.0.5+8-LTS, mixed mode, sharing)
```

Tests on a Mac Mini M1, 8GB memory, OSX Ventura 13.0.1, 250GB SSD. Memory usage
is eyeballed by using [VisualVM](https://visualvm.github.io).

### Cache Hit

```bash
> # seed the cache with a palindrome that is repeatedly hit
> curl -X POST -H "Content-Type: application/json" -d '{"username": "gbarnett", "text": "abba"}'  http://localhost:8080/api/v1/palindrome
> # 100 concurrent users repeatedly hitting the same API with the above request
> # for a duration of 100s
> k6 run -u 100 --duration 100s cache-hit.js 
```

_Results._

```
k6 run -u 100 --duration 100s cache-hit.js

          /\      |‾‾| /‾‾/   /‾‾/
     /\  /  \     |  |/  /   /  /
    /  \/    \    |     (   /   ‾‾\
   /          \   |  |\  \ |  (‾)  |
  / __________ \  |__| \__\ \_____/ .io

  execution: local
     script: cache-hit.js
     output: -

  scenarios: (100.00%) 1 scenario, 100 max VUs, 2m10s max duration (incl. graceful stop):
           * default: 100 looping VUs for 1m40s (gracefulStop: 30s)


running (1m40.0s), 000/100 VUs, 3131399 complete and 0 interrupted iterations
default ✓ [======================================] 100 VUs  1m40s

     ✓ status is 200

     checks.........................: 100.00% ✓ 3131399      ✗ 0
     data_received..................: 452 MB  4.5 MB/s
     data_sent......................: 586 MB  5.9 MB/s
     http_req_blocked...............: avg=20.91µs min=0s        med=1µs    max=135.45ms p(90)=2µs    p(95)=2µs
     http_req_connecting............: avg=15.34µs min=0s        med=0s     max=135.4ms  p(90)=0s     p(95)=0s
     http_req_duration..............: avg=2.79ms  min=40µs      med=1.74ms max=1.03s    p(90)=5ms    p(95)=7.91ms
       { expected_response:true }...: avg=2.79ms  min=40µs      med=1.74ms max=1.03s    p(90)=5ms    p(95)=7.91ms
     http_req_failed................: 0.00%   ✓ 0            ✗ 3131399
     http_req_receiving.............: avg=65.01µs min=-667000ns med=6µs    max=258.69ms p(90)=23µs   p(95)=57µs
     http_req_sending...............: avg=24.41µs min=-946000ns med=3µs    max=145.56ms p(90)=8µs    p(95)=13µs
     http_req_tls_handshaking.......: avg=0s      min=0s        med=0s     max=0s       p(90)=0s     p(95)=0s
     http_req_waiting...............: avg=2.7ms   min=34µs      med=1.7ms  max=1.03s    p(90)=4.87ms p(95)=7.66ms
     http_reqs......................: 3131399 31313.861613/s
     iteration_duration.............: avg=3.16ms  min=56.25µs   med=1.89ms max=1.03s    p(90)=5.64ms p(95)=9.23ms
     iterations.....................: 3131399 31313.861613/s
     vus............................: 100     min=100        max=100
     vus_max........................: 100     min=100        max=100
```

Peak heap usage is ~669MB.

### Cache Misses

```bash
> # 100 concurrent users repeatedly hitting the API with random strings up to 150
> # chars in length for a duration of 100s
> k6 run -u 100 --duration 100s cache-miss.js 
```

_Results._

```

          /\      |‾‾| /‾‾/   /‾‾/
     /\  /  \     |  |/  /   /  /
    /  \/    \    |     (   /   ‾‾\
   /          \   |  |\  \ |  (‾)  |
  / __________ \  |__| \__\ \_____/ .io

  execution: local
     script: cache-miss.js
     output: -

  scenarios: (100.00%) 1 scenario, 100 max VUs, 2m10s max duration (incl. graceful stop):
           * default: 100 looping VUs for 1m40s (gracefulStop: 30s)


running (1m40.1s), 000/100 VUs, 1644377 complete and 0 interrupted iterations
default ✓ [======================================] 100 VUs  1m40s

     ✓ status is 200

     checks.........................: 100.00% ✓ 1644377      ✗ 0
     data_received..................: 239 MB  2.4 MB/s
     data_sent......................: 549 MB  5.5 MB/s
     http_req_blocked...............: avg=45.97µs  min=0s       med=1µs    max=291.17ms p(90)=2µs     p(95)=3µs
     http_req_connecting............: avg=41.6µs   min=0s       med=0s     max=184.65ms p(90)=0s      p(95)=0s
     http_req_duration..............: avg=5.12ms   min=52µs     med=2.75ms max=881.53ms p(90)=11.1ms  p(95)=17.91ms
       { expected_response:true }...: avg=5.12ms   min=52µs     med=2.75ms max=881.53ms p(90)=11.1ms  p(95)=17.91ms
     http_req_failed................: 0.00%   ✓ 0            ✗ 1644377
     http_req_receiving.............: avg=118.07µs min=3µs      med=7µs    max=452.48ms p(90)=33µs    p(95)=85µs
     http_req_sending...............: avg=54.6µs   min=-35000ns med=3µs    max=165.52ms p(90)=10µs    p(95)=17µs
     http_req_tls_handshaking.......: avg=0s       min=0s       med=0s     max=0s       p(90)=0s      p(95)=0s
     http_req_waiting...............: avg=4.95ms   min=44µs     med=2.68ms max=881.48ms p(90)=10.71ms p(95)=17.31ms
     http_reqs......................: 1644377 16434.423907/s
     iteration_duration.............: avg=6.05ms   min=135.29µs med=3.16ms max=887.19ms p(90)=13.31ms p(95)=21.66ms
     iterations.....................: 1644377 16434.423907/s
     vus............................: 100     min=100        max=100
     vus_max........................: 100     min=100        max=100
```

Max heap usage again is ~670MB. Note though that the heap usage peaks and dips
due to internal data structures being allocated into a queue, references to this
data structure are eliminated upon being persisted. That's one of the reasons
the poll time and batch size are low and resp. high to flush them out from being
referenced as quick as possible.

Upon restart of the service after the above note:

```
2022-11-20 16:02:28.861  INFO 3574 --- [           main] c.z.c.cache.ehcache.EhcacheInitialiser   : Loaded 1644377 persisted cache entries
2022-11-20 16:02:28.862  INFO 3574 --- [           main] c.zynchronized.cme.cache.CacheBootstrap  : Took(ms): 4270
```

The `get` path is not optimised (unlike the write path): you can see that the
reads of 1,664,377 entries from the persistence store takes ~4s. The heap cache
is not defined for this quantity so a lot of these immediately spill into the
off-heap memory.