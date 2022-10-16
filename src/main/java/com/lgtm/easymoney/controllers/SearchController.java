package com.lgtm.easymoney.controllers;

import com.lgtm.easymoney.payload.ProfileRsp;
import com.lgtm.easymoney.payload.SearchRsp;
import com.lgtm.easymoney.services.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/search")
public class SearchController {
    private final SearchService searchService;

    @Autowired
    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<SearchRsp> searchByID(@PathVariable(value = "id") String id) {
        return searchService.searchByID(Long.valueOf(id));
    }
    @GetMapping("/info/{info}")
    public ResponseEntity<SearchRsp> searchByInfo(@PathVariable(value = "info") String info) {
        return searchService.searchByInfo(info);
    }

}
