Prerequisites
=============

Install Maven 3.0.x
-------------------

From http://maven.apache.org

Building the Solr HighlightComponent
====================================

Execute

mvn install

or run it faster (without the tests)_

mvn -Pfast install

Configure Solr to use the component (solrconfig.xml)
=====================================================

To insert the component before or after the 'standard' components, use:

lib Folder to your jar-file
<lib dir="/xxx/lib/"/>  

<searchComponent name="highlightQuery" class="com.memonews.solr.handler.component.HighlightQueryComponent" />

<arr name="first-components">
	<str>highlightQuery</str>
</arr>

Register the query parser.
<queryParser name="memoparser" class="com.memonews.solr.search.HighlightQParserPlugin">
	<!-- comma separated values eg. title,name -->
 	<str name="fields">title</str>
 </queryParser>

Restart the server
=====================
Restart tomcat as last step.