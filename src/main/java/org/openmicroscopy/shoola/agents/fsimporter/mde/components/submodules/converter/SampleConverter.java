package org.openmicroscopy.shoola.agents.fsimporter.mde.components.submodules.converter;

import java.util.LinkedHashMap;
import loci.formats.MetadataTools;
import ome.xml.model.primitives.Timestamp;

import org.openmicroscopy.shoola.agents.fsimporter.mde.components.format.Sample;
import org.openmicroscopy.shoola.agents.fsimporter.mde.configuration.TagNames;
import org.openmicroscopy.shoola.agents.fsimporter.mde.util.TagData;

public class SampleConverter extends DataConverter{

	public SampleConverter(){}

	public LinkedHashMap<String, TagData> convertData(Sample sample) 
	{
		tagMap=new LinkedHashMap<String,TagData>();
		if(sample!=null) {
			try{ tagMap.put(TagNames.PREPDESC,convertPreparationDescription(sample.getPrepDescription(), REQUIRED));}
			catch(NullPointerException e){
				tagMap.put(TagNames.PREPDESC,convertPreparationDescription(null, REQUIRED));
			}

			try{ tagMap.put(TagNames.PREPDATE,convertPreparationDate(sample.getPrepDate(), REQUIRED));}
			catch(NullPointerException e){
				tagMap.put(TagNames.PREPDATE,convertPreparationDate("", REQUIRED));
			}

			try{ tagMap.put(TagNames.RAWCODE,convertRawMaterialCode(sample.getRawMaterialCode(), OPTIONAL));}
			catch(NullPointerException e){
				tagMap.put(TagNames.RAWCODE,convertRawMaterialCode(null, OPTIONAL));
			}

			try{ tagMap.put(TagNames.RAWDESC,convertRawMaterialDesc(sample.getRawMaterialDesc(), REQUIRED));}
			catch(NullPointerException e){
				tagMap.put(TagNames.RAWDESC,convertRawMaterialDesc(null, REQUIRED));
			}

			try{ tagMap.put(TagNames.GRIDBOXNR,convertGridBoxNumber(sample.getGridBox().getNr(), REQUIRED));}
			catch(NullPointerException e){
				tagMap.put(TagNames.GRIDBOXNR,convertGridBoxNumber(null, REQUIRED));
			}


			try{
				String[] n={sample.getObservedSample().getGridNumberX(),
						sample.getObservedSample().getGridNumberY()};

				tagMap.put(TagNames.EXPGRID,convertExpGridNumber(n, REQUIRED));

			}catch(NullPointerException e){
				tagMap.put(TagNames.EXPGRID,convertExpGridNumber(null, REQUIRED));
			}


			try{ tagMap.put(TagNames.EXPOBJTYPE,convertExpObjectType(sample.getObservedSample().getObjectType(), REQUIRED));}
			catch(NullPointerException e){
				tagMap.put(TagNames.EXPOBJTYPE,convertExpObjectType(null, REQUIRED));
			}

			try{ tagMap.put(TagNames.EXPOBJNR,convertExpObjectNr(sample.getObservedSample().getObjectNumber(), REQUIRED));}
			catch(NullPointerException e){
				tagMap.put(TagNames.EXPOBJNR,convertExpObjectNr(null, REQUIRED));
			}
		}else {
			tagMap.put(TagNames.PREPDESC,convertPreparationDescription(null, REQUIRED));
			tagMap.put(TagNames.PREPDATE,convertPreparationDate("", REQUIRED));
			tagMap.put(TagNames.RAWCODE,convertRawMaterialCode(null, OPTIONAL));
			tagMap.put(TagNames.RAWDESC,convertRawMaterialDesc(null, REQUIRED));
			tagMap.put(TagNames.GRIDBOXNR,convertGridBoxNumber(null, REQUIRED));
			tagMap.put(TagNames.EXPGRID,convertExpGridNumber(new String[] {null,null}, REQUIRED));
			tagMap.put(TagNames.EXPOBJTYPE,convertExpObjectType(null, REQUIRED));
			tagMap.put(TagNames.EXPOBJNR,convertExpObjectNr(null, REQUIRED));
		}
		return tagMap;
	}




	/*------------------------------------------------------
	 * Set methods data Values
	 * -----------------------------------------------------*/
	private TagData convertRawMaterialDesc(String value, boolean prop) 
	{
		return new TagData(TagNames.ELEM_SAMPLE,TagNames.RAWDESC,value,prop,TagData.TEXTAREA);
	}

	private TagData convertRawMaterialCode(String value, boolean prop) 
	{
		return new TagData(TagNames.ELEM_SAMPLE,TagNames.RAWCODE,value,prop,TagData.TEXTFIELD);
	}

	private TagData convertPreparationDate(Timestamp value, boolean prop)
	{

		String val= (value != null) ? value.getValue():"";
		return new TagData(TagNames.ELEM_SAMPLE,TagNames.PREPDATE,val,prop,TagData.TIMESTAMP);
	}

	private TagData convertPreparationDate(String value, boolean prop)
	{

		String val= (value != null) ? value:"";
		return new TagData(TagNames.ELEM_SAMPLE,TagNames.PREPDATE,val,prop,TagData.TIMESTAMP);
	}

	private TagData convertPreparationDescription(String value, boolean prop)
	{
		return new TagData(TagNames.ELEM_SAMPLE,TagNames.PREPDESC,value,prop,TagData.TEXTAREA);
	}

	private TagData convertGridBoxNumber(String string, boolean prop)
	{
		String val=(string!=null) ? String.valueOf(string):"";
		return new TagData(TagNames.ELEM_SAMPLE,TagNames.GRIDBOXNR,val,prop,TagData.TEXTFIELD);
	}



	private TagData convertExpGridNumber(String[] value, boolean prop)
	{
		return new TagData(TagNames.ELEM_SAMPLE,TagNames.EXPGRID,value,prop,TagData.ARRAYFIELDS);

	}

	private TagData convertExpObjectNr(String value, boolean prop)
	{
		return new TagData(TagNames.ELEM_SAMPLE,TagNames.EXPOBJNR,value,prop,TagData.TEXTFIELD);
	}

	private TagData convertExpObjectType(String value, boolean prop)
	{
		TagData t = new TagData(TagNames.ELEM_SAMPLE,TagNames.EXPOBJTYPE,value,prop,TagData.TEXTAREA);
		
		return t;
	}

}
