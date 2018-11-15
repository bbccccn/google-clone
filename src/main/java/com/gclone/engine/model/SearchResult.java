package com.gclone.engine.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SearchResult {
    private String url;
    private String title;
    private String content;
    private String hitBlock;
}
