package com.example.hackathontest;

import com.example.hackathontest.data.Attorney;
import com.example.hackathontest.data.JustizResponse;
import com.example.hackathontest.utils.JsonConverter;
import com.example.hackathontest.utils.XmlParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.jdom2.Content;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.filter.ElementFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@CrossOrigin(origins = "http://localhost:8080")
@RestController
public class HackathonRestController {
    private final JsonConverter jsonConverter = new JsonConverter();

    @Value("${results.limit}")
    private int resultsLimit;

    @GetMapping("/test")
    public String getLawVfgh(@RequestParam(value = "Suchworte") String someParam) throws IOException, ParserConfigurationException, SAXException {
        String url = "https://data.bka.gv.at/ris/api/v2.6/Judikatur?Applikation=Justiz&Suchworte=" + someParam + "&Dokumenttyp.SucheInEntscheidungstexten=true&Sortierung.SortDirection=Descending&Sortierung.SortedByColumn=Datum&DokumenteProSeite=OneHundred";
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(url, String.class);
        List<String> strings = returnLink(response);
        List<String> listOfXmls = strings.stream().map(s -> restTemplate.getForObject(s, String.class)).collect(Collectors.toList());
        List<JustizResponse> listOfSpruchs = listOfXmls.stream().map(this::xmlText).collect(Collectors.toList());
        listOfSpruchs.forEach(System.out::println);

        return jsonConverter.toJson(listOfSpruchs);
    }

    private List<String> returnLink(String json) {
        List<String> xmlLinks = new ArrayList<>();
        String amountJsonPath = "$['OgdSearchResult']['OgdDocumentResults']['Hits']['#text']";
        DocumentContext jsonContext = JsonPath.parse(json);
        int amountOfResults = Integer.parseInt(jsonContext.read(amountJsonPath));
        if (amountOfResults > resultsLimit) {
            amountOfResults = resultsLimit;
        }
        for (int i = 0; i < amountOfResults; i++) {
            try {
                String jsonPath = "$['OgdSearchResult']['OgdDocumentResults']['OgdDocumentReference'][" + i + "]['Data']['Dokumentliste']['ContentReference'][0]['Urls']['ContentUrl'][0]['Url']";
                xmlLinks.add(jsonContext.read(jsonPath));
            } catch (Exception e) {
                String jsonPath = "$['OgdSearchResult']['OgdDocumentResults']['OgdDocumentReference'][" + i + "]['Data']['Dokumentliste']['ContentReference']['Urls']['ContentUrl'][0]['Url']";
                xmlLinks.add(jsonContext.read(jsonPath));
            }
        }
        return xmlLinks;
    }

    private JustizResponse xmlText(String xml) {
        String returnKopf = "";
        String returnSpruch = "";
        Attorney returnAttorney = new Attorney();
        int wonCases = 0;
        int lostCases = 0;

        try {
            Document xmlDocJdom = XmlParser.convertStringToXml(xml);
            Element rootElementJdom = xmlDocJdom.getRootElement();
            Element nutzdaten = rootElementJdom.getChild("nutzdaten", rootElementJdom.getNamespace());

            if (nutzdaten != null) {
                List<Element> absatzElements = new ArrayList<>();
                for (Element element : nutzdaten.getDescendants(new ElementFilter("absatz"))) {
                    absatzElements.add(element);
                }

                for (Element absatz : absatzElements) {
                    String ctValue = absatz.getAttributeValue("ct");
                    if ("kopf".equals(ctValue)) {
                        returnKopf = absatz.getText();
                        if (returnKopf.contains("vertreten durch")) {
                            String[] parts = returnKopf.split("vertreten durch");
                            if (parts.length > 1) {
                                String[] nameParts = parts[1].split(",");
                                returnAttorney.setName(nameParts[0].trim());
                            }
                        }
                    } else if ("spruch".equals(ctValue)) {
                        returnSpruch = absatz.getText();
                    }

                    // Beispielhafte Logik, um die gewonnenen und verlorenen Fälle zu zählen
                    if (absatz.getText().contains("stattgegeben") || absatz.getText().contains("zulässig")) {
                        wonCases++;
                    } else if (absatz.getText().contains("unzulässig") || absatz.getText().contains("zurückgewiesen") ||
                            absatz.getText().contains("Revisionsrekurs ist nicht zulässig") || absatz.getText().contains("abgewiesen") || absatz.getText().contains("nicht Folge gegeben.")) {
                        lostCases++;
                    }
                }
            }

            returnAttorney.setWonCases(wonCases);
            returnAttorney.setLostCases(lostCases);

            // Debugging-Ausgabe
            System.out.println("Kopf: " + returnKopf);
            System.out.println("Spruch: " + returnSpruch);
            System.out.println("Attorney: " + returnAttorney.getName() + ", Won Cases: " + returnAttorney.getWonCases() + ", Lost Cases: " + returnAttorney.getLostCases());

            return new JustizResponse(returnKopf, returnSpruch, returnAttorney);
        } catch (Exception e) {
            System.out.println("Error parsing XML: " + e.getMessage());
            return new JustizResponse("", "", new Attorney());
        }
    }





    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("http://localhost:8080");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
