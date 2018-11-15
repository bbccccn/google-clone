package com.gclone.engine.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.store.FSDirectory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

@Component
@Slf4j
public class SearchEngineUtils {

    public static final int RESULTS_PER_PAGE = 10;
    public static final String INDEXES_FOLDER = "indexes";

    public FSDirectory getFsDirectory() {
        String indexPath = getExpectedIndexPath();
        try {
            return FSDirectory.open(Paths.get(indexPath));
        } catch (IOException e) {
            throw new RuntimeException("Can't open directory for index: directory not found under path " + indexPath);
        }
    }

    public String getExpectedIndexPath() {
        return Paths.get("").toAbsolutePath().toString() + File.separator + INDEXES_FOLDER;
    }
}
