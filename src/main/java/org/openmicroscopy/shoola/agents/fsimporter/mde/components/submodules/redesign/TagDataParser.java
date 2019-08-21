package org.openmicroscopy.shoola.agents.fsimporter.mde.components.submodules.redesign;

import java.util.ArrayList;
import java.util.List;

import org.openmicroscopy.shoola.agents.fsimporter.mde.configuration.TagNames;
import org.openmicroscopy.shoola.agents.fsimporter.mde.util.TagData;
import ome.xml.model.primitives.Timestamp;
import ome.units.UNITS;
import ome.units.quantity.Length;
import ome.units.quantity.Time;
import ome.units.unit.Unit;

public class TagDataParser {
	
	public static TagData createStringData(String parent,String value, String name,boolean prop, TagData elem)
	{
		if(elem==null)
			return new TagData(parent,name,value,prop,TagData.TEXTFIELD);
		else {
			elem.setTagValue(value,prop);
			return elem;
		}
	}
	
	public static TagData createTimestampData(String parent,Timestamp value, String name,boolean prop, TagData elem)
	{
		String val= (value != null) ? value.getValue():"";
		if(elem==null)
			return new TagData(parent,name,val,prop,TagData.TIMESTAMP);
		else {
			elem.setTagValue(val,prop);
			return elem;
		}
	}
	
	public static TagData createTimeData(String parent,Time value, Unit defaultUnit,String name,boolean prop, TagData elem)
	{
		ome.model.units.Time val=null;
		if(value!=null)
			val=new ome.model.units.Time(value);
		
//		String val= (value != null) ? String.valueOf(value.value()) :"";
//		Unit unit=(value!=null) ? value.unit() : defaultUnit;
		if(elem==null)
			return new TagData(parent,name,val,ome.model.units.Time.class,prop,TagData.TEXTFIELD);
		else {
			elem.setTagValue(val,ome.model.units.Time.class,prop);
			return elem;
		}
	}
	
	public static TagData createStringArrayData(String parent,String[] value, String name,boolean prop, TagData elem)
	{
		if(elem==null)
			return new TagData(parent,name,value,prop,TagData.ARRAYFIELDS);
		else {
			for(int i=0; i<value.length;i++) {
				elem.setTagValue(value[i],i,prop);
			}
			return elem;
		}
	}
	
//	public static TagData createLengthArrayData(Length[] value,Unit defaultUnit,String name,boolean prop, TagData elem)
//	{
////		List<String> list=new ArrayList<String>();
////		for(Length l:value) {
////			String val=(l!=null)?String.valueOf(l.value()):"";
////			list.add(val);
////		}
////		Unit unit=(value[0]!=null) ? value[0].unit() : defaultUnit;
//		ome.model.units.Unit[] val=new ome.model.units.Unit[value.length];
//		for(int i=0; i<value.length; i++) {
//			val[i]=new ome.model.units.Length(value[i]);
//		}
//		if(elem==null)
//			return new TagData(name,val,ome.model.units.Length.class,prop,TagData.ARRAYFIELDS);
//		else {
//			for(int i=0; i<val.length;i++) {
//				elem.setTagValue(val[i],i,prop);
//			}
//			elem.setTagUnit(defaultUnit);
//			return elem;
//		}
//	}

}
