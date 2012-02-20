/**
 * Copyright (C) 2012 MeMo News AG
 * 
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.memonews.solr.handler.component;

import java.util.Arrays;

import org.apache.solr.SolrTestCaseJ4;
import org.apache.solr.handler.component.ResponseBuilder;
import org.apache.solr.handler.component.SearchComponent;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.SolrQueryResponse;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * 
 * Test class for the <code>HighlightQueryComponent</code>.
 * 
 */
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

		ResponseBuilder rb = new ResponseBuilder(req(),
				new SolrQueryResponse(), Arrays.asList(c));
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
		ResponseBuilder rb = new ResponseBuilder(q, new SolrQueryResponse(),
				Arrays.asList(c));
		rb.doHighlights = true;

		c.process(rb);

		assertEquals("+text:irobot +text:roomba", rb.getHighlightQuery()
				.toString());
	}

	@Test
	public void shouldIdentifySchemaFieldsInQuery() throws Exception {

		String query = "hadoop AND solr AND title:test";
		SolrQueryRequest q = req("q", query, "defType", "edismax");
		final SearchComponent c = h.getCore().getSearchComponent(
				"highlightQuery");
		ResponseBuilder rb = new ResponseBuilder(q, new SolrQueryResponse(),
				Arrays.asList(c));
		rb.doHighlights = true;

		c.process(rb);

		assertEquals("+text:hadoop +text:solr", rb.getHighlightQuery()
				.toString());

	}

	@Test
	public void shouldAddNoneSchemaFieldsInQueryToHighlightQuery()
			throws Exception {

		String query = "hadoop AND solr";
		SolrQueryRequest q = req("q", query, "defType", "edismax");
		final SearchComponent c = h.getCore().getSearchComponent(
				"highlightQuery");
		ResponseBuilder rb = new ResponseBuilder(q, new SolrQueryResponse(),
				Arrays.asList(c));
		rb.doHighlights = true;

		c.process(rb);

		assertEquals("+text:hadoop +text:solr", rb.getHighlightQuery()
				.toString());

	}

	@Test
	public void shouldDeleteFieldsFromQueryWhereValueIsNull() throws Exception {

		String query = "hadoop AND solr NOT title:test OR subject:";
		SolrQueryRequest q = req("q", query, "defType", "edismax");
		final SearchComponent c = h.getCore().getSearchComponent(
				"highlightQuery");
		ResponseBuilder rb = new ResponseBuilder(q, new SolrQueryResponse(),
				Arrays.asList(c));
		rb.doHighlights = true;

		c.process(rb);

		assertEquals("+text:hadoop +text:solr", rb.getHighlightQuery()
				.toString());
	}

	@Test
	public void shouldDeleteSpecialCharsFromQuery() throws Exception {
		String query = "( hadoop AND solr ) AND \" title:test \"";
		SolrQueryRequest q = req("q", query, "defType", "edismax");
		final SearchComponent c = h.getCore().getSearchComponent(
				"highlightQuery");
		ResponseBuilder rb = new ResponseBuilder(q, new SolrQueryResponse(),
				Arrays.asList(c));
		rb.doHighlights = true;

		c.process(rb);

		assertEquals("+text:hadoop +text:solr", rb.getHighlightQuery()
				.toString());
	}

}