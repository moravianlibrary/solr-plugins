=====================
=== Installation ====
=====================

2) mvn clean && mvn install

1) Copy target/solr_caching_header_filter-0.0.1-SNAPSHOT.jar to your Solr installation (eg. solr-webapp/webapp/WEB-INF/lib/).

2) Add the following lines to the begining of web.xml (eg. solr-webapp/webapp/WEB-INF/lib/): 

  <filter>
    <filter-name>SolrCachingHeaderFilter</filter-name>
    <filter-class>cz.mzk.servlet.SolrCachingHeaderFilter</filter-class>
    <init-param>
      <param-name>minRequestTime</param-name>
      <param-value>10</param-value>
    </init-param>
  </filter>

  <filter-mapping>
    <filter-name>SolrCachingHeaderFilter</filter-name>
    <url-pattern>/biblio/select</url-pattern>
  </filter-mapping>
