package com.lgtm.easymoney.controllers;


import com.lgtm.easymoney.payload.SearchRsp;
import com.lgtm.easymoney.services.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import javax.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
  @Operation(summary = "Method to retrieve the public profile of a user by user ID.")
  public ResponseEntity<SearchRsp> searchById(@PathVariable(value = "id")
                                                @NotNull Long id) throws Exception {
    return new ResponseEntity<>(searchService.searchById(id), HttpStatus.OK);
  }

  @GetMapping("/info/{info}")
  @Operation(summary =
      "Method to retrieve a list of public profiles of users matched by search info.")
  public ResponseEntity<SearchRsp> searchByInfo(@PathVariable(value = "info") String info) {
    return new ResponseEntity<>(searchService.searchByInfo(info), HttpStatus.OK);
  }

}
