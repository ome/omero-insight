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
package org.openmicroscopy.shoola.agents.fsimporter.mde.util;

import org.openmicroscopy.shoola.agents.fsimporter.ImporterAgent;

/**
 * 6/5/2021
 * @author Susanne Kunis<susannekunis at gmail dot com>
 */
public class OntologyElement {
    private String val;
    private String id;
    private String uri;
    private String onto_name;


    public OntologyElement(String val, String id, String uri) {
        this.val = val;
        parseId(id);
        this.uri=uri;
        //ImporterAgent.getRegistry().getLogger().debug(this,"[MDE] -- create OntologyElement:"+toString());
    }

    /**
     * id=<onto_name>_<id>
     * @param id
     */
    private void parseId(String accession) {
        if(accession==null){
            this.id="";
            this.onto_name="";
            return;
        }
        int brkIdx = -1;
        brkIdx = accession.indexOf("_");

        // OMIM has special id like MTHU004886
        if(brkIdx==-1){
            this.id=accession;
            this.onto_name="";
        }else{
            this.id=accession.substring(brkIdx+1,accession.length());
            this.onto_name=accession.substring(0,brkIdx);
        }

    }

    public String getId(){return id;}
    public String getUri(){return uri;}
    public String getOntologyName(){return onto_name;}
    public String toString(){
        return String.format("{name:%s,id:%s,uri:%s}",getName(),getAccession(),getUri());
    }

    public String getName() {return val;  }

    public String getAccession() {
        if(getOntologyName().equals(""))
            return getId();
        return String.format("%s_%s",getOntologyName(),getId());
    }
}
