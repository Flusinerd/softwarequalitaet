package eu.jan_krueger.uebung1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class Uebung1Application {

  public static void main(String[] args) {
    SpringApplication.run(Uebung1Application.class, args);
  }

  @GetMapping("/hello")
  public String hello(@RequestParam(value = "name", defaultValue = "World") String name) {
    return String.format("Hello %s!", name);
  }

  @GetMapping("/timestamp")
  public String timestamp() {
    return String.format("%s", System.currentTimeMillis() / 1000);
  }
}
