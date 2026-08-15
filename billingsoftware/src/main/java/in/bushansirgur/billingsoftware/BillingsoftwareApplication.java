package in.bushansirgur.billingsoftware;

import in.bushansirgur.billingsoftware.config.EnvConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(EnvConfig.class)
public class BillingsoftwareApplication {

	public static void main(String[] args) {
		SpringApplication.run(BillingsoftwareApplication.class, args);
	}

}
