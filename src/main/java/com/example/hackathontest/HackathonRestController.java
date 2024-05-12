package com.example.hackathontest;

import com.example.hackathontest.data.Attorney;
import com.example.hackathontest.data.JustizResponse;
import com.example.hackathontest.utils.DatabaseConnector;
import com.example.hackathontest.utils.JsonConverter;
import com.example.hackathontest.utils.XmlParser;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.jdom2.Content;
import org.jdom2.Document;
import org.jdom2.Element;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@CrossOrigin(origins = "http://localhost:8080")
@RestController
public class HackathonRestController {
    private final JsonConverter jsonConverter = new JsonConverter();
    private final DatabaseConnector databaseConnector = new DatabaseConnector();
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${results.limit}")
    private int resultsLimit;

    @GetMapping("/test") //Todo return Attorney object as class
    public String getAttorney(@RequestParam(value = "Suchworte") String nameOfAttorney) throws IOException, ParserConfigurationException, SAXException {
        try {
            Attorney attorney = databaseConnector.getAttorney(nameOfAttorney.replace("'", ""));
            if (attorney == null) {
                String response = fetchRis(nameOfAttorney, 1);
                List<String> strings = returnXmlLinks(response);
                //String xmlString = restTemplate.getForObject(strings.get(0), String.class);
                //String some = xmlText(xmlString);
                List<String> listOfXmls = strings.stream().map(s -> restTemplate.getForObject(s, String.class)).toList();
                //String s = xmlText(listOfXmls.get(31));
                List<JustizResponse> listOfSpruchs = listOfXmls.stream().map(this::xmlText).toList();
                //return restTemplate.getForObject(strings.get(0), String.class);
                attorney = createAttorney(listOfSpruchs, nameOfAttorney, response);
                databaseConnector.addAttorney(attorney);
                //return jsonConverter.toJson(listOfSpruchs);
                //return s;
            }
            return jsonConverter.toJson(attorney);
        } catch (Exception e) {
            System.out.println(e);
            return "Something went wrong on the server side. We apologize for this issue.";
        }
    }

    private List<String> returnXmlLinks(String json) {
        List<String> xmlLinks = new ArrayList<>();
        String amountJsonPath = "$['OgdSearchResult']['OgdDocumentResults']['Hits']['#text']";
        DocumentContext jsonContext = JsonPath.parse(json);
        int amountOfResults = Integer.parseInt(jsonContext.read(amountJsonPath));
        if (amountOfResults > resultsLimit) {
            amountOfResults = resultsLimit;
        }
        if (amountOfResults == 1) {
            try {
                String jsonPath = "$['OgdSearchResult']['OgdDocumentResults']['OgdDocumentReference']['Data']['Dokumentliste']['ContentReference'][0]['Urls']['ContentUrl'][0]['Url']";
                xmlLinks.add(jsonContext.read(jsonPath));
            } catch (Exception e) {
                String jsonPath = "$['OgdSearchResult']['OgdDocumentResults']['OgdDocumentReference']['Data']['Dokumentliste']['ContentReference']['Urls']['ContentUrl'][0]['Url']";
                xmlLinks.add(jsonContext.read(jsonPath));
            }
        } else {
            for (int i = 0; i < amountOfResults; i++) {
                try {
                    String jsonPath = "$['OgdSearchResult']['OgdDocumentResults']['OgdDocumentReference'][" + i + "]['Data']['Dokumentliste']['ContentReference'][0]['Urls']['ContentUrl'][0]['Url']";
                    xmlLinks.add(jsonContext.read(jsonPath));
                } catch (Exception e) {
                    String jsonPath = "$['OgdSearchResult']['OgdDocumentResults']['OgdDocumentReference'][" + i + "]['Data']['Dokumentliste']['ContentReference']['Urls']['ContentUrl'][0]['Url']";
                    xmlLinks.add(jsonContext.read(jsonPath));
                }
            }
        }
        return xmlLinks;
    }

