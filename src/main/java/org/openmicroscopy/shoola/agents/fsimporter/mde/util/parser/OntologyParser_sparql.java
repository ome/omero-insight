package org.openmicroscopy.shoola.agents.fsimporter.mde.util.parser;
import org.openmicroscopy.shoola.agents.fsimporter.ImporterAgent;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.openmicroscopy.shoola.agents.fsimporter.mde.util.OntologyElement;
import java.util.ArrayList;
import java.util.List;

/**
 * 7/20/2023
 *
 * @author Susanne Kunis<sinukesus at gmail dot com>
 *
 **/

/*
Universal ontology parser by sparql queries
Use as default sparql endpoint http://sparql.hegroup.org/sparql/

requires dependency in build.gradle:
implementation 'org.aksw.jena-sparql-api:jena-sparql-api-core:3.17.0-1'
(see https://github.com/SmartDataAnalytics/jena-sparql-api)
 */
public class OntologyParser_sparql {
    private String default_sparqlEndpoint="http://sparql.hegroup.org/sparql/";
    public OntologyParser_sparql() {
    }

    public List<OntologyElement>getSubLabels(String ontologyAcronym, String ontologyRef){
        System.out.println("####Run query for Acronym: "+ontologyAcronym+", Ref: "+ontologyRef);
        return runSparqlQuery(ontologyRef,default_sparqlEndpoint);
    }


    // generates query, runs query and parses results
    private List<OntologyElement> runSparqlQuery(String url_term, String sparqlEndpoint){

        String queryString =generateSparqlQuery(url_term);

        QueryExecution qExec = org.apache.jena.query.QueryExecutionFactory.sparqlService(sparqlEndpoint,queryString);
        List<OntologyElement> labelList=new ArrayList<>();
        try {
            // Execute the query
            ResultSet resultSet = qExec.execSelect();

            // Process the query results
            while (resultSet.hasNext()) {
                QuerySolution solution = resultSet.nextSolution();
                String subclassLabel = solution.getLiteral("Label").getString();

                String uri = solution.getResource("subclass").getURI().toString();
                String id = getTermIDFromURI(uri);
                //System.out.println(subclassLabel + ", " + uri + ", " + id);
                labelList.add(new OntologyElement(subclassLabel, id, uri));
            }
            if(labelList.isEmpty()){
                ImporterAgent.getRegistry().getLogger().debug(null,"[MDE] "+String.format("Could not retrieve subclasses of %s",url_term));
            }
        }catch(Exception c){
            ImporterAgent.getRegistry().getLogger().debug(null,"[MDE] "+String.format("Error while retrieve subclasses from %s",url_term));
        }
        finally {
            // Close the query execution to free resources
            qExec.close();
        }
        return labelList.isEmpty()?null:labelList;
    }
    private static String getTermIDFromURI(String uri) {
        // Extract the local name or term ID from the URI
        String[] uriParts = uri.split("/");
        return uriParts[uriParts.length - 1];
    }

    // generate query to retrieve the labels of all subclasses of given ontology element
    private String generateSparqlQuery(String url_term){
        String ontologyPURL=url_term;

        String queryString =
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"+
                        "SELECT DISTINCT ?subclass (STR(?subclassLabel) AS ?Label)\n"+
                        "WHERE {\n"+
                        " ?subclass rdfs:subClassOf <"+ontologyPURL+">.\n"+
                        " ?subclass rdfs:label ?subclassLabel.\n"+
                        //" FILTER (LANG(?Label)= 'en')\n"+
                        "}";
        return queryString;
    }
}


