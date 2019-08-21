package org.openmicroscopy.shoola.agents.fsimporter.mde.components.format;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import loci.formats.MetadataTools;

import org.openmicroscopy.shoola.agents.fsimporter.mde.MetaDataDialog;
import org.openmicroscopy.shoola.agents.fsimporter.mde.util.ExceptionDialog;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ome.xml.model.MapAnnotation;
import ome.xml.model.MapPair;
import ome.xml.model.primitives.Timestamp;
import omero.gateway.model.MapAnnotationData;
import omero.model.NamedValue;

/**
 * Object for sample preparation metadata like
 * <Sample namespace="">
 * 		<Preparation date="" description="" >{0,1}
 * 		<Raw material code="" description="">{0,1}
 * 		<GridBox ID="" Nr="" Type="">{0,..,n}
 * 		<GridBox ID="" Nr="" Type="">
 * 		<Observed Sample>{0,...,n}
 * 		</Observed Sample>
 * 		<Observed Sample>
 * 		</Observed Sample>
 * </Sample>
 * @author kunis
 *
 */
public class Sample 
{
	/** Logger for this class. */
//    private static Logger LOGGER = Logger.getLogger(UOSMetadataLogger.class.getName());
	 private static final org.slf4j.Logger LOGGER =
	    	    LoggerFactory.getLogger(Sample.class);
	/** preparation data **/
	private Timestamp preparationDate;
	private String preparationDescription;
	
	/** raw material data **/
	private String rawMaterialDesc;
	private String rawMaterialCode;
	
	/** sample support **/
	private GridBox gridBox;
	
	/** observed sample data**/
	private ObservedSample obSample;

	
	//-- elements labels
	public static final String SAMPLE="Sample";
	public static final String RAW="RawMaterial";
	public static final String RAW_CODE="Code";
	public static final String RAW_CODE_MAPLABEL="Raw Material Code";
	public static final String RAW_DESC="Description";
	public static final String RAW_DESC_MAPLABEL="Raw Material Desc";
	public static final String PREP="Preparation";
	public static final String PREP_DATE="Date";
	public static final String PREP_DATE_MAPLABEL="Preparation Date";
	public static final String PREP_DESCRIPTION="Description";
	public static final String PREP_DESCRIPTION_MAPLABEL="Preparation Desc";
	
	
	
	public Sample()
	{
		preparationDate=null;
		preparationDescription=null;
		rawMaterialDesc=null;
		rawMaterialCode=null;
		gridBox=null;
		obSample=null;
	}
	
	//copy constructor
	public Sample(Sample orig)
	{
		if(orig!=null){
		preparationDate=orig.preparationDate;
		preparationDescription=orig.preparationDescription;
		rawMaterialDesc=orig.rawMaterialDesc;
		rawMaterialCode=orig.rawMaterialCode;
		gridBox=new GridBox(orig.gridBox);
		obSample=new ObservedSample(orig.obSample);
		}
	}
	
	//parse string of xmlannotation object to sample object
	public Sample(Element element)
	{
		String tagName=element.getTagName();
		if(!"Sample".equals(tagName)){
			LOGGER.warn("Expecting node name of Sample - but this is "+tagName);
		}
		if(element.hasAttribute("namespace")){
			//TODO test right namespace
		}
		
		readPreparationDataFromXML(element);
		readRawMaterialDataFromXML(element);
		readGridBoxDataFromXML(element);
		readObservedSampleDataFromXML(element);
	}

	public Sample(MapAnnotation annot) 
	{
		List<MapPair> listMP=annot.getValue();
		switch (annot.getNamespace()) {
		case MetaDataDialog.NS_2016_06_07:
			parseFromXMLMapAnnotation2016_06_07(listMP);
			break;

		default:
			LOGGER.warn("[DATA] Namespace is not supported for parsing sample data");
			break;
		}
	}

	
	public Sample(MapAnnotationData anno) 
	{
		switch (anno.getNameSpace()) {
		case MetaDataDialog.NS_2016_06_07:
			parseFromOMEROMapAnnotation2016_06_07((List<NamedValue>) anno.getContent());
			break;

		default:
			LOGGER.warn("[DATA] Namespace is not supported for parsing sample data");
			break;
		}
	}

