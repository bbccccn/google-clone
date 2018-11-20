package com.gclone.engine.service;

import com.gclone.engine.exception.IndexFolderNotAccessibleException;
import com.gclone.engine.model.SearchStatus;
import com.gclone.engine.model.SearchResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.search.highlight.*;
import org.apache.lucene.store.Directory;
import org.springframework.stereotype.Service;
import org.tartarus.snowball.ext.PorterStemmer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.gclone.engine.service.SearchEngineUtils.RESULTS_PER_PAGE;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchService {

    private static final int FRAGMENT_SIZE = 30;
    private static final String CONTENT = "content";
    private final SearchEngineUtils searchEngineUtils;

    private IndexSearcher getIndexSearcher() {
        IndexReader reader;
        reader = getIndexReader();
        return new IndexSearcher(reader);
    }

    private IndexReader getIndexReader() {
        Directory dir = searchEngineUtils.getFsDirectory();
        try {
            return DirectoryReader.open(dir);
        } catch (IOException e) {
            throw new IndexFolderNotAccessibleException("Cannot access index folder! Nested exception message: " + e.getMessage());
        }
    }

    protected String stemWord(String query) {
        PorterStemmer porterStemmer = new PorterStemmer();
        porterStemmer.setCurrent(query.toLowerCase());
        porterStemmer.stem();
        return porterStemmer.getCurrent();
    }

    protected String stemPhrase(String phrase) {
        String[] splitedWords = phrase.toLowerCase().split("\\s+");
        PorterStemmer porterStemmer = new PorterStemmer();
        StringBuilder result = new StringBuilder();
        for (String word : splitedWords) {
            porterStemmer.setCurrent(word);
            porterStemmer.stem();
            result.append(porterStemmer.getCurrent()).append(" ");
        }
        return result.toString().trim();
    }

    public SearchStatus doSearch(String queryString, int page) {
        boolean isPhrase = isPhrase(queryString);

        Query directQuery = buildDirectQuery(queryString);
        TopDocs directQueryResult = extractResult(directQuery, page);

        if (directQueryResult.totalHits == 0) {
            if (isPhrase) {
                MultiPhraseQuery query = buildFuzzyPhraseQuery(queryString);
                return new SearchStatus(query, extractResult(query, page));
            } else {
                String stemmedQuery = stemWord(queryString);
                Query query = buildFuzzyQuery(stemmedQuery);
                return new SearchStatus(query, extractResult(query, page));
            }
        }

        return new SearchStatus(directQuery, directQueryResult);
    }

    public TopDocs extractResult(Query query, int page) {
        try {
            IndexSearcher indexSearcher = getIndexSearcher();
            TopDocs result = indexSearcher.search(query, RESULTS_PER_PAGE * page);
            indexSearcher.getIndexReader().close();
            return result;
        } catch (IOException e) {
            log.info("Extraction of result for query {} failed!", query.toString());
            throw new RuntimeException(e);
        }
    }

    private MultiPhraseQuery buildFuzzyPhraseQuery(String queryString) {
        MultiPhraseQuery.Builder builder = new MultiPhraseQuery.Builder();
        String[] words = queryString.split(" ");
        for (String queryWord : words) {
            Term term = new Term(CONTENT, queryWord);
            builder.add(term);
        }
        return builder.setSlop(4).build();
    }

    private Query buildFuzzyQuery(String queryString) {
        return new FuzzyQuery(new Term(CONTENT, queryString), 1);
    }

    private Query buildDirectQuery(String queryString) {
        StandardAnalyzer analyzer = new StandardAnalyzer();
        QueryParser queryParser = new QueryParser(CONTENT, analyzer);
        return parseQuery(queryString, queryParser);
    }

    private Query parseQuery(String queryString, QueryParser queryParser) {
        try {
            return queryParser.parse(queryString);
        } catch (ParseException e) {
            log.info("Parsing query failed, text:" + queryString);
            throw new RuntimeException(e);
        }
    }

    private boolean isPhrase(String request) {
        return request.trim().contains(" ");
    }

    public List<SearchResult> getHitText(Query query, TopDocs topDocs, int page) throws IOException {
        Formatter formatter = new SimpleHTMLFormatter();
        QueryScorer scorer = new QueryScorer(query);
        Highlighter highlighter = new Highlighter(formatter, scorer);
        Fragmenter fragmenter = new SimpleSpanFragmenter(scorer, FRAGMENT_SIZE);
        highlighter.setTextFragmenter(fragmenter);
        IndexReader reader = getIndexReader();

        Map<ScoreDoc, Document> documents = getIndexedDocuments(topDocs, page);

        List<SearchResult> results = new ArrayList<>();

        for (Map.Entry<ScoreDoc, Document> result : documents.entrySet()) {
            int docId = result.getKey().doc;
            Document doc = result.getValue();

            String text = doc.get(CONTENT);
            TokenStream stream = TokenSources.getAnyTokenStream(reader, docId, CONTENT, new StandardAnalyzer());

            String[] frags = getBestFragments(highlighter, text, stream);
            SearchResult searchResult = new SearchResult(doc.get("url"), doc.get("title"), frags[0]);
            results.add(searchResult);
        }
        return results;
    }

    private String[] getBestFragments(Highlighter highlighter, String text, TokenStream stream) {
        try {
            return highlighter.getBestFragments(stream, text, 1);
        } catch (IOException | InvalidTokenOffsetsException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<ScoreDoc, Document> getIndexedDocuments(TopDocs topDocs, int page) {
        int offset = getOffset(page);

        if (offset >= topDocs.scoreDocs.length) {
            return new HashMap<>();
        }

        int pageLength = (topDocs.scoreDocs.length - offset) >= RESULTS_PER_PAGE ? RESULTS_PER_PAGE : topDocs.scoreDocs.length % RESULTS_PER_PAGE;
        Map<ScoreDoc, Document> indexedDocuments = new HashMap<>();

        IndexSearcher indexSearcher = null;
        try {
            indexSearcher = getIndexSearcher();
            for (int i = offset; i < offset + pageLength; i++) {
                indexedDocuments.put(topDocs.scoreDocs[i], indexSearcher.doc(topDocs.scoreDocs[i].doc));
            }
        } catch (IOException e) {
            log.error("Can't get documents");
        } finally {
            if (indexSearcher != null) {
                try {
                    indexSearcher.getIndexReader().close();
                } catch (IOException e) {
                    log.error("Error closing reader");
                }
            }
        }

        return indexedDocuments;
    }

    private int getOffset(int page) {
        return (page - 1) * RESULTS_PER_PAGE;
    }


}
