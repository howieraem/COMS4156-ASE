package com.lgtm.easymoney.integrations;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

/** Integration tests of API invocations. */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class IntegrationTest {
  @Autowired
  private MockMvc mvc;

  @Test
  public void noAccessForUnauthenticatedUsers() throws Exception {
    mvc.perform(post("/friend/add")).andExpect(status().isUnauthorized());
    mvc.perform(put("/friend/accept")).andExpect(status().isUnauthorized());
    mvc.perform(delete("/friend/{uid}", String.valueOf(2L))).andExpect(status().isUnauthorized());
    mvc.perform(get("/friend")).andExpect(status().isUnauthorized());
    mvc.perform(get("/friend/pending")).andExpect(status().isUnauthorized());
  }
}
