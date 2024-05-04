package com.example.hackathontest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@RestController
public class HackathonRestController {
    private final JsonConverter jsonConverter = new JsonConverter();

    @GetMapping("/test")
    public String getLawVfgh(/*@RequestParam(value = "Applikation") String someParam*/) throws JsonProcessingException {
        String url = "https://data.bka.gv.at/ris/api/v2.6/Judikatur?Applikation=Justiz&Suchworte='Peter+Hauser'&Dokumenttyp.SucheInEntscheidungstexten=true&Sortierung.SortDirection=Descending&Sortierung.SortedByColumn=Datum";
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(url, String.class);
        return returnLink(response);
        //return returnLink(returnFormattedResponse(response));
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

    private String returnLink(String json){
        String jsonPath = "$['OgdSearchResult']['OgdDocumentResults']['OgdDocumentReference'][0]['Data']['Dokumentliste']['ContentReference']['Urls']['ContentUrl'][0]['Url']";
        //String jsonPath = "$['OgdSearchResult']['OgdDocumentResults']['Hits']['#text']";
        DocumentContext jsonContext = JsonPath.parse(json);
        return  jsonContext.read(jsonPath);
    }
}