	private void parseFromOMEROMapAnnotation2016_06_07(List<NamedValue> listMP) 
	{
		ObservedSample oSample=new ObservedSample();
		GridBox g=new GridBox("", "", "");
		
		for(NamedValue obj:listMP){
			switch (obj.name) {
			case PREP_DATE_MAPLABEL:
				setPrepDate(obj.value);
				break;
			case PREP_DESCRIPTION_MAPLABEL:
				setPrepDescription(obj.value);
				break;
			case RAW_CODE_MAPLABEL:
				setRawMaterialCode(obj.value);
				break;
			case RAW_DESC_MAPLABEL:
				setRawMaterialDesc(obj.value);
				break;
			case ObservedSample.GRID_MAPLABEL:
				oSample.setGridNumber(obj.value);
				break;
			case ObservedSample.OBJECT_NUMBER_MAPLABEL:
				oSample.setObjectNumber(obj.value);
				break;
			case ObservedSample.OBJECT_TYPE_MAPLABEL:
				oSample.setObjectType(obj.value);
				break;
			case GridBox.GRID_NR_MAPLABEL:
				g.setNr(obj.value);
				break;
			case GridBox.GRID_TYPE_MAPLABEL:
				g.setType(obj.value);
				break;
			default:
				LOGGER.info("[DATA] unknown Label for Sample MapAnnotation: "+obj.name);
				break;
			}
		}
		
	}

	private void parseFromXMLMapAnnotation2016_06_07(List<MapPair> listMP)
	{
		ObservedSample oSample=new ObservedSample();
		GridBox g=new GridBox("", "", "");
		for(MapPair obj:listMP){
			switch (obj.getName()) {
			case PREP_DATE_MAPLABEL:
				setPrepDate(obj.getValue());
				break;
			case PREP_DESCRIPTION_MAPLABEL:
				setPrepDescription(obj.getValue());
				break;
			case RAW_CODE_MAPLABEL:
				setRawMaterialCode(obj.getValue());
				break;
			case RAW_DESC_MAPLABEL:
				setRawMaterialDesc(obj.getValue());
				break;
			case ObservedSample.GRID_MAPLABEL:
				oSample.setGridNumber(obj.getValue());
				break;
			case ObservedSample.OBJECT_NUMBER_MAPLABEL:
				oSample.setObjectNumber(obj.getValue());
				break;
			case ObservedSample.OBJECT_TYPE_MAPLABEL:
				oSample.setObjectType(obj.getValue());
				break;
			case GridBox.GRID_NR_MAPLABEL:
				g.setNr(obj.getValue());
				break;
			case GridBox.GRID_TYPE_MAPLABEL:
				g.setType(obj.getValue());
				break;
			default:
				LOGGER.info("[DATA] unknown Label for Sample MapAnnotation: "+obj.getName());
				break;
			}
		}
		addObservedSample(oSample);
		addGridBox(g);
	}


	private void readObservedSampleDataFromXML(Element element) {
		NodeList list;
		// parse observed sample infos
		list=element.getElementsByTagName(ObservedSample.getTagName());
		for(int i=0; i<list.getLength(); i++){
			Node node=list.item(i);
			if(node.getNodeType()==Node.ELEMENT_NODE){
				addObservedSample(new ObservedSample((Element) node));
			}
		}
	}

	private void readGridBoxDataFromXML(Element element) {
		// parse gridbox infos
		NodeList list=element.getElementsByTagName(GridBox.getGridBoxTagName());
		for (int i = 0; i < list.getLength(); i++) {
			Node node=list.item(i);
			if(node.getNodeType()==Node.ELEMENT_NODE){
				addGridBox(new GridBox((Element) node));
			}
		}
	}

	private void readRawMaterialDataFromXML(Element element)
	{
		NodeList list = element.getElementsByTagName(RAW);

		// parse preparation infos
		if(list!=null && list.getLength()>0){
			Node node=list.item(0);
			if(node.getNodeType()==Node.ELEMENT_NODE){
				if(((Element) node).hasAttribute(RAW_CODE)){
					setRawMaterialCode(String.valueOf(((Element) node).getAttribute(RAW_CODE)));
				}
				if(((Element)node).hasAttribute(RAW_DESC)){
					setRawMaterialDesc(String.valueOf(((Element)node).getAttribute(RAW_DESC)));
				}
			}
		}
	}
	