    private List<String> returnLinksToCases(String json) {
        List<String> xmlLinks = new ArrayList<>();
        String amountJsonPath = "$['OgdSearchResult']['OgdDocumentResults']['Hits']['#text']";
        DocumentContext jsonContext = JsonPath.parse(json);
        int amountOfResults = Integer.parseInt(jsonContext.read(amountJsonPath));
        if (amountOfResults > resultsLimit) {
            amountOfResults = resultsLimit;
        }
        if (amountOfResults == 1) {
            try {
                String jsonPath = "$['OgdSearchResult']['OgdDocumentResults']['OgdDocumentReference']['Data']['Metadaten']['Allgemein']['DokumentUrl']";
                xmlLinks.add(jsonContext.read(jsonPath));
            } catch (Exception e) {
                //String jsonPath = "$['OgdSearchResult']['OgdDocumentResults']['OgdDocumentReference'][" + i + "]['Data']['Dokumentliste']['ContentReference']['Urls']['ContentUrl'][0]['Url']";
                //xmlLinks.add(jsonContext.read(jsonPath));
            }
        } else {
            for (int i = 0; i < amountOfResults; i++) {
                try {
                    String jsonPath = "$['OgdSearchResult']['OgdDocumentResults']['OgdDocumentReference'][" + i + "]['Data']['Metadaten']['Allgemein']['DokumentUrl']";
                    xmlLinks.add(jsonContext.read(jsonPath));
                } catch (Exception e) {
                    //String jsonPath = "$['OgdSearchResult']['OgdDocumentResults']['OgdDocumentReference'][" + i + "]['Data']['Dokumentliste']['ContentReference']['Urls']['ContentUrl'][0]['Url']";
                    //xmlLinks.add(jsonContext.read(jsonPath));
                }
            }
        }

        return xmlLinks;
    }

    private JustizResponse xmlText(String xml) {
        String returnKopf = "";
        String returnSpruch = "";
        //Jdom
        try {
            Document xmlDocJdom = XmlParser.convertStringToXml(xml);
            Element rootElementJdom = xmlDocJdom.getRootElement();
            Element contentNutzdatenStream = (Element) rootElementJdom.getContent().stream().filter(content1 -> content1.getCType().equals(Content.CType.Element)).toList().get(1);
            List<Element> listOfAbschnitts = contentNutzdatenStream.getContent().stream().filter(content -> content.getCType().equals(Content.CType.Element)).toList().stream().map(content -> (Element) content).toList();
            Element contentAbschnittStream = listOfAbschnitts.stream().filter(element -> element.getAttribute("endnhier") != null).toList().get(0);
            List<Element> listOfContents = contentAbschnittStream.getContent().stream().map(content -> (Element) content).toList();
            Element kopf = listOfContents.stream().filter(element -> element.getAttribute("ct") != null
                            && element.getAttribute("ct").getValue().equals("kopf")
                    /*&& element.getAttribute("ct") != null
                    && element.getAttribute("typ").getValue().equals("erltext")*/
            ).toList().get(0);
            List<Element> listOfSpruchs = listOfContents.stream().filter(element -> element.getAttribute("ct") != null
                    && element.getAttribute("ct").getValue().equals("spruch")
                    && element.getAttribute("typ") != null
            ).toList();
            StringBuilder stringBuilder1 = new StringBuilder();
            listOfSpruchs.forEach(element -> stringBuilder1.append(getSpruch(element)));
            returnSpruch = stringBuilder1.toString();
            /*Element spruch = listOfContents.stream().filter(element -> element.getAttribute("ct") != null
                    && element.getAttribute("ct").getValue().equals("spruch")
                    && element.getAttribute("typ") != null
            ).toList().get(0);*/ //Todo change since method now returns only part of the Spruch. Cases where the case "aufgehoben worden ist" are not working properly.
            if (kopf.getText().isEmpty()) {
                Element inner = (Element) kopf.getContent(0);
                System.out.println(inner.getText());
                //return inner.getText();
                returnKopf = inner.getText();
            } else if (!kopf.getText().isEmpty()) {
                List<Element> innerContents = kopf.getContent().stream().filter(content -> content.getCType().equals(Content.CType.Element)).map(content -> (Element) content).toList();
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(kopf.getText());
                innerContents.forEach(element -> stringBuilder.append(element.getText()));
                //return stringBuilder.toString();
                returnKopf = stringBuilder.toString();
                //Todo investigate case 29 since it has no "Kopf" and returns undesired String
            }
            /*if(spruch.getText().isEmpty()){
                String output = spruch.getContent(0).getValue();
                System.out.println(output);
                //return output;
                returnSpruch = output;
            }
            else{
                returnSpruch = spruch.getText();
            }*/
            //return xml;
            return new JustizResponse(returnKopf, returnSpruch);
        } catch (Exception e) {
            System.out.println("Error");
            //return xml;
            return new JustizResponse("", "");
        }
    }

