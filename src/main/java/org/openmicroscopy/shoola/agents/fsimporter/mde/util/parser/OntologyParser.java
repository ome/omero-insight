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
import org.openmicroscopy.shoola.agents.fsimporter.mde.util.OntologyElement;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import omero.log.LogMessage;

/**
 * 1/8/2021
 *
 * @author Susanne Kunis<susannekunis at gmail dot com>
 **/
public abstract class OntologyParser {
    String REST_URL;
    String acronym;
    static final ObjectMapper mapper = new ObjectMapper();

    public OntologyParser(String ontology_restapi_url) {
        this(ontology_restapi_url, "");
    }

    public OntologyParser(String ontology_restapi_url,String acronym) {
        this.acronym=acronym;
        this.REST_URL=ontology_restapi_url;
    }

    /**
     *
     * @param ontology_acronym acronym of ontology that the term is part of
     * @param termID_href href
     * @return labels of all subclasses of given termID, if subclass is a leaf
     */
    public List<OntologyElement> getSubLabels(String ontology_acronym, String termID_href) throws Exception {
        List<OntologyElement> labels=null;
        if(ontology_acronym==null || ontology_acronym.isEmpty() || termID_href==null || termID_href.isEmpty()){
            return null;
        }
        try {
            JsonNode ontology_node = getNode(formatURL(ontology_acronym, termID_href));
            labels = getSubClassLabels(ontology_node);
        }catch(Exception e){
            LogMessage msg = new LogMessage();
            msg.print(String.format("Exception while getting sub labels for %s", termID_href));
            msg.print(e);
        }
        return labels;
    }

    /**
     * Get ontology from the REST service and parse the JSON
     * @param url
     * @return
     */
    JsonNode getNode(String url) throws Exception {
        String ontology_string= get_inputStreamAsStringFromURL(url);
        JsonNode ontology= stringToJsonNode(ontology_string);

        /*if(ontology==null){
            throw new NoSuchElementException(String.format("Exception while fetching url: %s",url));
        }*/
        return ontology;
    }

    static JsonNode stringToJsonNode(String json) throws Exception {
        JsonNode root = mapper.readTree(json);
        return root;
    }

    private  String get_inputStreamAsStringFromURL(String urlToGet) {
        URL url;
        HttpURLConnection conn;
        BufferedReader rd = null;
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
        } catch (Exception e) {
            LogMessage msg = new LogMessage();
            msg.print(String.format("Exception while creating url for %s",urlToGet));
            msg.print(e);
            // to check if exception: url_restapi and api_key
            //throw new MalformedURLException(String.format("Exception while creating url for %s",urlToGet));
            // Parse from OLS produce always at first children parsing an Error - However, this has no effect on the result.
        } finally {
            try {
                if (rd != null) rd.close();
            } catch (Exception e) {}
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
    protected abstract List<OntologyElement> getSubClassLabels(JsonNode ontology_node) throws Exception;

    /**
     *
     * @param ontology_node
     * @param parentLabel
     * @return tree path labels of all subclasses of given termID, delimeter= ":"
     * @throws Exception
     */
    protected abstract List<String> getSubClassLabelsWithParents(JsonNode ontology_node, String parentLabel) throws Exception;
    protected abstract HttpURLConnection initURLConnection(URL url) throws Exception;
}
