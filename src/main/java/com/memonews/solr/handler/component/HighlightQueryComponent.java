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

import java.io.IOException;

import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.Query;
import org.apache.solr.common.SolrException;
import org.apache.solr.common.params.CommonParams;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.handler.component.ResponseBuilder;
import org.apache.solr.handler.component.SearchComponent;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.search.QParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This search component takes all given query terms and parse them with the
 * <code>HighlightQParser</code> and sets the highlight query directly on the
 * <code>ResponseBuilder</code>.
 * 
 * A fielded term is defined by the field name followed by a colon ":" and then
 * the term you are looking for.
 * 
 * Since Solr 4.0
 * 
 */
public class HighlightQueryComponent extends SearchComponent {
    private static final Logger LOG = LoggerFactory
	    .getLogger(HighlightQueryComponent.class);

    private static final String NAME = "memoparser";

    private Query parseAndCleanQuery(final String query,
	    final SolrQueryRequest req, final String defaultType)
	    throws ParseException {
	QParser parser = QParser.getParser(query, defaultType, req);
	Query highlightQuery = parser.getHighlightQuery();
	if (LOG.isDebugEnabled()) {
	    LOG.debug("Parsed and cleaned query: " + highlightQuery.toString());
	}
	return highlightQuery;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public void init(NamedList args) {
	super.init(args);
    }

    @Override
    public void prepare(final ResponseBuilder rb) throws IOException {
	// do nothing
    }

    @Override
    public void process(final ResponseBuilder rb) throws IOException {

	if (rb.doHighlights) {
	    final SolrQueryRequest req = rb.req;
	    final SolrParams params = req.getParams();
	    final String qstr = params.get(CommonParams.Q);
	    if (LOG.isDebugEnabled()) {
		LOG.info("Original query: " + qstr);
	    }

	    try {
		Query parsedQuery = parseAndCleanQuery(qstr, req, NAME);
		if (parsedQuery != null) {
		    rb.setHighlightQuery(parsedQuery);
		}
	    } catch (ParseException e1) {
		throw new SolrException(SolrException.ErrorCode.BAD_REQUEST, e1);
	    }
	}
    }

    @Override
    public String getDescription() {
	return null;
    }

    @Override
    public String getSourceId() {
	return null;
    }

    @Override
    public String getSource() {
	return null;
    }

    @Override
    public String getVersion() {
	return null;
    }

}