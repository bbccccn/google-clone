package com.gclone.engine.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;

@Data
@AllArgsConstructor
public class SearchStatus {
    private Query query;
    private TopDocs result;
}
