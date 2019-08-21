package org.openmicroscopy.shoola.agents.fsimporter.mde.components.submodules.redesign;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.openmicroscopy.shoola.agents.fsimporter.mde.MDEHelper;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.ModuleContent;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.view.ContentViewer;
import org.openmicroscopy.shoola.agents.fsimporter.mde.components.view.ModuleContentTableModel;


/**
 * TODO: Allgemeiner fuer tagDataListen
 * @author Kunis
 *
 * @param <T>
 */
public class ObjectTable extends JPanel{
//TODO extends JPanel unessesarry after clean up
	private List<ModuleContent> availableObjects;
	private int currentSelection;
	
	public ObjectTable(List<ModuleContent> availableElems )
	{
		if(availableElems!=null) {
			this.availableObjects=availableElems;
		}
		currentSelection=-1;
	}
	
	public void setCurrentSelected(int index) {
		this.currentSelection=index;
	}
	/**
	 * 
	 * @param c
	 * @return index of element that is equal with given elem
	 */
	public int getElementIndex(ModuleContent elem) {
		int idx=0;
		
		if(availableObjects!=null && elem.getList()!=null) {
			for(ModuleContent c:availableObjects) {
				if(MDEHelper.isEqual(c, elem)) {
					System.out.println("--getElemIndex selected element "+idx+", "+elem.getType()+" [ObjectTable::getElementIndex]");
					return idx;
				}
				idx++;
			}
		}
		System.out.println("--getElementIndex not found ("+elem.getType()+")");
		return -1;
	}
	
	
	protected JTable getObjectTable() {
		JTable table=new JTable();
		ModuleContentTableModel dataModel = null;
		if(availableObjects!=null && availableObjects.size()>0){
			dataModel =new ModuleContentTableModel(availableObjects.get(0).getIdents());
			for(int i=0; i<availableObjects.size(); i++){
				dataModel.addRow(availableObjects.get(i));
			}
		}
		if(dataModel!=null) {
			table.setModel(dataModel);
			if(currentSelection!=-1) {
				table.setRowSelectionInterval(currentSelection, currentSelection);
				System.out.println("#### Table current element "+currentSelection+", selected: "+table.getSelectedRow());
			}else {
				System.out.println("#### Table current element is -1");
			}
		}
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setPreferredScrollableViewportSize(new Dimension(table.getWidth(), 60));
		table.setFillsViewportHeight(true);
		
		return table;
	}
	
	public JPanel buildGUI(int selectedIndex,final ContentViewer contentViewer)
	{
		JPanel panel=new JPanel();
		panel.setLayout(new BorderLayout());
		this.currentSelection=selectedIndex;
		
		JPanel pane=new JPanel();
		pane.setLayout(new BorderLayout(5, 5));
		pane.setBorder(new EmptyBorder(0,0,0,0));

		JLabel label = new JLabel("Available Elements:");
		label.setFont(new Font("Tahoma", Font.BOLD, 11));

		JTable table = getObjectTable();
		
		
		addListSelectionListener(contentViewer,table);
		JScrollPane scrollPane = new JScrollPane(table);
		
		
		pane.add(label,BorderLayout.NORTH);
		pane.add(scrollPane, BorderLayout.CENTER);
		
		panel.add(pane,BorderLayout.CENTER);
		
//		revalidate();
//		repaint();
		return panel;
	}
	
	private void addListSelectionListener(final ContentViewer contentViewer,JTable table) {
		ListSelectionListener listener=new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				//find out which elem is selected
				if(e.getValueIsAdjusting()) {
					ListSelectionModel lsm= (ListSelectionModel)e.getSource();
					if(lsm ==null || lsm.isSelectionEmpty())
						return;
					int idx=-1;
					if(lsm.isSelectedIndex(e.getFirstIndex())) {
						idx=e.getFirstIndex();
					}else if(lsm.isSelectedIndex(e.getLastIndex())) {
						idx=e.getLastIndex();
					}
					if(idx>-1 && idx<availableObjects.size()) {
						System.out.println("-- replace "+currentSelection+" with table element "+idx);
						ModuleContent c=availableObjects.get(idx);
						ModuleContent origContent=null;
						if(currentSelection!=-1) origContent=availableObjects.get(currentSelection);
						currentSelection=idx;
						contentViewer.replaceData(c,origContent);
					}
				}
			}
		};
		
//		ListSelectionListener listener=null;
//		if(type.equals(LightSource.class)) {
//			listener=new ListSelectionListener() {
//				@Override
//				public void valueChanged(ListSelectionEvent e) {
//					System.out.println("-- ListSelectionEvent: "+e.toString());
//					LightSourceConverter converter = new LightSourceConverter();
//					if(e.getValueIsAdjusting() && e.getLastIndex()< availableObjects.size()) {
//						converter.convertData(((LightSource)availableObjects.get(e.getLastIndex())), null);
//						viewer.replaceData(converter.getTagList(),false);
//					}
//
//				}
//			};
//		}else if( type.equals(Detector.class)) {
//			listener=new ListSelectionListener() {
//				@Override
//				public void valueChanged(ListSelectionEvent e) {
//					System.out.println("-- ListSelectionEvent: "+e.toString());
//					DetectorConverter converter = new DetectorConverter();
//					if(e.getValueIsAdjusting() && e.getLastIndex()< availableObjects.size()) {
//						converter.convertData(((Detector)availableObjects.get(e.getLastIndex())), null);
//						viewer.replaceData(converter.getTagList(),false);
//					}
////					System.out.println("-- Select objective: "+table.getValueAt(table.getSelectedRow(), 1).toString());
//				}
//			};
//		}else if(type.equals(Objective.class)) {
//			listener=new ListSelectionListener() {
//				@Override
//				public void valueChanged(ListSelectionEvent e) {
//					System.out.println("-- ListSelectionEvent: "+e.toString());
//					ObjectiveConverter converter = new ObjectiveConverter();
//					if(e.getValueIsAdjusting() && e.getLastIndex()< availableObjects.size()) {
//						converter.convertData(((Objective)availableObjects.get(e.getLastIndex())), null);
//						viewer.replaceData(converter.getTagList(),false);
//					}
////					System.out.println("-- Select objective: "+table.getValueAt(table.getSelectedRow(), 1).toString());
//				}
//			};
//		}
		
		if(table!=null)
			table.getSelectionModel().addListSelectionListener(listener);
		
	}
}
