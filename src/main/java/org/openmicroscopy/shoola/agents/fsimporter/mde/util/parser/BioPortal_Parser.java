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

import com.fasterxml.jackson.databind.JsonNode;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * 1/8/2021
 *
 * @author Susanne Kunis<susannekunis at gmail dot com>
 *
 *  output inside a browser see for example: https://data.bioontology.org/ontologies/BRO/
 *  see also: https://github.com/molgenis/ontocat/blob/master/ontoCAT/src/uk/ac/ebi/ontocat/bioportal/BioportalOntologyService.java
 *  doc: https://www.bioontology.org/wiki/CTS2_BioPortal_Rest_mapping#Get_a_specific_ontology_based_on_a_version_id
 *  doc: https://data.bioontology.org/documentation
 **/
public class BioPortal_Parser extends OntologyParser {
    //static final String REST_URL = "http://data.bioontology.org";
    static final String API_KEY = "c6ae1b27-9f86-4e3c-9dcf-087e1156eabe";

    public BioPortal_Parser(String ontology_restapi_url) {
        super(ontology_restapi_url);
    }


    @Override
    protected String formatURL(String ontology_acronym, String termID_href) {
        String id_utf8 = null;
        try{
            id_utf8= URLEncoder.encode(termID_href, "UTF-8");
        }catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return REST_URL+"/ontologies/"+ontology_acronym+"/classes/"+id_utf8+"/children";
    }

    @Override
    protected ArrayList<String> getSubClassLabelsWithParents(JsonNode ontology_node, String parentLabel) throws Exception {
        if(ontology_node==null){
            return null;
        }
        ArrayList<String> labels = new ArrayList<String>();
        // From the returned page, get the hypermedia link to the next page
        String nextPage = ontology_node.get("links").get("nextPage").asText();

        // Iterate over the available pages adding labels from all classes
        // When we hit the last page, the while loop will exit
        while (nextPage.length() != 0) {
            for (JsonNode cls : ontology_node.get("collection")) {
                if (!cls.get("prefLabel").isNull()) {
                    labels.add(parentLabel+cls.get("prefLabel").asText());
                }
                if(!cls.get("links").get("children").isNull()){
                    labels.addAll(getSubClassLabelsWithParents(getNode(cls.get("links").get("children").asText()),parentLabel+cls.get("prefLabel").asText()+":"));
                }
            }

            if (ontology_node.get("totalCount").asLong()>0 && !ontology_node.get("links").get("nextPage").isNull()) {
                nextPage = ontology_node.get("links").get("nextPage").asText();
                ontology_node = getNode(nextPage);
            } else {
                nextPage = "";
            }
        }
        return labels;
    }

    @Override
    protected ArrayList<String> getSubClassLabels(JsonNode ontology_node) throws Exception {
        if(ontology_node==null){
            return null;
        }
        ArrayList<String> labels = new ArrayList<String>();
        // From the returned page, get the hypermedia link to the next page
        String nextPage = ontology_node.get("links").get("nextPage").asText();

        // Iterate over the available pages adding labels from all classes
        // When we hit the last page, the while loop will exit
        while (nextPage.length() != 0) {
            for (JsonNode cls : ontology_node.get("collection")) {
                ArrayList subNodes_label = null;
                if(!cls.get("links").get("children").isNull()){
                    subNodes_label = getSubClassLabels(getNode(cls.get("links").get("children").asText()));
                }
                if(subNodes_label== null || subNodes_label.isEmpty()) {
                    if (!cls.get("prefLabel").isNull()) {
                        labels.add(cls.get("prefLabel").asText());
                    }
                }else{
                    labels.addAll(subNodes_label);
                }
            }

            if (ontology_node.get("totalCount").asLong()>0 && !ontology_node.get("links").get("nextPage").isNull()) {
                nextPage = ontology_node.get("links").get("nextPage").asText();
                ontology_node = getNode(nextPage);
            } else {
                nextPage = "";
            }
        }
        return labels;
    }

    @Override
    protected HttpURLConnection initURLConnection(URL url) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Authorization", "apikey token=" + API_KEY);
        conn.setRequestProperty("Accept", "application/json");
        return conn;
    }
}
