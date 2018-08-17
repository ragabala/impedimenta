#!/usr/bin/env sh
#
# Generate a Garmin-compatible map file for a certain geographic region.
#

region=us-northeast-latest

wget "http://download.geofabrik.de/north-america/${region}.osm.pbf"
splitter "${region}.osm.pbf"
osmconvert "${region}.osm.pbf" --out-o5m > "${region}.o5m"
osmfilter "${region}.o5m" \
    --keep-nodes= \
    --keep-ways-relations='boundary=administrative =postal_code postal_code=' \
    --out-o5m \
    > "${region}-boundaries.o5m"
java -cp /usr/share/java/mkgmap/mkgmap.jar \
    uk.me.parabola.mkgmap.reader.osm.boundary.BoundaryPreprocessor \
    "${region}-boundaries.o5m" \
    bounds
mkgmap \
    --route \
    --add-pois-to-areas \
    --bounds=bounds \
    --index \
    --gmapsupp \
    --mapname=$(echo "${RANDOM}" | sed -e :a -e 's/^.\{1,7\}$/&0/;ta') \
    6324*.osm.pbf
