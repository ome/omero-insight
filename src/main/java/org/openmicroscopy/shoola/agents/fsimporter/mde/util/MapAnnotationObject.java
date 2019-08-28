package org.openmicroscopy.shoola.agents.fsimporter.mde.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.openmicroscopy.shoola.util.MonitorAndDebug;

import omero.gateway.model.MapAnnotationData;
import omero.model.MapAnnotation;
import omero.model.MapAnnotationI;
import omero.model.NamedValue;

public class MapAnnotationObject {

	private String fileName;
	private List<MapAnnotationData> mapAnnotation;

	public MapAnnotationObject(String fileName, MapAnnotationData map)
	{
		this.fileName=fileName;
		this.mapAnnotation=new ArrayList<>();
		this.mapAnnotation.add(map);
	}
	
	public MapAnnotationObject(String fileName, List<MapAnnotationData> maps)
	{
		this.fileName=fileName;
		this.mapAnnotation=maps;
	}
	
	/**
	 * Return {@link MapAnnotationObject} with key:= parent | node | tagName; value:= tagValue tagUnit
	 * @param input
	 */
	public MapAnnotationObject(HashMap<String, List<TagData>> input) {
		MapAnnotation ma = new MapAnnotationI();
		List<NamedValue> values = new ArrayList<NamedValue>();
		//input->values
		for(Map.Entry<String, List<TagData>> entry: input.entrySet()) {
			if(entry.getValue()!=null) {
				for(TagData t:entry.getValue()) {
					values.add(new NamedValue(entry.getKey()+" | "+t.getTagName(),t.getTagWholeValue()));
				}
			}
		}
		
		ma.setMapValue(values);
		MapAnnotationData res=new MapAnnotationData(ma);
		res.setDescription("MDE");
		res.setNameSpace("MDE_v1.0");
		
		this.mapAnnotation=new ArrayList<>();
		this.mapAnnotation.add(res);
	}
	
	public MapAnnotationObject(MapAnnotationObject orig)
	{
		this.fileName=orig.fileName;
		this.mapAnnotation=new ArrayList<>();
		// deep copy
		List<MapAnnotationData> origList=orig.getMapAnnotationList();
		for(MapAnnotationData m:origList){
			List<NamedValue> valuesOrig=(List<NamedValue>) m.getContent();
			MapAnnotation ma = new MapAnnotationI();
			//copy values
			List<NamedValue> values=new ArrayList<NamedValue>();
			for(NamedValue val:valuesOrig){
				values.add(new NamedValue(val.name, val.value));
			}
			ma.setMapValue(values);
			this.mapAnnotation.add(new MapAnnotationData(ma));
		}
	}
	
	
	

	public String getFileName() {
		return fileName;
	}
	
	public void setFileName(String name){
		fileName=name;
	}

	public MapAnnotationData getMapAnnotation(int i) {
		return mapAnnotation.get(i);
	}
	
	public List<MapAnnotationData> getMapAnnotationList() {
		return mapAnnotation;
	}

	static public void printMapAnnotation(MapAnnotationData map)
	{
		if(map==null)
			return;
		MonitorAndDebug.printConsole("\t PRINT MAPANNOTATIONS: ");
		
		List<NamedValue> values=(List<NamedValue>) map.getContent();
		for(NamedValue val:values){
			MonitorAndDebug.printConsole("\t\t"+ val.name+": "+val.value);
		}
	}
	
	static public void printMapAnnotations(Map<String,MapAnnotationObject> map)
	{
		for (Iterator i = map.entrySet().iterator(); i.hasNext(); ) {
			Map.Entry next = (Map.Entry)i.next();
			MonitorAndDebug.printConsole("\t PRINT MAPANNOTATION OBJECT - : "+next.getKey());
			printObject((MapAnnotationObject) next.getValue());
		}
	}
	
	static public void printObject(MapAnnotationObject o)
	{
		if(o==null)
			return;
		MonitorAndDebug.printConsole("\t file : "+o.getFileName());
		List<MapAnnotationData> list=o.getMapAnnotationList();
		int index=0;
		for(MapAnnotationData m:list){
			MonitorAndDebug.printConsole("Series_"+index++);
			printMapAnnotation(m);
		}
	}

	public void printObject() {
		printObject(this);
		
	}
	
//	/**
//	 * Merge o1 and o2. If key exists in o1 and o2 with different values, overwrite o2.
//	 * @param o1
//	 * @param o2
//	 * @return o2 with all values of o1
//	 */
//	static public MapAnnotationObject merge(MapAnnotationObject o1, MapAnnotationObject o2)
//	{
//		if(o1==null)
//			return o2;
//		if(o2==null)
//			return o1;
//		   
//		List<MapAnnotationData> listData1 = o1.getMapAnnotationList();
//		List<MapAnnotationData> listData2 = o2.getMapAnnotationList();
//		
//		//singleData
//		if(listData1.size()==listData2.size()){
//			for(int i=0; i<listData1.size();i++){
//				List<NamedValue> list1=(List<NamedValue>) listData1.get(i).getContent();
//				List<NamedValue> list2=(List<NamedValue>) listData2.get(i).getContent();
//				for(NamedValue value:list1){
//					
//				}
//			}
//		}
//		   
//		return null;
//	}

	    }
