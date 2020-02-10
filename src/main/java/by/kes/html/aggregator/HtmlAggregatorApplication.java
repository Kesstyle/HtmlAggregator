package by.kes.html.aggregator;

import by.kes.html.aggregator.adapter.HtmlJsoupAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class HtmlAggregatorApplication {

	@Autowired
	private HtmlJsoupAdapter htmlJsoupAdapter;

	public static void main(String[] args) {
		SpringApplication.run(HtmlAggregatorApplication.class, args);
	}

	@GetMapping(path = "/api/v1/ping")
	public String ping(@RequestParam(defaultValue = "Kes") String name) {
		return String.format("Hello %s from HTML aggregator", name);
	}

	@GetMapping(path = "/api/v1/page")
	public String fetch(@RequestParam final String url) {
		return htmlJsoupAdapter.parseFromUrl(url);
	}
}
