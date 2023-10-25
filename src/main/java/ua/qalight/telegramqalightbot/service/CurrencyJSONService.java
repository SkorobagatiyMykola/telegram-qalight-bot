package ua.qalight.telegramqalightbot.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import ua.qalight.telegramqalightbot.model.CurrencyJSON;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Service
@PropertySource("application.properties")
public class CurrencyJSONService implements CurrencyService{
    private static String URL;
    @Value("${baseJSON.url}")
    public void setURL(String url) {
        CurrencyJSONService.URL = url;
    }
    private static List<CurrencyJSON> currencyJSONList = new ArrayList<>();
    @SneakyThrows
    public  String getResponse(String currency)  {
        if (currencyJSONList.isEmpty()) {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(URL))
                    .GET()
                    .build();

            HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String res = response.body().toString();
            ObjectMapper mapper = new ObjectMapper();
            TypeReference<List<CurrencyJSON>> listType = new TypeReference<List<CurrencyJSON>>() {
            };
            currencyJSONList = mapper.readValue(res, listType);
        }

        CurrencyJSON currencyJSON = getCurrencyJSON(currency);

        String result = currencyJSON != null ?
                currencyJSON.getCurrency() + " rate is " + currencyJSON.getRate() : "Check the currency name";

        return result;
    }


    private static CurrencyJSON getCurrencyJSON(String currency) {
        CurrencyJSON currencyXML = currencyJSONList.stream()
                .filter(el -> el.getCurrency().equals(currency))
                .findAny()
                .orElse(null);

        return currencyXML;
    }


}
