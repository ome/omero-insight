/*
 * Copyright (C) <2016-2019> University of Dundee & Open Microscopy Environment.
 * All rights reserved.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openmicroscopy.shoola.agents.fsimporter.mde.util.parser;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 1/4/2021
 *
 * @author Susanne Kunis<susannekunis at gmail dot com>
 **/

public class GetLabels_ncbo {

    static final String REST_URL = "http://data.bioontology.org";
    static final String API_KEY = "";
    static final ObjectMapper mapper = new ObjectMapper();

    public void parse() {
        ArrayList<String> labels = new ArrayList<String>();

        // Get all ontologies from the REST service and parse the JSON
        String ontologies_string = get(REST_URL + "/ontologies");
        JsonNode ontologies = jsonToNode(ontologies_string);


        System.out.println("Parse BRO");
        
        // Iterate looking for ontology with acronym BRO
        JsonNode bro = null;
        for (JsonNode ontology : ontologies) {
            if (ontology.get("acronym").asText().equalsIgnoreCase("bro")) {
                bro = ontology;
            }
        }

        // Using the hypermedia link called `classes`, get the first page
        JsonNode page = jsonToNode(get(bro.get("links").get("classes").asText()));

        // From the returned page, get the hypermedia link to the next page
        String nextPage = page.get("links").get("nextPage").asText();

        // Iterate over the available pages adding labels from all classes
        // When we hit the last page, the while loop will exit
        while (nextPage.length() != 0) {
            for (JsonNode cls : page.get("collection")) {
                if (!cls.get("prefLabel").isNull()) {
                    labels.add(cls.get("prefLabel").asText());
                }
            }

            if (!page.get("links").get("nextPage").isNull()) {
                nextPage = page.get("links").get("nextPage").asText();
                page = jsonToNode(get(nextPage));
            } else {
                nextPage = "";
            }
        }

        // Print out all the labels
        for (String label : labels) {
            System.out.println(label);
        }
    }

    private static JsonNode jsonToNode(String json) {
        JsonNode root = null;
        try {
            root = mapper.readTree(json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return root;
    }

    private static String get(String urlToGet) {
        URL url;
        HttpURLConnection conn;
        BufferedReader rd;
        String line;
        String result = "";
        try {
            url = new URL(urlToGet);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "apikey token=" + API_KEY);
            conn.setRequestProperty("Accept", "application/json");
            rd = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            while ((line = rd.readLine()) != null) {
                result += line;
            }
            rd.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
