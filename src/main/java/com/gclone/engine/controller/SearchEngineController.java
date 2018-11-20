package com.gclone.engine.controller;

import com.gclone.engine.service.EngineService;
import com.gclone.engine.service.SearchService;
import com.gclone.engine.exception.PageNotFoundException;
import com.gclone.engine.model.SearchStatus;
import com.gclone.engine.model.SearchResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.validator.routines.UrlValidator;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.TopDocs;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.List;

import static com.gclone.engine.service.SearchEngineUtils.RESULTS_PER_PAGE;

@Controller
@RequiredArgsConstructor
@Slf4j
public class SearchEngineController {

    private final EngineService engineService;
    private final SearchService searchService;

    @GetMapping("/")
    public String mainPage() {
        return "search";
    }

    @GetMapping("/index")
    public String indexPage() {
        return "index";
    }

    @PostMapping("/index")
    public String doIndex(@RequestParam(value="q") String url, @RequestParam(value = "depth", required = false, defaultValue = "2") int depth, Model model) {
        if (!UrlValidator.getInstance().isValid(url)) {
            model.addAttribute("errorMessage", "Invalid url");
            return "index";
        }

        try {
            long executionTime = engineService.indexPagesWithDepth(url, depth);
            model.addAttribute("executionTime", executionTime);
        } catch (PageNotFoundException e) {
            model.addAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Undefined error.");
        }
        return "index";
    }

    @GetMapping("/search")
    public String search(@RequestParam(value="q") String query, @RequestParam(value = "page", required = false, defaultValue = "1") int page, Model model) throws IOException, ParseException {
        SearchStatus searchStatus = searchService.doSearch(query, page);

        if (searchStatus.getResult().totalHits != 0) {
            TopDocs topDocs = searchStatus.getResult();
            List<SearchResult> searchResults = searchService.getHitText(searchStatus.getQuery(), topDocs, page);

            long l = topDocs.totalHits / RESULTS_PER_PAGE;
            model.addAttribute("amountOfPages", l + (topDocs.totalHits % RESULTS_PER_PAGE > 0? 1 : 0));

            model.addAttribute("currentPage", page);
            model.addAttribute("searchResults", searchResults);
        }
        model.addAttribute("query", query);
        return "search-result";
    }

}
