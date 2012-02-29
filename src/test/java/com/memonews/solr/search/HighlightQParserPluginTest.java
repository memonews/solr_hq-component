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
package com.memonews.solr.search;

import org.apache.lucene.search.Query;
import org.apache.solr.SolrTestCaseJ4;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.search.QParser;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * 
 * Test class for the <code>HighlightQParserPlugin</code>.
 * 
 */
public class HighlightQParserPluginTest extends SolrTestCaseJ4 {

    @BeforeClass
    public static void beforeClass() throws Exception {
	initCore("solrconfig.xml", "schema.xml");
    }

    @Test
    public void testHighlightQueryParser() throws Exception {
	ModifiableSolrParams local = new ModifiableSolrParams();
	ModifiableSolrParams params = new ModifiableSolrParams();
	HighlightQParserPlugin parserPlugin = new HighlightQParserPlugin();

	String qstr = "title:\"Apache Lucene\" AND solr OR name:123 NOT \"Apache Hadoop\"";
	SolrQueryRequest req = req("q", qstr);
	QParser parser = parserPlugin.createParser(qstr, local, params, req);
	Query query = parser.parse();
	assertEquals(
		"+title:\"apache lucene\" text:solr name:123 -text:\"apache hadoop\"",
		query.toString());
    }
}
