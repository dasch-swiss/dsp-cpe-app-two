# dsp-cpe-app ![build](https://github.com/dasch-swiss/dsp-cpe-app-two/workflows/build/badge.svg)

Example of 

1. Client written with [Laminar](https://github.com/raquo/Laminar), interacting with server using [zio-http](https://github.com/dream11/zio-http)

2. Server with zio-http serving both the compiled Javascript for the client and an endpoint for server-side interactions

3. Shared code with **protocol** definitions

4. Gzip compression on the server side

5. Docker packaging of the full application

6. Tests for the client with simulated DOM using [jsdom](https://github.com/scala-js/scala-js-env-jsdom-nodejs)

## Development mode

Run in SBT (uses fast JS compilation, not optimized):

```
sbt> ~runDev
```

And open http://localhost:8090/frontend

This will restart the server on any changes: shared code, client/server, assets.

## Tests
It is a prerequisite to have jsdom installed, in order for the frontend tests to run. Proposal:

```
yarn add jsdom
```
Then move into an sbt console and run tests as normal

## Production mode

Run in SBT (uses full JS optimization):

```
sbt> ~runProd
```

## Docker packaging 

```
sbt> backend/docker:publishLocal
```

Will publish the docker image with fully optimised JS code, and you can run the container:

```bash
$ docker run --rm -p 8080:8080 backend:0.1.0-SNAPSHOT

SLF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder".
SLF4J: Defaulting to no-operation (NOP) logger implementation
SLF4J: See http://www.slf4j.org/codes.html#StaticLoggerBinder for further details.
Running server on http://0.0.0.0:8090 (mode: prod)
```
