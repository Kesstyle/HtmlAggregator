package by.kes.html.aggregator.adapter;

import by.kes.html.aggregator.utils.HtmlHelper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.empty;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.springframework.util.CollectionUtils.isEmpty;

@Service
@Slf4j
public class HtmlJsoupAdapter {

    private static final String CLASS_COMMAND = "class=";
    private static final String CHILD_CLASS_COMMAND = "c-class=";
    private static final String TAG_COMMAND = "tag=";
    private static final String CHILD_COMMAND = "c";
    private static final String CHILD_NUMBERED_COMMAND = "c-";
    private static final String NA = "N/A";
    private static final String COMMA = ",";
    private static final String CHILD_COMMAND_SEPARATOR = "-";

    @Autowired
    private HtmlHelper htmlHelper;

    @SneakyThrows(Exception.class)
    public String parseFromUrl(final String url) {
        return getDocument(url)
                .map(d -> d.title())
                .orElse(NA);
    }

    public List<String> getListOfElementsFromUrlByPattern(final String url, final String pattern,
                                                          final String filter) {
        final String[] commands = pattern.split(COMMA);
        log.info(url);
        log.info(commands.toString());

        final String startCommand = commands[0];

        Stream<Elements> elementsStream = processCommandToGetElements(getDocument(url)
                .map(Stream::of).orElseGet(Stream::empty), startCommand)
                .peek(e -> log.info(e.toString()));

        for (int i = 1; i < commands.length - 1; i++) {
            final String nextCommand = commands[i];
            elementsStream = processTransformCommand(elementsStream, nextCommand);
        }

        if (commands.length > 1) {
            final String terminalCommand = commands[commands.length - 1];
            return processTerminalCommand(elementsStream, terminalCommand)
                    .filter(e -> e.contains(filter))
                    .collect(toList());
        }
        return elementsStream.map(String::valueOf).collect(toList());
    }

    private Stream<String> processTerminalCommand(final Stream<Elements> elementsStream, final String command) {
        final String[] terminalCommandsList = command.split(CHILD_COMMAND_SEPARATOR);
        Stream<Element> elementStream = elementsStream.flatMap(e -> e.stream());

        for (int i = 0; i < terminalCommandsList.length; i++) {
            final String nextCommand = terminalCommandsList[i];
            if (nextCommand.contains(CHILD_COMMAND)) {
                final Integer indexOfChild = Integer.valueOf(nextCommand.replace(CHILD_COMMAND, EMPTY));
                if (i == terminalCommandsList.length - 1) {
                    return onlyValueElements(elementStream)
                            .filter(el -> !isEmpty(el.childNodes()))
                            .map(el -> el.childNode(indexOfChild).toString())
                            .map(htmlHelper::clearStringFromTrash);
                }
                elementStream = elementStream
                        .map(el -> getChild(el, indexOfChild))
                        .filter(el -> el != null);
            }
        }

        return empty();
    }

    private Stream<Element> onlyValueElements(final Stream<Element> source) {
        return source.filter(el -> el.childNodes().stream()
                .allMatch(e -> e instanceof TextNode));
    }

    private Element getChild(final Element element, final int index) {
        if (isEmpty(element.childNodes())) {
            return null;
        }
        return element.childNodes().stream().filter(e -> e instanceof Element)
                .skip(index).findFirst().map(node -> (Element) node).orElse(null);
    }

    private Stream<Elements> processTransformCommand(final Stream<Elements> elementsStream, final String command) {
        if (command.contains(CHILD_COMMAND)) {
            if (command.equals(CHILD_COMMAND)) {
                return elementsStream
                        .flatMap(e -> e.stream().map(el -> el.children()));
            }
            if (command.contains(CHILD_NUMBERED_COMMAND)) {
                final String className = command.replace(CHILD_CLASS_COMMAND, EMPTY);
                return elementsStream
                        .flatMap(e -> e.stream().map(el -> el.children())
                        .filter(el -> el.hasClass(className)));
            }
            final Integer index = Integer.valueOf(command.replace(CHILD_COMMAND, EMPTY));
            return elementsStream.flatMap(e -> e.stream()
                    .map(el -> el.child(index).children()));
        }
        return elementsStream;
    }

    private Stream<Elements> processCommandToGetElements(final Stream<Document> docStream, final String command) {
        if (command.contains(CLASS_COMMAND)) {
            final String classNames = command.replace(CLASS_COMMAND, EMPTY);
            final String[] classes = classNames.split("\\+");
            Stream<Elements> elementsStream = docStream
                    .map(d -> d.getElementsByClass(classes[0]));
            for (int i = 1; i < classes.length; i++) {
                final String classToApply = classes[i];
                elementsStream = elementsStream.filter(e -> e.hasClass(classToApply));
            }
            return elementsStream;
        }
        if (command.contains(TAG_COMMAND)) {
            final String tag = command.replace(TAG_COMMAND, EMPTY);
            return docStream.map(d -> d.getElementsByTag(tag));
        }
        return docStream.map(d -> d.getAllElements());
    }

    private Optional<Document> getDocument(final String url) {
        return Optional.ofNullable(url)
                .map(this::connect)
                .map(this::getConnection);
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