    private Boolean isDefense(JustizResponse justizResponse, String name) {
        List<String> listOfAttack = new ArrayList<>(List.of(
                "betreibenden",
                "klagenden",
                "antragstellenden",
                "Antragstellerin",
                "Antragstellers",
                "Gläubigerin",
                "Gläubiger"
        ));
        List<String> listOfDefense = new ArrayList<>(List.of(
                "verpflichteten",
                "beklagten",
                "Beklagten",
                "beklagte",
                "Antragsgegnerin",
                "Antragsgegner",
                "Antragsgegners",
                "Schuldners",
                "Schuldnerin",
                "Gegner",
                "Gegnerin",
                "Vertreter",
                "Vertreterin"
        ));
        String kopf = justizResponse.getKopf();

        for (String s1 : listOfAttack) {
            if (kopf.contains(s1)) {
                for (String s2 : listOfDefense) {
                    if (kopf.contains(s2)) {
                        int indexOfAttack = kopf.indexOf(s1);
                        int indexOfName = kopf.indexOf(name);
                        int indexOfDefense = kopf.indexOf(s2);
                        if (indexOfAttack < indexOfDefense) {
                            return indexOfName >= indexOfDefense;
                        } else {
                            return indexOfName <= indexOfAttack;
                        }
                        /*if (indexOfName - indexOfAttack < 70) {
                            return false;
                        } else {
                            return true;
                        }*/
                    }
                }
            }
        }
        return null;
    }

    private Boolean isWon(JustizResponse justizResponse, boolean isDefense) {
        List<String> listOfWins = new ArrayList<>(List.of(
                "stattgegeben",
                "wird Folge gegeben"
        ));
        List<String> listOfLosses = new ArrayList<>(List.of(
                "wird nicht Folge gegeben",
                "zurückgewiesen ",
                "abgewiesen"
        ));
        List<String> listOfNeutrals = new ArrayList<>(List.of(
                "eingestellt",
                "aufgehoben"
        ));

        String spruch = justizResponse.getSpruch();

        for (String s1 : listOfNeutrals) {
            if (spruch.contains(s1)) {
                return null; //Decision was cancelled, so state of case is undefined.
            }
        }
        for (String s2 : listOfWins) {
            if (spruch.contains(s2)) {
                return !isDefense; //Case won by attacking side, so if lawyer was on defensive side, he lost.
            }
        }
        for (String s3 : listOfLosses) {
            if (spruch.contains(s3)) {
                return isDefense; //Case lost by attacking side, so if lawyer was on defensive side, he won.
            }
        }
        return null; //If for some reason above-written for-loops don`t work, return null
    }

    private String getSpruch(Element spruch) {
        if (spruch.getText().isEmpty()) {
            /*List<Content> contentList = spruch.getContent().stream().filter(content -> content.getCType().equals(Content.CType.Element)).toList();
            StringBuilder s = new StringBuilder();
            contentList.forEach(content -> s.append(content.getValue()));*/
            String output = spruch.getContent(0).getValue();
            System.out.println(output);
            return output;
        } else {
            return spruch.getText();
        }
    }

    private Attorney createAttorney(List<JustizResponse> justizResponses, String nameOfAttorney, String response) {
        List<Boolean> booleans = new ArrayList<>();
        String name = nameOfAttorney.replace("'", "");
        justizResponses.forEach(justizResponse -> {
            try {
                boolean isDefense = isDefense(justizResponse, name);
                booleans.add(isWon(justizResponse, isDefense));
            }
            catch (Exception ignored){
            }
        });
        int wonCases = (int) booleans.stream().filter(Objects::nonNull).filter(aBoolean -> aBoolean).count();
        int lostCases = (int) booleans.stream().filter(Objects::nonNull).filter(aBoolean -> !aBoolean).count();
        List<String> linksToCases = returnLinksToCases(response);

        return new Attorney(name, wonCases, lostCases, linksToCases);
    }

    private String fetchRis(String nameOfAttorney, int pageNumber){
        String url = "https://data.bka.gv.at/ris/api/v2.6/Judikatur?Applikation=Justiz&Suchworte=" + nameOfAttorney + "&Dokumenttyp.SucheInEntscheidungstexten=true&Sortierung.SortDirection=Descending&Sortierung.SortedByColumn=Datum&DokumenteProSeite=OneHundred&Seitennummer=" + pageNumber;
        return restTemplate.getForObject(url, String.class);
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