	private void readPreparationDataFromXML(Element element) 
	{
		NodeList list = element.getElementsByTagName(PREP);

		// parse preparation infos
		if(list!=null && list.getLength()>0){
			Node node=list.item(0);
			if(node.getNodeType()==Node.ELEMENT_NODE){
				if(((Element) node).hasAttribute(PREP_DATE)){
					setPrepDate(Timestamp.valueOf(String.valueOf(
							((Element) node).getAttribute(PREP_DATE))));
				}
				if(((Element)node).hasAttribute(PREP_DESCRIPTION)){
					setPrepDescription(String.valueOf(((Element)node).getAttribute(PREP_DESCRIPTION)));
				}
			}
		}
	}
	
	//-- set and getter
	public void setPrepDate(Timestamp date)
	{
		this.preparationDate=date;
	}
	
	public void setPrepDate(String date)
	{
		Timestamp timestamp=null;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		
		if(date!=null && !date.equals("")){
				timestamp = new Timestamp(date);
		}
		
		if(date!=null && timestamp!=null)
			this.preparationDate=timestamp;
	
	}
	
	public Timestamp getPrepDate()
	{
		return preparationDate;
	}
	
	public String getDateAsString()
	{
		java.sql.Timestamp timestamp=null;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		
		if(preparationDate!=null){
			try{
				
				Date parsedDate = dateFormat.parse(preparationDate.toString());
				timestamp = new java.sql.Timestamp(parsedDate.getTime());

			}catch(Exception e){//this generic but you can control another types of exception
				LOGGER.error("Wrong date format for SAMPLE preparation date");
				ExceptionDialog ld = new ExceptionDialog("Timestamp Format Error!", 
						"Wrong timestamp format",e,
						this.getClass().getSimpleName());
				ld.setVisible(true);
				return "";
			}
			return dateFormat.format(timestamp);
		}else
			return "";
	}
	
	/**
	 * @return the description of probe preparation
	 */
	public String getPrepDescription() {
		return preparationDescription;
	}

	/**
	 * @param description of the probe preparation
	 */
	public void setPrepDescription(String description) {
		this.preparationDescription = description;
	}
	
	public void setRawMaterialDesc(String tagValue) {
		this.rawMaterialDesc=tagValue;
	}
	
	public String getRawMaterialDesc()
	{
		return rawMaterialDesc;
	}
	
	public String getRawMaterialCode()
	{
		return rawMaterialCode;
	}

	public void setRawMaterialCode(String tagValue) {
		this.rawMaterialCode=tagValue;		
	}

	/**
	 * @return the gridSizeX
	 */
	public GridBox getGridBox() {
			return gridBox;
	}
	
	

//	public void addGridBoxData(String nr, String type)
//	{
//		int size=0;
//		if(gridBoxList!=null){
//			size=gridBoxList.size();
//		}else{
//			gridBoxList=new ArrayList<GridBox>();
//		}
//		int gridBoxNr = (nr!=null && !nr.equals("")) ? Integer.valueOf(nr) : -1;
//		
//		GridBox box=new GridBox(String.valueOf(size),gridBoxNr,type);
//		gridBoxList.add(box);
//		
//	}
	
	public void setGridBoxData(String nr, String type) 
	{
		if(gridBox==null)
			gridBox=new GridBox("0",nr,type);
		else{
			gridBox.setNr(nr);
			gridBox.setType(type);
		}		
	}
	
	
	
	/**
	 * @param gridSizeX the gridSizeX to set
	 */
	public void addGridBox(GridBox g) 
	{
		gridBox=g;
	}
	
	public void setGridBoxNr(String nr)
	{
		if(gridBox==null){
			gridBox=new GridBox("0",nr,"");
		}else{
			gridBox.setNr(nr);
		}
	}
	
	public void setGridBoxType(String t)
	{
		if(gridBox==null){
			gridBox=new GridBox("0",null,t);
		}else{
			gridBox.setType(t);
		}
	}
	

	public void addObservedSample(ObservedSample sample)
	{
		
		sample.setSampleID(MetadataTools.createLSID("ObservedSample", 0));
		obSample=sample;
	}
	
