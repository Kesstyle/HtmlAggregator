package by.kes.html.aggregator.adapter;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;

@Service
@Slf4j
public class HtmlJsoupAdapter {

    @SneakyThrows(Exception.class)
    public String parseFromUrl(final String url) {
        return Optional.ofNullable(url)
                .map(this::connect)
                .map(this::getConnection)
                .map(d -> d.title())
                .orElse("N/A");
    }

    private Connection connect(final String url) {
        try {
            return Jsoup.connect(url);
        } catch (final Exception e) {
            log.error("Exception occured: ", e);
        }
        return null;
    }

    private Document getConnection(final Connection connection) {
        try {
            return connection.get();
        } catch (final IOException e) {
            log.error("Exception occured: ", e);
        }
        return null;
    }
}
