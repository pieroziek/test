package com.example.AtiperaRec;

import jakarta.websocket.server.PathParam;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

@RestController
public class Controller {

    //, produces = "application/json"

    @ResponseBody
    @GetMapping("/{username}")
    public ResponseEntity<?> fetchData(@PathVariable String username) {
        ////Listowanie repozytoriów i nazwy użytkownika
        String url = "https://api.github.com/users/" + username + "/repos";
        RestTemplate restTemplate = new RestTemplate();
        LinkedList<String> repoListPag = new LinkedList<>();
        String autor = "";
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
        System.out.println("Autor: " + autor);

        /*
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

        for(String x : repBranchMap.keySet()){
            System.out.println("Nazwa repo: " + x);
            System.out.println("Lista branchy: " + repBranchMap.get(x));
        }

         */


        //System.out.println("Lista repozytorium" + repoListPag);
        /*
        /////Listowanie branchy
        String branchUrl = "https://api.github.com/repos/StylingAndroid/androidx/branches";
        String jsonDataBranch = restTemplate.getForObject(branchUrl, String.class);
        JSONArray jsonArrayBranch = new JSONArray(jsonDataBranch);
        LinkedList<String> branchList = new LinkedList<>();
        for(int i = 0; i < jsonArrayBranch.length(); i ++) {
            for(String title : jsonArrayBranch.getJSONObject(i).keySet()) {
                if(title.equals("name")){
                    branchList.add(jsonArrayBranch.getJSONObject(i).getString(title));
                }
            }
        }



        System.out.println("Lista branchy:" + branchList);
        */

        //String finalRespone = "Owner Login: " + autor + "Repository Name: " + repoListPag + "Branch list:" + branchList;
        String urlCommit = "https://api.github.com/repos/LargeWorldModel/LWM/commits";
        ResponseEntity<String> responseCommit = restTemplate.getForEntity(urlCommit, String.class);
        String jsonDataCommit = responseCommit.getBody();


        System.out.println("Commity:" + jsonDataCommit);
        System.out.println(repoListPag);
        return ResponseEntity.ok(repoListPag);
    }

}
