package com.gclone.engine.service;

import com.gclone.engine.exception.IndexFolderNotAccessibleException;
import com.gclone.engine.model.ScrapingResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.Collection;

import static org.apache.lucene.index.IndexWriterConfig.OpenMode.CREATE_OR_APPEND;

@Service
@RequiredArgsConstructor
@Slf4j
public class IndexService {

    private final SearchEngineUtils searchEngineUtils;

    @PostConstruct
    public void initValues() {
        String expectedIndexPath = searchEngineUtils.getExpectedIndexPath();
        File indexFolder = new File(expectedIndexPath);
        if (!indexFolder.exists()) {
            boolean isDirectoryCreated = indexFolder.mkdir();
            if (!isDirectoryCreated) {
                throw new IndexFolderNotAccessibleException("Can't create folder for storing index under path: " + expectedIndexPath);
            } else {
                log.info("Directory for index created under path '{}' ", expectedIndexPath);
            }
        }
    }

    private IndexWriter getIndexWriter() throws IOException {
        FSDirectory dir = searchEngineUtils.getFsDirectory();
        IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
        config.setOpenMode(CREATE_OR_APPEND);
        return new IndexWriter(dir, config);
    }

    public void indexContent(String content, String title, String url) {
        Document document = new Document();
        document.add(new TextField("content", content, Field.Store.YES));
        document.add(new TextField("title", title, Field.Store.YES));
        document.add(new StringField("url", url, Field.Store.YES));
        try (IndexWriter indexWriter = getIndexWriter()) {
            indexWriter.addDocument(document);
            indexWriter.commit();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void indexAllSites(Collection<ScrapingResult> scrapedSites) {
        for (ScrapingResult scrapedSite : scrapedSites) {
            indexContent(scrapedSite.getPageContent(), scrapedSite.getTitle(), scrapedSite.getRespectiveUrl());
        }

        //Way in which it written below doesn't work - no data added no matter how I experiment.

//        List<Document> documents = new ArrayList<>(scrapedSites.size());
//        for (ScrapingResult scrapedSite : scrapedSites) {
//            Document document = new Document();
//            document.add(new TextField("content", scrapedSite.getPageContent(), Field.Store.YES));
//            document.add(new TextField("url", scrapedSite.getRespectiveUrl(), Field.Store.YES));
//        }
//        IndexWriter indexWriter = getIndexWriter();
//        indexWriter.addDocuments(documents);
//        indexWriter.commit();
//        indexWriter.close();
    }

}
