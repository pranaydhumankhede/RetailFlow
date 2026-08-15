package in.bushansirgur.billingsoftware;

import in.bushansirgur.billingsoftware.config.EnvConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BillingsoftwareApplication {

	static {
		// Force load EnvConfig before Spring initialization
		new EnvConfig();
	}

	public static void main(String[] args) {
		SpringApplication.run(BillingsoftwareApplication.class, args);
	}

}
