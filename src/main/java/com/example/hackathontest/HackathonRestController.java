package com.example.hackathontest;

import com.example.hackathontest.data.JustizResponse;
import com.example.hackathontest.utils.JsonConverter;
import com.example.hackathontest.utils.XmlParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.jdom2.Content;
import org.jdom2.Document;
import org.jdom2.Element;
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
        //String xmlString = restTemplate.getForObject(strings.get(0), String.class);
        //String some = xmlText(xmlString);
        List<String> listOfXmls = strings.stream().map(s -> restTemplate.getForObject(s, String.class)).toList();
        List<JustizResponse> listOfSpruchs = listOfXmls.stream().map(this::xmlText).toList();
        //int i = listOfSpruchs.get(0).getKopf().indexOf("vertreten durch Dr. Peter Hauser");
        /*int i = listOfSpruchs.get(0).getKopf().indexOf("vertreten");
        int i2 = listOfSpruchs.get(0).getKopf().indexOf("klagenden Partei");
        int i3 = listOfSpruchs.get(0).getKopf().indexOf("klagenden");
        int i4 = listOfSpruchs.get(0).getKopf().indexOf("beklagte");*/
        //return restTemplate.getForObject(strings.get(0), String.class);
        return jsonConverter.toJson(listOfSpruchs);
    }

    @GetMapping("/history")
    public String getChanges(/*@RequestParam(value = "Applikation") String someParam*/) throws JsonProcessingException {
        String url = "https://data.bka.gv.at/ris/api/v2.6/History?Anwendung=Vfgh&AendurungenVon=1970-01-01&AendurungenBis=2024-01-01";
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(url, String.class);
        String finalResponse = jsonConverter.toJson(response);
        StringBuilder stringBuilder = new StringBuilder();

        //return returnFormattedResponse(response);
        return response;
    }

    private List<String> returnLink(String json){
        List<String> xmlLinks = new ArrayList<>();
        String amountJsonPath = "$['OgdSearchResult']['OgdDocumentResults']['Hits']['#text']";
        DocumentContext jsonContext = JsonPath.parse(json);
        int amountOfResults =  Integer.parseInt(jsonContext.read(amountJsonPath));
        if (amountOfResults > resultsLimit){
            amountOfResults = resultsLimit;
        }
        for (int i = 0; i < amountOfResults; i++){
            try{
                String jsonPath = "$['OgdSearchResult']['OgdDocumentResults']['OgdDocumentReference'][" + i + "]['Data']['Dokumentliste']['ContentReference'][0]['Urls']['ContentUrl'][0]['Url']";
                xmlLinks.add(jsonContext.read(jsonPath));
            }
            catch (Exception e){
                String jsonPath = "$['OgdSearchResult']['OgdDocumentResults']['OgdDocumentReference'][" + i + "]['Data']['Dokumentliste']['ContentReference']['Urls']['ContentUrl'][0]['Url']";
                xmlLinks.add(jsonContext.read(jsonPath));
            }
        }
        return  xmlLinks;
    }

    private JustizResponse xmlText(String xml){
        String returnKopf = "";
        String returnSpruch = "";
        //Jdom
        try{
            Document xmlDocJdom = XmlParser.convertStringToXml(xml);
            Element rootElementJdom = xmlDocJdom.getRootElement();
            Element contentNutzdatenStream = (Element) rootElementJdom.getContent().stream().filter(content1 -> content1.getCType().equals(Content.CType.Element)).toList().get(1);
            Element contentAbschnittStream = (Element) contentNutzdatenStream.getContent().stream().filter(content -> content.getCType().equals(Content.CType.Element)).toList().get(0);
            List<Element> listOfContents = contentAbschnittStream.getContent().stream().map(content -> (Element) content).toList();
            Element kopf = listOfContents.stream().filter(element -> element.getAttribute("ct") != null && element.getAttribute("ct").getValue().equals("kopf")).toList().get(0);
            Element spruch = listOfContents.stream().filter(element -> element.getAttribute("ct") != null
                    && element.getAttribute("ct").getValue().equals("spruch")
                    && element.getAttribute("typ") != null
            ).toList().get(0); //Todo change since method now returns only part of the Spruch. Cases where the case "aufgehoben worden ist" are not working properly.
            if(kopf.getText().isEmpty()){
                Element inner = (Element) kopf.getContent(0);
                System.out.println(inner.getText());
                //return inner.getText();
                returnKopf = inner.getText();
            }
            else if(!kopf.getText().isEmpty()){
                List<Element> innerContents = kopf.getContent().stream().filter(content -> content.getCType().equals(Content.CType.Element)).map(content -> (Element) content).toList();
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(kopf.getText());
                innerContents.forEach(element -> stringBuilder.append(element.getText()));
                //return stringBuilder.toString();
                returnKopf = stringBuilder.toString();
                //Todo investigate case 29 since it has no "Kopf" and returns undesired String
            }
            if(spruch.getText().isEmpty()){
                String output = spruch.getContent(0).getValue();
                System.out.println(output);
                //return output;
                returnSpruch = output;
            }
            else{
                returnSpruch = spruch.getText();
            }
            return new JustizResponse(returnKopf, returnSpruch);
        }
        catch (Exception e){
            System.out.println("Error");
            return (JustizResponse) List.of();
        }
    }
}
