FROM solr:${solr.version}
LABEL authors="fonfalleh"

COPY target/${artifactId}-${project.version}.jar /opt/solr-${solr.version}/lib/
COPY target/dependency/* /opt/solr-${solr.version}/lib/
COPY target/conf /conf

CMD ["solr-precreate", "playin", "/conf/playin"]

HEALTHCHECK --start-period=10s --timeout=100ms --start-interval=100ms CMD curl -f http://localhost:8983/solr/playin/select || exit 1
