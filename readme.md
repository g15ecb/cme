# CME

![Coverage](.github/badges/jacoco.svg)

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

> Note. You can visit `http://localhost:8080/api-docs` to get an Open API 3.0
> description of the API.

You can submit a number of API requests, interrupt and restart the server
and the in-memory cache will hydrate from the persistence store. For example,
after issuing the following a fresh system:

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

Submitting a request for one of previous inputs yields, e.g. `o!n!o` the
following log messages (I have left these on for your benefit).

```
2022-11-18 09:09:29.980 TRACE 3875 --- [nio-8080-exec-1] o.s.cache.interceptor.CacheInterceptor   : Cache entry for key 'o!n!o' found in cache 'palindrome'
```

For Prometheus metrics:

> Note. This back office API is for monitoring, e.g. via Prometheus and
> optionally Grafana. It exports a number of application specific metrics in
> addition to JMX. Alerting and visualisations can also be built over this,
> e.g. via Prometheus or Grafana. This is simplest when deploying in
> Kubernetes.

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

## Docker

There is Docker image published by [this](https://github.com/g15ecb/cme/actions)
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