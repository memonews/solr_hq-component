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

import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.solr.common.params.CommonParams;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.search.QParser;
import org.apache.solr.search.QParserPlugin;
import org.apache.solr.search.QueryParsing;
import org.apache.solr.search.SolrQueryParser;

/**
 * Excludes the given fields from the query.
 *  
 * Since Solr 4.0
 * 
 */
public class HighlightQParserPlugin extends QParserPlugin {

	private TreeSet<String> fieldNames = new TreeSet<String>();
	
	@Override
	@SuppressWarnings("rawtypes")
	public void init(NamedList args) {
		if (args != null) {
			String fields = (String)args.get("fields");
			if (StringUtils.isNotBlank(fields)) {
				for (String field : fields.split(",")) {
					fieldNames.add(StringUtils.trim(field));
				}
			}
		}
	}

	@Override
	public QParser createParser(String qstr, SolrParams localParams,
			SolrParams params, SolrQueryRequest req) {
		return new HighlightQParser(qstr, localParams, params, req, this.fieldNames);
	}

}

class HighlightQParser extends QParser {
	
	private TreeSet<String> fieldNames;

	public HighlightQParser(String qstr, SolrParams localParams,
			SolrParams params, SolrQueryRequest req, TreeSet<String> fieldNames) {
		super(qstr, localParams, params, req);
		this.fieldNames = fieldNames;
	}

	@Override
	public Query parse() throws ParseException {

		String qstr = getString();
		if (qstr == null) {
			return null;
		}
		
		String defaultField = getParam(CommonParams.DF);
		if (defaultField == null) {
			defaultField = getReq().getSchema().getDefaultSearchFieldName();
		}

		HighlightQueryParserNew parser = new HighlightQueryParserNew(this, defaultField, this.fieldNames);
		
		parser.setDefaultOperator(QueryParsing.getQueryParserDefaultOperator(
				getReq().getSchema(), getParam(QueryParsing.OP)));

		Query q = parser.parse(qstr);
		return q;
	}
}

class HighlightQueryParserNew extends SolrQueryParser {

	private TreeSet<String> fieldNames;
	
	public HighlightQueryParserNew(QParser parser, String defaultField, TreeSet<String> fieldNames) {
		super(parser, defaultField, parser.getReq().getSchema().getQueryAnalyzer());
		this.fieldNames = fieldNames;
	}

	@Override
	protected Query getFieldQuery(String field, String queryText, boolean quoted)
			throws ParseException {
		if (fieldNames.contains(field)) {
			return new BooleanQuery();
		}
		return super.getFieldQuery(field, queryText, quoted);
	}

	@Override
	protected Query getRangeQuery(String field, String part1, String part2,
			boolean startInclusive, boolean endInclusive) throws ParseException {
		if (fieldNames.contains(field)) {
			return new BooleanQuery();
		}
		return super.getRangeQuery(field, part1, part2, startInclusive, endInclusive);
	}



	@Override
	protected Query getPrefixQuery(String field, String termStr)
			throws ParseException {
		if (fieldNames.contains(field)) {
			return new BooleanQuery();
		}
		return super.getPrefixQuery(field, termStr);
	}

	@Override
	protected Query getWildcardQuery(String field, String termStr)
			throws ParseException {
		if (fieldNames.contains(field)) {
			return new BooleanQuery();
		}
		return super.getWildcardQuery(field, termStr);
	}

	@Override
	protected Query getRegexpQuery(String field, String termStr)
			throws ParseException {
		if (fieldNames.contains(field)) {
			return new BooleanQuery();
		}
		return super.getRegexpQuery(field, termStr);
	}
	
	
}