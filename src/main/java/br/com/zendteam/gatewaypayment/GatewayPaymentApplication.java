package br.com.zendteam.gatewaypayment;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableEncryptableProperties
public class GatewayPaymentApplication {
	public static void main(String[] args) {
		SpringApplication.run(GatewayPaymentApplication.class, args);
	}

}
