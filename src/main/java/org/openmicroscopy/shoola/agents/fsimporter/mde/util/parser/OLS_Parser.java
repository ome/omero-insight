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
import org.openmicroscopy.shoola.agents.fsimporter.ImporterAgent;
import org.openmicroscopy.shoola.agents.fsimporter.mde.util.OntologyElement;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * 1/8/2021
 *
 * @author Susanne Kunis<susannekunis at gmail dot com>
 *
 *  doc: https://www.ebi.ac.uk/ols/docs/api
 **/
public class OLS_Parser extends OntologyParser {
    //static final String REST_URL = "https://www.ebi.ac.uk/ols";

    public OLS_Parser(String ontology_restapi_url) {
        this(ontology_restapi_url, "");
    }

    public OLS_Parser(String ontology_restapi_url,String acronym) {
        super(ontology_restapi_url,acronym);
    }

    @Override
    protected String formatURL(String ontology_acronym, String termID_href) {
        String id_utf8 = null;
        // has to be double URL encoded (see https://www.ebi.ac.uk/ols/docs/api :Term)
        // or use : "?iri="+URLEncoder.encode(termID_href, "UTF-8");
        try{
            id_utf8= "/"+ URLEncoder.encode(URLEncoder.encode(termID_href,"UTF-8"),"UTF-8");
        }catch (UnsupportedEncodingException e) {
            ImporterAgent.getRegistry().getLogger().warn(this,"[MDE] Encoding error for ID_href");
            return null;
        }
        return REST_URL+"/api/ontologies/"+ontology_acronym+"/terms"+id_utf8+"/children";
    }

    @Override
    protected List<OntologyElement> getSubClassLabels(JsonNode ontology_node) throws Exception {
        if(ontology_node==null){
            return null;
        }

        List<OntologyElement> labels = new ArrayList<OntologyElement>();
        try{
            // From the returned page, get the hypermedia link to the next page
            JsonNode embedded_node = ontology_node.get("_embedded");

            // Iterate over the available terms
            if(embedded_node!=null) {
                for (JsonNode cls : embedded_node.get("terms")) {

                    // get childs
                    if (!cls.get("has_children").isNull() && cls.get("has_children").asBoolean()) {
                        String childId = cls.get("_links").get("children").get("href").asText();
                        labels.addAll(getSubClassLabels(getNode(childId)));
                    }else {
                        try {
                            if (cls != null) {
                                OntologyElement oElem = new OntologyElement(cls.get("label").asText(),
                                        cls.get("short_form").asText(),
                                        cls.get("ontology_iri").asText());
                                labels.add(oElem);

                            }
                        }catch(Exception e){
                            throw new UnsupportedEncodingException("Error while parsing from OLS: "+cls.get("label").asText());
                        }
                    }
                }
            }
        }catch(Exception e){
            throw new UnsupportedEncodingException(
                    String.format("Exception while parsing from %s: %s",this.REST_URL,this.acronym));
        }
        return labels;
    }

    @Override
    protected List<String> getSubClassLabelsWithParents(JsonNode ontology_node, String parentLabel) throws Exception {
        if(ontology_node==null){
            return null;
        }
        List<String> labels = new ArrayList<String>();
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
