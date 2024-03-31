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