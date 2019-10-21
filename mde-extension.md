OMERO.mde
---------

Extension of OMERO.importer to get an overview of available metadata provided by the selected image
container and annotate images at import step by standardized key-value templates.
Changes of metadata provided by image will be saved as key-value pairs.

Workflow example
----------------
In this example we load data from an Olympus TIRF Setup. There is no information about
the objective in the image container.

<p align="center">
  <img src="images/MDE1.PNG" width="550" >
  </p>
When you have specified your objectives for your Olympus TIRF Setup (see section Customize MDE), you can load these objectives by choosing object specification: Olympus TIRF. Now one of the objectives could be adopt from the list of available Elements or you can add manually the missing information.
   <p align="center">
  <img src="images/MDE2.PNG" width="550" >
  </p>
After the import you will find your annotation under General>Key-Value Pairs.
   <p align="center">
  <img src="images/MDE3.PNG" width="550" >
  </p>

Customize MDE
-------------
Save mdeConfiguration.xml in the directory &lt;user &gt;/omero/ to specify available objects and
how the looks like. You can edit the mdeConfiguration.xml to configure your personal MDE.

You can configure different setups for different object subsets and layouts.



mdeConfiguration.xml: Element MDEObjects (in progress)
------------------------------------------------------
First you can specify which objects are general available: 
```
    <Definitions>
	   <ObjectDef Type=<yourObjectName>>
	   	...
	   </ObjectDef>
	   ...
    </Definitions>
```

You can add a child in this element to create a new custom object with key-values as `TagData` elements
(see example object `Available InputFields` in mdeConfiguration.xml in this repository).
Please specify an insertion point for every object by defining a parent object
e.g. object OME:Detector has the insertion OME:Channel - that means that OME:Detector can only be a
sub-object of an OME:Channel object.
```
    <Parents Values="OME:Channel" />
```
Predefined are most of objects specified in the [ome schema](https://www.openmicroscopy.org/Schemas/Documentation/Generated/OME-2016-06/ome.html).

You can specify different setup to use only subsets of objects or present different layout of defined objects:
```
	<Configurations>
		<SetupConf Name=<yourSetupName>>
			...
		</SetupConf>
		...
	</Configurations>
```
For any new setup you added to the xml Configuration element you can specify which of objects
are available for this setup by add an element
```
	<ObjectConf Type=<yourObjectName>>
		...
	</ObjectConf>
``` 
and how the looks like (overwrite TagData properties by defining `TagDataProp` to hide a TagData or
change default unit).



TagData
-------
There are different editor input field types for TagData:

  <p align="center">
  <img src="images/AvailableInputFields.PNG" width="550" >
  </p>

`TextField` define like:
```
      <TagData DefaultValues=""
                Name="Tag of Type TextField"
                Type="TextField"
                Unit=""
                Value=""
                Visible="true" />
```
`TextField with unit` define like:
```
      <TagData DefaultValues=""
                Name="Tag of Type TextField with unit"
                Type="TextField"
                Unit="nm"
                Value=""
                Visible="true" />
```
`TextArea` define like:
```
      <TagData DefaultValues=""
                Name="Tag of Type TextArea"
                Type="TextArea"
                Unit=""
                Value=""
                Visible="true" />
```
`ArrayField` define like (for an array of 2 elements):
```
    <TagData DefaultValues="2"
              Name="Tag of Type ArrayField"
              Type="ArrayField"
              Unit=""
	      Value=""
              Visible="true" />
```
`ArrayField` with unit define like (for an array of 3 elements):
```
    <TagData DefaultValues="3"
              Name="Tag of Type ArrayField with unit"
              Type="ArrayField"
              Unit="s"
	      Value=""
              Visible="true" />
```
`ComboBox` define like:
```
    <TagData DefaultValues="Value1,Value2,Value3"
    	      Name="tag of Type ComboBox"
              Type="ComboBox"
              Unit=""
              Value="Value1"
              Visible="true" />
```
`TimeStamp` define like:
```
    <TagData DefaultValues=""
              Name="Tag of Type TimeStamp"
              Type="TimeStamp"
              Unit=""
              Value=""
              Visible="true" />

```
mdeConfiguration.xml: Element MDEPredefinitions 
-----------------------------------------------
Here you can specify predefined values for your object for different setups.
```
<MDEPredefinitions>
	<SetupPre Name=<yourSetupName>>
		<ObjectPre Type=<yourObjectName>>
			<TagData .../>
			...
		</ObjectPre>
		...
	</SetupPre>
	....
</MDEPredefinitions>
```
