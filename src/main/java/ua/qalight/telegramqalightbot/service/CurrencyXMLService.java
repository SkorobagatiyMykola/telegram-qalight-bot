package ua.qalight.telegramqalightbot.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.vdurmont.emoji.EmojiParser;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import ua.qalight.telegramqalightbot.enums.Emoji;
import ua.qalight.telegramqalightbot.model.CurrencyJSON;
import ua.qalight.telegramqalightbot.model.CurrencyXML;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Service
@PropertySource("application.properties")
public class CurrencyXMLService implements CurrencyService {

    private static String URL;

    @Value("${baseXML.url}")
    public void setURL(String url) {
        CurrencyXMLService.URL = url;
    }

    private static List<CurrencyXML> currencyXMLList = new ArrayList<>();

    @SneakyThrows
    @Override
    public String getResponse(String currency) {
        if (currencyXMLList.isEmpty()) {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(URL))
                    .GET()
                    .build();

            HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String res = response.body().toString();

            XmlMapper mapper = new XmlMapper();
            TypeReference<List<CurrencyXML>> listType = new TypeReference<List<CurrencyXML>>() {
            };
            currencyXMLList = mapper.readValue(res, listType);
        }

        CurrencyXML currencyXML = getCurrencyXML(currency);

        String emoji = currencyXML.getCurrency().equals("USD") ? Emoji.DOLLAR.getEmoji() :
                (currencyXML.getCurrency().equals("EUR") ? Emoji.EURO.getEmoji() : Emoji.OK.getEmoji());
        String result = currencyXML != null ?
                currencyXML.getCurrency() + " rate is " + currencyXML.getRate() + emoji : "Check the currency name";

        return EmojiParser.parseToUnicode(result);
    }

    private static CurrencyXML getCurrencyXML(String currency) {
        CurrencyXML currencyXML = currencyXMLList.stream()
                .filter(el -> el.getCurrency().equals(currency))
                .findAny()
                .orElse(null);

        return currencyXML;
    }
}
