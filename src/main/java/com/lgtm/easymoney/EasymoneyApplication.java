package com.lgtm.easymoney;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(info=@Info(title="EasyMoney Service"))
public class EasymoneyApplication {

	public static void main(String[] args) {
		SpringApplication.run(EasymoneyApplication.class, args);
	}

}
