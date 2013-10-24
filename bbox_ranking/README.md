FunctionQuery for relevance ranking of bounding boxes for use with new spatial
support in Solr 4.x (http://wiki.apache.org/solr/SolrAdaptersForLuceneSpatial4).
The score is computed as follows:

score = (C / (A + B - C))

where:

A - total area of first bounding box
B - total area of second bounding box
C - overlapping area of both bounding boxes

Computed score is between 0.0 (no overlap area) and 1.0 (exact match).

Installation:

1) Set the solr.version property in pom.xml accordingly to your Solr version and the execute 'maven install' 

2) Add bbox_ranking-0.0.1-SNAPSHOT.jar and jts-1.13.jar (http://sourceforge.net/projects/jts-topo-suite/) to your solr lib directory.

3) Register the function in solrconfig.xml:

<valueSourceParser name="geo_overlap" class="cz.mzk.solr.plugin.functions.BoundingBoxOverlapValueSourceParser" />

Usage:

Syntax: geo_overlap(box1, box2)

Parameters box1 and box2 can be either string value or field name containing bounding box (must be string).

Example:

geo_overlap('16.528408813476236 49.13083837517464 16.68359069824182 49.237655388053895', bbox_geo_str)
