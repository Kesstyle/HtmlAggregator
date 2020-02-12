package by.kes.html.aggregator;

import by.kes.html.aggregator.adapter.HtmlJsoupAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
	public String fetch(@RequestHeader(name = "Target-URL", defaultValue = "") final String url) {
		return htmlJsoupAdapter.parseFromUrl(url);
	}

	@GetMapping(path = "/api/v1/elements")
	public List<String> getElements(@RequestHeader(name = "Target-URL", defaultValue = "") final String url,
									@RequestHeader(name = "Target-html-template", defaultValue = "")
									final String template,
									@RequestParam (defaultValue = "") final String filter) {
		return htmlJsoupAdapter.getListOfElementsFromUrlByPattern(url, template, filter);

	}
}
