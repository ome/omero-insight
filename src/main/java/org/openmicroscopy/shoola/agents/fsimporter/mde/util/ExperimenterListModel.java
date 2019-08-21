package org.openmicroscopy.shoola.agents.fsimporter.mde.util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.DefaultListModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import org.apache.commons.lang.SerializationUtils;

import ome.xml.model.Experimenter;
import ome.xml.model.Objective;

public class ExperimenterListModel extends DefaultListModel<String>
{
	private List<Experimenter> exp;
	
	public ExperimenterListModel()
	{
		super();
		exp= new ArrayList<Experimenter>();
		
	}
	

	public Experimenter getExperimenterAt(int index){
		Experimenter e = null;
		if(exp!=null && exp.size()>0)
			e=exp.get(index);
		
		return e;
	}
	
	public void addElement(Experimenter e)
	{
		if(exp==null){
			exp=new ArrayList<Experimenter>();
		}
		
		if(e!=null){
			String name=getExperimenterName(e);
			if(name!=null && !name.equals("") && elementExists(name)==-1){
				exp.add(e);
				this.addElement(name);
			}
		}
	}
	public void addElement(int index,Experimenter e)
	{
		if(index < exp.size()){
			String name=getExperimenterName(e);
			if(name!=null && !name.equals("")&& elementExists(name)==-1){
				exp.set(index, e);
			
				set(index,name);
			}
		}
	}
	
	public static String getExperimenterName(Experimenter e)
	{
		
		String res=null;
		if(e!=null){
			String fName= (e.getFirstName()!=null && !e.getFirstName().equals("")) ? e.getFirstName():"";
			String lName=(e.getLastName()!=null && !e.getLastName().equals("")) ? e.getLastName() : "";

			if(fName.equals(""))
				res=lName;
			else
				res=fName+" "+lName;
		}
		return res;
	}
	

	
	public List<Experimenter> getList()
	{
		List<Experimenter> list=new ArrayList<Experimenter>();
		for(int i=0; i<getSize(); i++){
			list.add(getExperimenterAt(i));
		}
		
		return list;
	}
	
	public void setList(List<Experimenter> list)
	{
		if(list!=null)
			for(int i=0; i<list.size();i++)
				System.out.println("ExpModel::setList "+list.get(i).getLastName());
		exp=list;
	}

	public void replace(int editRow, Experimenter ex) 
	{
		addElement(editRow,ex);
	}

	public void removeExperimenter(int index) {
		if(index < exp.size()){
			exp.remove(index);
			remove(index);
		}
	}
	
	public int elementExists(String item)
	{
		// test if element still exists
    	for (int i = 0; i < getSize(); i++) {
    		Experimenter e=getExperimenterAt(i);
    		String data=getExperimenterName(e);
    		if (data!=null && data.toLowerCase().equals(item.toLowerCase())) {
    			return i;
    		}
    	}
    	return -1;
	}


}
