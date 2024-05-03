FROM solr:@solr.version@
LABEL authors="fonfalleh"

COPY lib/* /opt/solr-@solr.version@/lib/
COPY conf /conf

CMD ["solr-precreate", "playin", "/conf/playin"]