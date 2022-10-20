package com.lgtm.easymoney;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * starting application.
 */
@SpringBootApplication
@OpenAPIDefinition(
    info = @Info(
        title = "EasyMoney Service",
        version = "0.0.1",
        license = @License(
            name = "Apache 2.0",
            url = "https://www.apache.org/licenses/LICENSE-2.0.html"
        )
    )
)
public class EasymoneyApplication {

  public static void main(String[] args) {
    SpringApplication.run(EasymoneyApplication.class, args);
  }

}
