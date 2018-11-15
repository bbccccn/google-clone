package com.gclone.engine.service;

import com.gclone.engine.exception.PageNotFoundException;
import com.gclone.engine.model.ScrapingResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class EngineService {
    private final IndexService indexService;
    private final ScraperService scraperService;

    public long indexPagesWithDepth(String url, int depth) throws PageNotFoundException {
        Instant startTimestamp = Instant.now();

        ScrapingResult pageContent = scraperService.getPageContent(url);
        Map<String, ScrapingResult> urlToContentMap = doRecursiveSearch(pageContent.getCollectedUrls(), new HashSet<>(),1, depth);
        indexService.indexAllSites(urlToContentMap.values());

        Instant finishTimestamp = Instant.now();

        Duration interval = Duration.between(startTimestamp, finishTimestamp);

        return interval.getSeconds();
    }

    private Map<String, ScrapingResult> doRecursiveSearch(Set<String> scrapingResult, Set<String> scrapedUrls, int currentDepth, int limitDepth) {
        if (currentDepth <= limitDepth) {
            Map<String, ScrapingResult> result = new HashMap<>();
            for (String url : scrapingResult) {
                if (scrapedUrls.contains(url)) continue;
                ScrapingResult pageContent;
                try {
                    pageContent = scraperService.getPageContent(url);
                } catch (Exception e) {
                    continue;
                }
                result.put(pageContent.getRespectiveUrl(), pageContent);
                scrapedUrls.add(url);
                log.info("Scraped url " + pageContent.getRespectiveUrl());
                result.putAll(doRecursiveSearch(pageContent.getCollectedUrls(), scrapedUrls, currentDepth + 1, limitDepth));
            }
            return result;
        }
        return new HashMap<>();
    }
}
