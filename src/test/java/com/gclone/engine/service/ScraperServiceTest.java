package com.gclone.engine.service;

import com.gclone.engine.exception.PageNotFoundException;
import com.gclone.engine.model.ScrapingResult;
import com.gclone.engine.service.ScraperService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ScraperServiceTest {

    @Test
    public void getPageContent() throws PageNotFoundException, IOException {
        String expectedUrl = "expected url";
        String expectedTitle = "Test site title";
        ScraperService scraperService = mock(ScraperService.class);
        when(scraperService.getPage(any())).thenReturn(getMock());
        when(scraperService.getPageContent(any())).thenCallRealMethod();

        ScrapingResult scrapingResult = scraperService.getPageContent(expectedUrl);

        assertEquals(2, scrapingResult.getCollectedUrls().size());
        assertEquals(expectedUrl, scrapingResult.getRespectiveUrl());
        assertEquals(expectedTitle, scrapingResult.getTitle());
        assertEquals("HTML Links This is first link to be parsed And here there's no direct link, it shouldn't be parsed : https://www.google.com This is a second link", scrapingResult.getPageContent());
    }

    private Document getMock() throws IOException {
        return Jsoup.parse(new ClassPathResource("testpage.html").getFile(), "UTF-8");
    }
}