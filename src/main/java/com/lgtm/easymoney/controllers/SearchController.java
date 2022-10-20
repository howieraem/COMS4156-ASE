package com.lgtm.easymoney.controllers;

import com.lgtm.easymoney.payload.SearchRsp;
import com.lgtm.easymoney.services.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * search controller.
 */
@RestController
@RequestMapping("/search")
public class SearchController {
  private final SearchService searchService;

  @Autowired
  public SearchController(SearchService searchService) {
    this.searchService = searchService;
  }

  @GetMapping("/id/{id}")
  @Operation(description = "Method to retrieve the public profile of a user by user ID.")
  public ResponseEntity<SearchRsp> searchById(@PathVariable(value = "id") String id) {
    return searchService.searchById(Long.valueOf(id));
  }

  @GetMapping("/info/{info}")
  @Operation(description = "Method to retrieve a list"
          + " of public profiles of users matched by search info.")
  public ResponseEntity<SearchRsp> searchByInfo(@PathVariable(value = "info") String info) {
    return searchService.searchByInfo(info);
  }

}