	public void setObservedSample(ObservedSample sample)
	{
		addObservedSample(sample);
	}
	
	
	
	public ObservedSample getObservedSample()
	{
		return obSample;
	}
	
	
	
	//-- xml
	
//	public String toXMLAnnotation()
//	{
//		StringBuffer xml=new StringBuffer("<Sample namespace=\"uos/Schemas/Additions/2016-02\">");
//		
//		xml=appendPreparation(xml);
//		appendRawMaterial(xml);
//		
//		xml.append(gridBox.toXMLAnnotation());
//		
//		for(int i=0; i<obSampleList.size(); i++){
//			xml.append(obSampleList.get(i).toXMLAnnotation());
//		}
//		
//		xml.append("</Sample>");
//		
//		return xml.toString();
//	}
	

//	public MapAnnotation toMapAnnotation()
//	{
//		MapAnnotation ma = new MapAnnotation();
////		ma.setNs(NAMESPACE);
//		ma.setDescription("Sample");
//		
//		List<MapPair> valueList=new ArrayList<MapPair>();
//		
//		valueList.add(new MapPair(PREP_DATE_MAPLABEL,getDateAsString()));
//		valueList.add(new MapPair(PREP_DESCRIPTION_MAPLABEL,getPrepDescription()));
//		valueList.add(new MapPair(RAW_CODE_MAPLABEL,getRawMaterialCode()));
//		valueList.add(new MapPair(RAW_DESC_MAPLABEL,getRawMaterialDesc()));
//		GridBox gb=getGridBox();
//		valueList.add(new MapPair(GridBox.GRID_NR_MAPLABEL,gb.getNr()));
//		valueList.add(new MapPair(GridBox.GRID_TYPE_MAPLABEL,gb.getType()));
//		List<ObservedSample> list =getObservedSampleList();
//		for(ObservedSample o: list){
//			valueList.add(new MapPair(o.GRID_MAPLABEL,o.getGridNumberX()+o.GRID_SEPARATOR+o.getGridNumberY()));
//			valueList.add(new MapPair(o.OBJECT_TYPE_MAPLABEL,o.getObjectType()));
//			valueList.add(new MapPair(o.OBJECT_NUMBER_MAPLABEL,o.getObjectNumber()));
//		}
//		
//		ma.setValue(new MapPairs(valueList));
//		
//		return ma;
//	}
	
//	public MapAnnotation appendToMapAnnotation(MapAnnotation ma)
//	{
//		List<MapPair> valueList;
//		if(ma == null){
//			 ma = new MapAnnotation();
//			 ma.setNamespace(OMEStore.CELLNANOS_NS);
//			 valueList=new ArrayList<MapPair>();
//		}else{
//			valueList=ma.getValue().getPairs();
//		}
//		
//		OMEStore.addMapPair(valueList, PREP_DATE_MAPLABEL,getDateAsString());
//		OMEStore.addMapPair(valueList,PREP_DESCRIPTION_MAPLABEL,getPrepDescription());
//		OMEStore.addMapPair(valueList,RAW_CODE_MAPLABEL,getRawMaterialCode());
//		OMEStore.addMapPair(valueList,RAW_DESC_MAPLABEL,getRawMaterialDesc());
//		GridBox gb=getGridBox();
//		OMEStore.addMapPair(valueList,GridBox.GRID_NR_MAPLABEL,gb.getNr());
//		OMEStore.addMapPair(valueList,GridBox.GRID_TYPE_MAPLABEL,gb.getType());
//		List<ObservedSample> list =getObservedSampleList();
//		for(ObservedSample o: list){
//			OMEStore.addMapPair(valueList,o.GRID_MAPLABEL,o.getGridNumberX()+o.GRID_SEPARATOR+o.getGridNumberY());
//			OMEStore.addMapPair(valueList,o.OBJECT_TYPE_MAPLABEL,o.getObjectType());
//			OMEStore.addMapPair(valueList,o.OBJECT_NUMBER_MAPLABEL,o.getObjectNumber());
//		}
//		
//		ma.setValue(new MapPairs(valueList));
//		return ma;
//	}
	
//	private StringBuffer appendPreparation(StringBuffer xml)
//	{
//		boolean writePrep=false;
//		if(preparationDate!=null || preparationDescription!=null){
//			xml.append("<"+PREP);
//			writePrep=true;
//		}
//			
//		if(preparationDate!=null){
//			xml.append(" "+PREP_DATE+"=\"");
//			xml.append(preparationDate.getValue());
//			xml.append("\"");
//		}
//		
//		if(preparationDescription!=null){
//			xml.append(" "+PREP_DESCRIPTION+"=\"");
//			xml.append(preparationDescription);
//			xml.append("\"");
//		}
//		
//		if(writePrep)
//			xml.append("/>");
//		return xml;
//	}
	
//	private StringBuffer appendRawMaterial(StringBuffer xml)
//	{
//		boolean writePrep=false;
//		if(rawMaterialCode!=null || rawMaterialDesc!=null){
//			xml.append("<"+PREP);
//			writePrep=true;
//		}
//			
//		if(rawMaterialCode!=null){
//			xml.append(" "+RAW_CODE+"=\"");
//			xml.append(rawMaterialCode);
//			xml.append("\"");
//		}
//		
//		if(rawMaterialDesc!=null){
//			xml.append(" "+RAW_DESC+"=\"");
//			xml.append(rawMaterialDesc);
//			xml.append("\"");
//		}
//		
//		if(writePrep)
//			xml.append("/>");
//		return xml;
//	}
	
	
	
