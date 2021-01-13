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
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openmicroscopy.shoola.agents.fsimporter.ImporterAgent;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * 1/8/2021
 *
 * @author Susanne Kunis<susannekunis at gmail dot com>
 **/
public abstract class OntologyParser {
    //String REST_URL;
    static final ObjectMapper mapper = new ObjectMapper();

    /**
     *
     * @param ontology_acronym acronym of ontology that the term is part of
     * @param termID_href href
     * @return labels of all subclasses of given termID, if subclass is a leaf
     */
    public String[] getSubLabels(String ontology_acronym,String termID_href) throws Exception {
        if(ontology_acronym==null || ontology_acronym.isEmpty() || termID_href==null || termID_href.isEmpty()){
            return null;
        }
        JsonNode ontology_node = getNode(formatURL(ontology_acronym,termID_href));

        ArrayList<String> labels=getSubClassLabels(ontology_node);
        String[] result=null;
        if(labels!=null) {
            result = (String[]) labels.toArray(new String[0]);
        }

        return result;
    }

    /**
     * Get ontology from the REST service and parse the JSON
     * @param url
     * @return
     */
    JsonNode getNode(String url) throws Exception {
        String ontology_string= get_inputStreamAsStringFromURL(url);
        JsonNode ontology= stringToJsonNode(ontology_string);

        if(ontology==null){
            ImporterAgent.getRegistry().getLogger().info(this,"[MDE] Can't parse ontology from "+url);
            return null;
        }
        return ontology;
    }

    static JsonNode stringToJsonNode(String json) throws Exception {
        JsonNode root = mapper.readTree(json);
        return root;
    }

    private  String get_inputStreamAsStringFromURL(String urlToGet) {
        URL url;
        HttpURLConnection conn;
        BufferedReader rd;
        String line;
        String result = "";
        try {
            url = new URL(urlToGet);
            conn = initURLConnection(url);
            rd = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            while ((line = rd.readLine()) != null) {
                result += line;
            }
            rd.close();
        } catch (Exception e) {
            ImporterAgent.getRegistry().getLogger().warn(this,"[MDE] can't create url connection to "+urlToGet);
        }
        return result;
    }

    protected abstract String formatURL(String ontology_acronym, String termID_href);

    /**
     *
     * @param ontology_node
     * @return labels of all subclasses of given termID, if subclass is a leaf
     * @throws Exception
     */
    protected abstract ArrayList<String> getSubClassLabels(JsonNode ontology_node) throws Exception;

    /**
     *
     * @param ontology_node
     * @param parentLabel
     * @return tree path labels of all subclasses of given termID, delimeter= ":"
     * @throws Exception
     */
    protected abstract ArrayList<String> getSubClassLabelsWithParents(JsonNode ontology_node, String parentLabel) throws Exception;
    protected abstract HttpURLConnection initURLConnection(URL url) throws Exception;
}
