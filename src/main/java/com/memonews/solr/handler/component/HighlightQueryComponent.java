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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.solr.common.SolrException;
import org.apache.solr.common.params.CommonParams;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.handler.component.ResponseBuilder;
import org.apache.solr.handler.component.SearchComponent;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.schema.IndexSchema;
import org.apache.solr.schema.SchemaField;
import org.apache.solr.search.QParser;

/**
 * This search component takes all given query terms except the fielded terms
 * and sets the highlight query directly on the <code>ResponseBuilder</code>.
 * 
 * A fielded term is defined by the field name followed by a colon ":"
 * and then the term you are looking for.
 * 
 * Since Solr 4.0
 * 
 */
public class HighlightQueryComponent extends SearchComponent {

	private final Set<String> charset = new HashSet<String>(0);

	public HighlightQueryComponent() {

		final String[] collection = new String[] { "AND", "OR", "NOT", "(",
				")", "\"" };
		this.charset.addAll(Arrays.asList(collection));
	}

	@Override
	public void prepare(final ResponseBuilder rb) throws IOException {
		// do nothing
	}

	@Override
	public void process(final ResponseBuilder rb) throws IOException {

		if (rb.doHighlights) {

			final List<String> terms = new ArrayList<String>(0);
			final SolrQueryRequest req = rb.req;
			final IndexSchema schema = req.getSchema();
			final Map<String, SchemaField> fields = schema.getFields();
			final SolrParams params = req.getParams();
			final String query = params.get(CommonParams.Q);
			final String[] splitQuery = query.split(" ");

			for (String split : splitQuery) {
				if (!charset.contains(split)) {
					if (split.contains(":")) {
						// is a field?
						final String[] splitParams = split.split(":");
						if (splitParams.length == 2) {
							if (!fields.containsKey(splitParams[0])) {
								terms.add(split);
							}
						}
					} else {
						split = StringUtils.remove(split, "\"");
						terms.add(split);
					}
				}
			}

			final StringBuffer hlQuery = new StringBuffer();

			for (int i = 0; i < terms.size(); i++) {
				hlQuery.append(terms.get(i));
				if (i != terms.size() - 1) {
					hlQuery.append(" AND ");
				}
			}

			if (hlQuery.length() > 0) {
				try {
					QParser parser = QParser.getParser(hlQuery.toString(),
							null, rb.req);
					rb.setHighlightQuery(parser.getHighlightQuery());
				} catch (ParseException e) {
					throw new SolrException(
							SolrException.ErrorCode.BAD_REQUEST, e);
				}
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