	public static class GridBox
	{
		private String id;
		private String nr;
		private String type;
		
		public static String GRID="GridBox";
		public static String GRID_ID="ID";
		public static String GRID_NR="NR";
		public static final String GRID_NR_MAPLABEL="Grid Number";
		public static final String GRID_TYPE_MAPLABEL="Grid Type";
		public static String GRID_TYPE="Type";
		
		public GridBox(String id,String nr, String type)
		{
			this.id=id;
			this.nr=nr;
			this.type=type;
		}
		
		public GridBox(Element node)
		{
			id=null;
			nr=null;
			type=null;
			
			if(((Element)node).hasAttribute(GRID_ID)){
				setId(String.valueOf(((Element)node).getAttribute(GRID_ID)));
			}
			
			if(((Element) node).hasAttribute(GRID_NR)){
				setNr(String.valueOf(
						((Element) node).getAttribute(GRID_NR)));
			}
			if(((Element)node).hasAttribute(GRID_TYPE)){
				setType(String.valueOf(((Element)node).getAttribute(GRID_TYPE)));
			}
		}

		
		/**
		 * Copy constructor
		 * @param gridBox
		 */
		public GridBox(GridBox orig) 
		{
			if(orig!=null){
				this.id=orig.id;
				this.nr=orig.nr;
				this.type=orig.type;
			}
		}

		/**
		 * @return the id
		 */
		public String getId() {
			return id!=null ? id : "";
		}



		/**
		 * @param id the id to set
		 */
		public void setId(String id) {
			this.id = id;
		}


		/**
		 * @return the sizeY
		 */
		public String getNr() {
			return nr;
		}

		/**
		 * @param sizeY the sizeY to set
		 */
		public void setNr(String nr) {
			this.nr = nr;
		}

		/**
		 * @return the type
		 */
		public String getType() {
			return type!=null ? type : "";
		}

		/**
		 * @param type the type to set
		 */
		public void setType(String type) {
			this.type = type;
		}
		
		public final static String getGridBoxTagName()
		{
			return GRID;
		}
		
		private String toXMLAnnotation()
		{
			StringBuffer xml=new StringBuffer("<"+GRID);
			
			
			if(id!=null){
				xml.append(" "+GRID_ID+"=\"");
				xml.append(id);
				xml.append("\"");
			}
			
			if(nr!=null){
				xml.append(" "+GRID_NR+"=\"");
				xml.append(String.valueOf(nr));
				xml.append("\"");
			}
			if(type!=null){
				xml.append(" "+GRID_TYPE+"=\"");
				xml.append(type);
				xml.append("\"");
			}
			
			xml.append("/>");
			
			return xml.toString();
		}
		
	}

	public static boolean validAnnot(String annoNS) 
	{
		switch (annoNS) {
		case MetaDataDialog.NS_2016_06_07:
			return true;

		default:
			return false;
		}
	}



	



	



	



	
}
