package by.kes.html.aggregator.utils;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

@Service
public class HtmlHelper {

    private final Map<String, String> replacemenets;

    {
        replacemenets = new HashMap<>();
        replacemenets.put("\\n", "");
        replacemenets.put("&laquo;", "\"");
        replacemenets.put("&raquo;", "\"");
    }

    public String clearStringFromTrash(final String source) {
        String result = source;
        for (final Entry<String, String> entry: replacemenets.entrySet()) {
            result = result.replace(entry.getKey(), entry.getValue());
        }
        return result;
    }

}
