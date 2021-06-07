package org.openmicroscopy.shoola.agents.fsimporter.mde.util;

import org.openmicroscopy.shoola.agents.fsimporter.ImporterAgent;

public class OntologyElement {
    private String val;
    private String id;
    private String uri;
    private String onto_name;


    public OntologyElement(String val, String id, String uri) {
        this.val = val;
        parseId(id);
        this.uri=uri;
        //System.out.println("\tSET OntologyElement: "+toString());
        ImporterAgent.getRegistry().getLogger().debug(null,"[MDE] -- SET OntologyElement:"+toString());
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
