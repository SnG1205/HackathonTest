package com.example.hackathontest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
public class HackathonRestController {
    private final JsonConverter jsonConverter = new JsonConverter();

    @Value("${results.limit}")
    private int resultsLimit;

    @GetMapping("/test")
    public List<String> getLawVfgh(@RequestParam(value = "Suchworte") String someParam) throws JsonProcessingException {
        String url = "https://data.bka.gv.at/ris/api/v2.6/Judikatur?Applikation=Justiz&Suchworte=" + someParam + "&Dokumenttyp.SucheInEntscheidungstexten=true&Sortierung.SortDirection=Descending&Sortierung.SortedByColumn=Datum";
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(url, String.class);
        return returnLink(response);
    }

    @GetMapping("/history")
    public String getChanges(/*@RequestParam(value = "Applikation") String someParam*/) throws JsonProcessingException {
        String url = "https://data.bka.gv.at/ris/api/v2.6/History?Anwendung=Vfgh&AendurungenVon=1970-01-01&AendurungenBis=2024-01-01";
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(url, String.class);
        String finalResponse = jsonConverter.toJson(response);


        //return returnFormattedResponse(response);
        return response;
    }

    /*private String returnFormattedResponse(String response) throws JsonProcessingException {
        String convertedResponse = jsonConverter.toJson(response);
        String finalResponse = convertedResponse.replace("\\", "");
        String finalFinalResponse = finalResponse.replace("\\\\\\", "\\");
        return finalResponse.substring(1, finalFinalResponse.length()-1);
    }*/

    private List<String> returnLink(String json){ //Todo change method to return list of xml links
        List<String> xmlLinks = new ArrayList<>();
        //String jsonPath = "$['OgdSearchResult']['OgdDocumentResults']['OgdDocumentReference'][0]['Data']['Dokumentliste']['ContentReference']['Urls']['ContentUrl'][0]['Url']";
        String amountJsonPath = "$['OgdSearchResult']['OgdDocumentResults']['Hits']['#text']";
        DocumentContext jsonContext = JsonPath.parse(json);
        int amountOfResults =  Integer.parseInt(jsonContext.read(amountJsonPath));
        if (amountOfResults > resultsLimit){
            amountOfResults = resultsLimit;
        }
        for (int i = 0; i < amountOfResults; i++){
            String jsonPath = "$['OgdSearchResult']['OgdDocumentResults']['OgdDocumentReference'][" + i + "]['Data']['Dokumentliste']['ContentReference']['Urls']['ContentUrl'][0]['Url']";
            xmlLinks.add(jsonContext.read(jsonPath));
        }
        return  xmlLinks;
    }
}
