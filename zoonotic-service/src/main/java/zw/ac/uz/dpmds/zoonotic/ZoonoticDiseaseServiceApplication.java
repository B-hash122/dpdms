package zw.ac.uz.dpmds.zoonotic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class ZoonoticDiseaseServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZoonoticDiseaseServiceApplication.class, args);
    }

}
