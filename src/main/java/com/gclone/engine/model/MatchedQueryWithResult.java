package com.gclone.engine.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;

@Data
@AllArgsConstructor
public class MatchedQueryWithResult {
    private String queryString;
    private Query query;
    private TopDocs result;
}
