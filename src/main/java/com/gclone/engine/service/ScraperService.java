package com.gclone.engine.service;

import com.gclone.engine.exception.PageNotFoundException;
import com.gclone.engine.model.ScrapingResult;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

@Service
public class ScraperService {

    public ScrapingResult getPageContent(String url) throws PageNotFoundException {
        Document page = getPage(url);

        Set<String> links = page.select("a[href]").eachAttr("abs:href").stream().filter(v -> !v.startsWith("mailto")).collect(toSet());
        String header = page.title();
        String pageContent = page.body().text();

        return new ScrapingResult(url, header, links, pageContent);
    }

    protected Document getPage(String url) throws PageNotFoundException {
        try {
            return Jsoup.connect(url).get();
        } catch (IOException e) {
            throw new PageNotFoundException();
        }
    }


}
