FROM solr:${solr.version}
LABEL authors="fonfalleh"

COPY target/${artifactId}-${project.version}.jar /opt/solr-${solr.version}/lib/
COPY target/conf /conf

CMD ["solr-precreate", "playin", "/conf/playin"]
