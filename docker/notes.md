TODO review this. Not quite true, but was a bit tricky to get right

- Docker compose using Solr 9.5.0
- add user to docker group to avoid sudo

`solr-precreate`:
```
# Create a core on disk
# arguments are: corename configdir
```

https://solr.apache.org/guide/8_5/libs.html

Mounting `./lib:/var/solr/data/lib` before first launch breaks premissions, no good.

Current way: Mount as `/opt/solr-x.x.x/lib`.

Another workaround is supplying it in `conf/playin/lib/`, but it's only copied at core creation, so it's annoying to update the plugins.

----
# 2026-10-26~
Working on integration tests. Surprisingly annoying to test uploading files using multipart forms.
First class citizen in the framework, but not in the testing framework (doable, but ergonomics are... ech. At least somewhat happy I didn't need to manually create client within JavalinTest (which was my first attempt))
Lots of hallucinations when looking for an answer, perhaps they worked in javalin <7
Bruno-generated snippet did not work ootb either.
Looked at some spec snippets but ultimately looked at what curl output using the following:

```shell
curl --request POST \                                                                                                                             INT 14:10:22
  --url http://localhost:7070/fileupload \
  --header 'Content-Type: multipart/form-data' \
  --form id=1234 \
  --form file=@foo.xml -v --trace-ascii -
```

Man, it's not pretty. But it seems to work running from intellij when already running solr (intellij doesn't pick up solr.port property from failsafe conf in pom).
TODO get failsafe to work.
