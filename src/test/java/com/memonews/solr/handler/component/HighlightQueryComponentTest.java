package com.memonews.solr.handler.component;

import java.util.Arrays;

import org.apache.solr.SolrTestCaseJ4;
import org.apache.solr.handler.component.ResponseBuilder;
import org.apache.solr.handler.component.SearchComponent;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.SolrQueryResponse;
import org.junit.BeforeClass;
import org.junit.Test;

public class HighlightQueryComponentTest extends SolrTestCaseJ4 {

	@BeforeClass
	public static void beforeClass() throws Exception {
		initCore("solrconfig.xml", "schema.xml");
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
	}

	@Override
	public void tearDown() throws Exception {
		// if you override setUp or tearDown, you better call
		// the super classes version
		clearIndex();
		super.tearDown();
	}

	@Test
	public void shouldLoadConfig() throws Exception {

		final SearchComponent c = h.getCore().getSearchComponent(
				"highlightQuery");
		assertNotNull(c);
	}

	 @Test
	 public void shouldNotAddHighlightQuery() throws Exception {
		 
		 final SearchComponent c = h.getCore().getSearchComponent(
					"highlightQuery");
		 
		 ResponseBuilder rb = new ResponseBuilder(req(),  new SolrQueryResponse(), Arrays.asList(c));
		 rb.doHighlights = false;
		 c.process(rb);
		 
		 assertNull(rb.getHighlightQuery());
	 }
	 
	
	 @Test
	 public void shouldAddHighlightQuery() throws Exception {
		 String query = "irobot AND roomba";
		 SolrQueryRequest q = req("q", query, "defType", "edismax");
		 final SearchComponent c = h.getCore().getSearchComponent(
					"highlightQuery");
		 ResponseBuilder rb = new ResponseBuilder(q,  new SolrQueryResponse(), Arrays.asList(c));
		 rb.doHighlights = true;
		 
		 c.process(rb);
		 
		 assertEquals("+text:irobot +text:roomba", rb.getHighlightQuery().toString());
	 }


	
	 @Test
	 public void shouldIdentifySchemaFieldsInQuery() throws Exception {
	
		 String query = "hadoop AND solr AND title:test";
		 SolrQueryRequest q = req("q", query, "defType", "edismax");
		 final SearchComponent c = h.getCore().getSearchComponent(
					"highlightQuery");
		 ResponseBuilder rb = new ResponseBuilder(q,  new SolrQueryResponse(), Arrays.asList(c));
		 rb.doHighlights = true;
		 
		 c.process(rb);
		 
		 assertEquals("+text:hadoop +text:solr", rb.getHighlightQuery().toString());
		 
	 }

	
	 @Test
	 public void shouldAddNoneSchemaFieldsInQueryToHighlightQuery() throws Exception {

		 String query = "hadoop AND solr";
		 SolrQueryRequest q = req("q", query, "defType", "edismax");
		 final SearchComponent c = h.getCore().getSearchComponent(
					"highlightQuery");
		 ResponseBuilder rb = new ResponseBuilder(q,  new SolrQueryResponse(), Arrays.asList(c));
		 rb.doHighlights = true;
		 
		 c.process(rb);
		 
		 assertEquals("+text:hadoop +text:solr", rb.getHighlightQuery().toString());
	
	 }
    @Test
    public void shouldDeleteFieldsFromQueryWhereValueIsNull() throws Exception {
    	
		 String query = "hadoop AND solr NOT title:test OR subject:";
		 SolrQueryRequest q = req("q", query, "defType", "edismax");
		 final SearchComponent c = h.getCore().getSearchComponent(
					"highlightQuery");
		 ResponseBuilder rb = new ResponseBuilder(q,  new SolrQueryResponse(), Arrays.asList(c));
		 rb.doHighlights = true;
		 
		 c.process(rb);
		 
		 assertEquals("+text:hadoop +text:solr", rb.getHighlightQuery().toString());
    }

    @Test
    public void shouldDeleteSpecialCharsFromQuery() throws Exception {
		 String query = "( hadoop AND solr ) AND \" title:test \"";
		 SolrQueryRequest q = req("q", query, "defType", "edismax");
		 final SearchComponent c = h.getCore().getSearchComponent(
					"highlightQuery");
		 ResponseBuilder rb = new ResponseBuilder(q,  new SolrQueryResponse(), Arrays.asList(c));
		 rb.doHighlights = true;
		 
		 c.process(rb);
		 
		 assertEquals("+text:hadoop +text:solr", rb.getHighlightQuery().toString());
    }
    
}


//http://memo-test-solr-2:8080/solr/shard_2012_01/select/?tie=0.01&hl.simple.pre=[[_HIGHLIGHT_]]&q.alt=*:*&hl=true&shard.url=memo-test-solr-2:8080/solr/shard_2012_01&NOW=1329320361355&hl.simple.post=[[/_HIGHLIGHT_]]&fq=pubDate:[%222012-01-15T15:39:21Z%22+TO+%222012-02-15T15:39:21Z%22]&hl.fragsize=400&mm=%0A++++++++++++++++++++++++0%0A++++++++++++++++++++&ids=de.golem.video/wirtschaft/5744/irobot-chef-colin-angle-ueber-roomba-770-und-780.html&qf=title+text+author+url&hl.fl=title+text+author+url&defType=edismax&rows=20&pf=%0A++++++++++++++++++++++++text%0A++++++++++++++++++++&hl.snippets=100&start=0&q=irobot+AND+roomba+AND+language:de&isShard=true&ps=100