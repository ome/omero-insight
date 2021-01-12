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
 *  doc: https://www.ebi.ac.uk/ols/docs/api
 **/
public class OLS_Parser extends OntologyParser {
    static final String REST_URL = "https://www.ebi.ac.uk/ols";

    @Override
    protected String formatURL(String ontology_acronym, String termID_href) {
        String id_utf8 = null;
        // has to be double URL encoded (see https://www.ebi.ac.uk/ols/docs/api :Term)
        // or use : "?iri="+URLEncoder.encode(termID_href, "UTF-8");
        try{
            id_utf8= "/"+ URLEncoder.encode(URLEncoder.encode(termID_href,"UTF-8"),"UTF-8");
        }catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return REST_URL+"/api/ontologies/"+ontology_acronym+"/terms"+id_utf8+"/children";
    }

    @Override
    protected ArrayList<String> getSubClassLabels(JsonNode ontology_node) {
        if(ontology_node==null){
            return null;
        }
        ArrayList<String> labels = new ArrayList<String>();
        // From the returned page, get the hypermedia link to the next page
        JsonNode embedded_node = ontology_node.get("_embedded");

        // Iterate over the available terms
        if(embedded_node!=null) {
            for (JsonNode cls : embedded_node.get("terms")) {
                // get childs
                if (!cls.get("has_children").isNull() && cls.get("has_children").asBoolean()) {
                    String childId = cls.get("_links").get("children").get("href").asText();
                    labels.addAll(getSubClassLabels(getNode(childId)));
                }else{
                    if(!cls.get("label").isNull()) {
                        labels.add(cls.get("label").asText());
                    }
                }
            }
        }
        return labels;
    }

    @Override
    protected ArrayList<String> getSubClassLabelsWithParents(JsonNode ontology_node, String parentLabel) {
        if(ontology_node==null){
            return null;
        }
        ArrayList<String> labels = new ArrayList<String>();
        // From the returned page, get the hypermedia link to the next page
        JsonNode embedded_node = ontology_node.get("_embedded");

        // Iterate over the available terms
        if(embedded_node!=null) {
            for (JsonNode cls : embedded_node.get("terms")) {
                if(!cls.get("label").isNull()){
                    labels.add(parentLabel+cls.get("label").asText());
                }
                // get childs
                if (!cls.get("has_children").isNull() && cls.get("has_children").asBoolean()) {
                    String childId = cls.get("_links").get("children").get("href").asText();
                    labels.addAll(getSubClassLabelsWithParents(getNode(childId),parentLabel + cls.get("label").asText() + ":"));
                }
            }
        }
        return labels;
    }

    @Override
    protected HttpURLConnection initURLConnection(URL url) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");
        return conn;
    }
}
