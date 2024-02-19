package com.example.AtiperaRec;

import org.json.JSONArray;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

@RestController
public class Controller {

    @ResponseBody
    @GetMapping(value = "/{username}", produces = "application/json")
    public ResponseEntity<?> fetchData(@PathVariable String username) {
        ////Listowanie repozytoriów i nazwy użytkownika
        String url = "https://api.github.com/users/" + username + "/repos";
        RestTemplate restTemplate = new RestTemplate();
        //Lista nazw repozytoriów
        LinkedList<String> repoListPag = new LinkedList<>();
        String autor = "";
        //Kolekcja mapująca Listę branchy do nazw repozytorium
        HashMap<String, List<String>> repBranchMap = new HashMap<>();

        while(url != null){
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            String jsonDataPag = response.getBody();
            JSONArray jsonArrayPag = new JSONArray(jsonDataPag);

            //Pętla pobiera nazwy repozytoriów i dodaje je do listy
            for(int i = 0; i < jsonArrayPag.length(); i++) {
                for(String title : jsonArrayPag.getJSONObject(i).keySet()) {
                    if(title.equals("name")){
                        repoListPag.add(jsonArrayPag.getJSONObject(i).getString(title));
                    }
                    else if(title.equals("owner")){
                        autor = jsonArrayPag.getJSONObject(i).getJSONObject(title).getString("login");
                    }
                }
            }

            HttpHeaders headers = response.getHeaders();
            String linkHeader = headers.getFirst(HttpHeaders.LINK);

            //Petla sprawdzająca czy istnieje następna strona w paginacji
            if(linkHeader != null){
                String[] links = linkHeader.split(",");
                for (String link : links){
                    String[] parts = link.split(";");
                    if(parts.length == 2) {
                        String linkUrl = parts[0].trim().replace("<", "").replace(">", "");
                        String rel = parts[1].trim().split("=")[1].replace("\"", "");
                        if(rel.equals("first")) {
                            url = null;
                            break;
                        }
                        else if(rel.equals("next")) {
                            url = linkUrl;
                            break;
                        }
                    }
                }
            } else {
                url = null;
            }
        }


        for(String repoName : repoListPag){
            String branchUrl = "https://api.github.com/repos/" + username + "/" + repoName + "/branches";

            ResponseEntity<String> responseBranch = restTemplate.getForEntity(branchUrl, String.class);
            String jsonDataBranch = responseBranch.getBody();

            JSONArray jsonArrayBranch = new JSONArray(jsonDataBranch);
            LinkedList<String> branchList = new LinkedList<>();
            for(int i = 0; i < jsonArrayBranch.length(); i ++) {
                for(String title : jsonArrayBranch.getJSONObject(i).keySet()) {
                    if(title.equals("name")){
                        branchList.add(jsonArrayBranch.getJSONObject(i).getString(title));
                    }
                }
            }
            repBranchMap.put(repoName, branchList);
        }

        String jsonResponse = "";
        jsonResponse += "OwnerLogin: " + autor + "\n";
        for(String x : repBranchMap.keySet()){
            jsonResponse += "Repository Name: " + "\n" + x + "\n";
            jsonResponse += "Branches Name: " + "\n" + repBranchMap.get(x) + "\n";
            //Pobranie kodu sha z najnowszego commitu brancha
            String urlCommit = "https://api.github.com/repos/" + autor + "/" + x + "/commits";
            ResponseEntity<String> responseCommit = restTemplate.getForEntity(urlCommit, String.class);
            String jsonDataCommit = responseCommit.getBody();
            JSONArray jsonArrayBranch = new JSONArray(jsonDataCommit);
            jsonResponse += "Last commit: " + "\n" + jsonArrayBranch.getJSONObject(0).getString("sha");
        }

        System.out.println(jsonResponse);
        return ResponseEntity.ok(jsonResponse);
    }

}
