package com.gclone.engine.service;

import com.gclone.engine.service.SearchService;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SearchServiceTest {

    SearchService searchService = new SearchService(null);

    @Test
    public void doSingleWordStem() {
        String word = "cats";
        String expectedResult = "cat";

        String result = searchService.stemWord(word);

        assertEquals(result, expectedResult);
    }

    @Test
    public void doMultiWordStem() {
        String word = "cats dogs";
        String expectedResult = "cat dog";

        String result = searchService.stemPhrase(word);

        assertEquals(expectedResult, result);
    }
}