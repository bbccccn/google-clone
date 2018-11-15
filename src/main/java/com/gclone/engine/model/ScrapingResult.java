package com.gclone.engine.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

@Data
@AllArgsConstructor
public class ScrapingResult {
    private String respectiveUrl;
    private String title;
    private Set<String> collectedUrls;
    private String pageContent;
}
