FROM playin_solr:@solr.version@
LABEL authors="fonfalleh"

COPY web.xml /opt/solr-@solr.version@/server/solr-webapp/webapp/WEB-INF/
COPY jetty-* /opt/solr-@solr.version@/server/solr-webapp/webapp/WEB-INF/lib/