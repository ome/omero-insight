5.8.2 (August 2023)
-------------------

- BugFix: LDAP check [#393](https://github.com/ome/omero-insight/pull/393)
- BugFix: display failure dialog if any [#392](https://github.com/ome/omero-insight/pull/392)
- BugFix: handle Null object when attempting to create an object [#391](https://github.com/ome/omero-insight/pull/391)
- BugFix: handle case when no object is selected [#390](https://github.com/ome/omero-insight/pull/390)
- BugFix: Pass the node and not the Data object [#389](https://github.com/ome/omero-insight/pull/389)
- BugFix: Handle root directory [#387](https://github.com/ome/omero-insight/pull/387)
- BugFix: Handle case where ROI is not longer in table [#385](https://github.com/ome/omero-insight/pull/385)
- BugFix: Replace : by _ in name on Windows [#384](https://github.com/ome/omero-insight/pull/384)
- BugFix: Handle case when project is not found [#383](https://github.com/ome/omero-insight/pull/383)
- BugFix: Adjust title in ImageJ/Fiji window [#382](https://github.com/ome/omero-insight/pull/382)
- Open image in imageJ when using as IJ plugin [#386](https://github.com/ome/omero-insight/pull/386)
- Add top-level Git mailmap to normalize commit author variants [#366](https://github.com/ome/omero-insight/pull/366)
- Enable pointing to external container xml [#365](https://github.com/ome/omero-insight/pull/365)
- Bump omero-gateway-java to 5.8.1 [#378](https://github.com/ome/omero-insight/pull/378)

5.8.1 (March 2023)
------------------

- BugFix: Fix issue with scripts menu [#341](https://github.com/ome/omero-insight/pull/341)
- BugFix: Fix a null pointer exception when loading thumbnails [#344](https://github.com/ome/omero-insight/pull/344)
- BugFix: Fix a null pointer exception in the user profile [#345](https://github.com/ome/omero-insight/pull/345)
- Use gradle-build-action [#349](https://github.com/ome/omero-insight/pull/349)
- Bump omero-gateway-java to 5.8.0

5.8.0 (December 2022)
---------------------

- Add ability to reset key in the registry [#330](https://github.com/ome/omero-insight/pull/330)
- Add logic to retrieve omero.qa.feedback property server-side [#327](https://github.com/ome/omero-insight/pull/327)
- DataServicesFactory: only log server exception class and message [#329](https://github.com/ome/omero-insight/pull/329)
- Remove joinSession method [#296](https://github.com/ome/omero-insight/pull/296)
- BugFix: Move gateway calls to OMEROGateway [#305](https://github.com/ome/omero-insight/pull/305)
- BugFix: Increase max heap for insight to 4gb [#317](https://github.com/ome/omero-insight/pull/317)
- BugFix: Don't load annotations for ROIs [#318](https://github.com/ome/omero-insight/pull/318)
- BugFix: IllegalComponentStateException: component must be showing on the screen [#313](https://github.com/ome/omero-insight/pull/313)
- BugFix: Check if the path is value [#328](https://github.com/ome/omero-insight/pull/328)
- Bump omero-gateway-java to 5.7.0 [#331](https://github.com/ome/omero-insight/pull/331)

5.7.2 (July 2022)
------------------

- Update extlinks to comply with Sphinx 5 deprecation warnings [#306](https://github.com/ome/omero-insight/pull/306)
- Hide groups if session key is used to connect [#302](https://github.com/ome/omero-insight/pull/302)
- BugFix: No image to save [#299](https://github.com/ome/omero-insight/pull/299)
- BugFix: Fix NPE if user only in system group [#298](https://github.com/ome/omero-insight/pull/298)
- BugFix: Importer handle case without tab [#297](https://github.com/ome/omero-insight/pull/297)
- BugFix: Issue 210: javax.swing.JComponent: ArrayIndexOutOfBoundsException [#295](https://github.com/ome/omero-insight/pull/295)
- Bump omero-gateway-java to 5.6.10

5.7.1 (April 2022)
------------------

- BugFix: Make sure component is showing [#290](https://github.com/ome/omero-insight/pull/290)
- Bump omero-gateway-java to 5.6.9 [#291](https://github.com/ome/omero-insight/pull/291)

5.7.0 (February 2022)
---------------------

- Simply download the OriginalFiles [#264](https://github.com/ome/omero-insight/pull/264)
- Remove loci_tools [#265](https://github.com/ome/omero-insight/pull/265)
- Update FAQ.md JVM Options for Windows [#261](https://github.com/ome/omero-insight/pull/261)
- BugFix: Use toURI method to handle [#268](https://github.com/ome/omero-insight/pull/268)
- BugFix: Handle possible null object[#267](https://github.com/ome/omero-insight/pull/267)
- BugFix: Fix NPE if no user is displayed [#266](https://github.com/ome/omero-insight/pull/266)
- BugFix: Fix potential ArrayIndexOutOfBoundsException [#257](https://github.com/ome/omero-insight/pull/257)
- MDE BugFix: Catching non-valid filehandle [#279](https://github.com/ome/omero-insight/pull/279)
- MDE BugFix: Handle empty file selection [#278](https://github.com/ome/omero-insight/pull/278)

5.6.2 (September 2021)
----------------------

- Don't throw Exception if there's no data to show [#249](https://github.com/ome/omero-insight/pull/249)
- Point to image.sc forum [#244](https://github.com/ome/omero-insight/pull/244) and [#247](https://github.com/ome/omero-insight/pull/247)
- Move UI update code in Swing thread [#240](https://github.com/ome/omero-insight/pull/240)
- Fix NPE when components is null [#238](https://github.com/ome/omero-insight/pull/238)
- Bump omero-gateway-java to 5.6.7 [#253](https://github.com/ome/omero-insight/pull/253)

5.6.1 (July 2021)
-----------------

- BugFix: Load thumbnails when read-only [#235](https://github.com/ome/omero-insight/pull/235)

5.6.0 (July 2021)
-----------------

- MDE enabled by default [#229](https://github.com/ome/omero-insight/pull/229)
- MDE feature: Allow access to read-only server [#230](https://github.com/ome/omero-insight/pull/230)
- MDE feature: Add export extension in terms of formats and ontology content [#228](https://github.com/ome/omero-insight/pull/228)


5.5.19 (June 2021)
------------------

- BugFix: Escape ice special characters for login [#227](https://github.com/ome/omero-insight/pull/227) (regression introduced in [#197](https://github.com/ome/omero-insight/pull/197))
- Bump omero-gateway-java to 5.6.6 [#232](https://github.com/ome/omero-insight/issues/232)


5.5.18 (April 2021)
-------------------

- MDE BugFix: correction of mdeConfiguration.xml example for required field [#215](https://github.com/ome/omero-insight/pull/215)
- MDE BugFix: trigger the mouse event linux [#213](https://github.com/ome/omero-insight/pull/213)


5.5.17 (March 2021)
-------------------

- BugFix: Fiji: import image not saved on disk when used on Windows [#207](https://github.com/ome/omero-insight/pull/207)

5.5.16 (February 2021)
----------------------

- Allow to pass through commandline arguments [#197](https://github.com/ome/omero-insight/pull/197)
- Build using Gradle 6 [#201](https://github.com/ome/omero-insight/pull/201)
- BugFix: Avoid potential NPEs [#202](https://github.com/ome/omero-insight/pull/202)
- BugFix: Prevent potential IllegalComponentStateException[#194](https://github.com/ome/omero-insight/pull/194)
- BugFix: Prevent to close the ImportDialog tab [#195](https://github.com/ome/omero-insight/pull/195)
- BugFix: Convert the Date column to String in the search result table [#196](https://github.com/ome/omero-insight/pull/196)
- BugFix: Catch all exceptions and use the error icon as thumbnails [#198](https://github.com/ome/omero-insight/pull/198)


5.5.15 (February 2021)
----------------------

- MDE Feature: Display the content of objects that contain required tags [#191](https://github.com/ome/omero-insight/issues/191)
- MDE Feature: Add multiselection combobox as available inputfield type [#183](https://github.com/ome/omero-insight/issues/183)
- MDE Bugfix: Use correct LookupName for mde path  [#187](https://github.com/ome/omero-insight/issues/187)
- MDE Bugfix: Fix error in creation of fields from objectDef  [#186](https://github.com/ome/omero-insight/issues/186)
- MDE Bugfix: Create textfield if given type for inputField is unknown [#185](https://github.com/ome/omero-insight/issues/185)
- MDE Bugfix: Disable filtering when no template is loaded [#179](https://github.com/ome/omero-insight/issues/179)
- Replace Travis by GitHub actions [#173](https://github.com/ome/omero-insight/issues/173), [#176](https://github.com/ome/omero-insight/issues/176) and [#200](https://github.com/ome/omero-insight/pull/200)

5.5.14 (October 2020)
---------------------

- Fix bug with null folders [#163](https://github.com/ome/omero-insight/issues/163)
- Bump omero-gateway-java to 5.6.5 [#164](https://github.com/ome/omero-insight/issues/164)

5.5.13 (July 2020)
------------------

- Bugfix: Insight picked up wrong user home directory [#156](https://github.com/ome/omero-insight/pull/156), which caused issues with viewing log files and running scripts: [#153](https://github.com/ome/omero-insight/issues/153) and [#154](https://github.com/ome/omero-insight/issues/154)

5.5.12 (July 2020)
------------------

- Provide error message if unsupported protocol was used [#113](https://github.com/ome/omero-insight/pull/113)
- Handle the case when no dataset is set during the import process [#138](https://github.com/ome/omero-insight/pull/138)
- Review logging output when used as ImageJ plugin [#136](https://github.com/ome/omero-insight/issues/136)
- Return root folder 'name' instead of empty string [#145](https://github.com/ome/omero-insight/issues/145)
- Remove files no longer needed [#144](https://github.com/ome/omero-insight/pull/144)
- Improve error message [#144](https://github.com/ome/omero-insight/pull/144)
- Add FAQ from help.openmicroscopy.org [#142](https://github.com/ome/omero-insight/pull/142)
- Handle case when callback is not returned [#143](https://github.com/ome/omero-insight/pull/143)
- Fix bug when no project is selected for a single file import [#149](https://github.com/ome/omero-insight/pull/149)
- Add feature export to csv in mde from @sukunis [#147](https://github.com/ome/omero-insight/pull/147)
- Fix bug in MapAnnotationObject in mde from @sukunis [#140](https://github.com/ome/omero-insight/pull/140)
- Bump omero-gateway-java to 5.6.4


5.5.11 (May 2020)
-----------------

- Fix DnD issue when moving other user's data [#133](https://github.com/ome/omero-insight/pull/133)
- Allow to replace existing server-side script [#134](https://github.com/ome/omero-insight/pull/134)
- New features in mde from @sukunis [#126](https://github.com/ome/omero-insight/pull/126)


5.5.10 (April 2020)
-------------------

- Fix the bin/omero-importer script [#127](https://github.com/ome/omero-insight/pull/127)
- Remove ant build system [#125](https://github.com/ome/omero-insight/pull/125)
- Add CSV to the list of the supported formats for the attachments dialog [#118](https://github.com/ome/omero-insight/pull/118)
- Build Windows executable using Travis [#115](https://github.com/ome/omero-insight/pull/115)
- Fiji plugin, use the Bio-Formats crop option when opening a large image in Fiji [#114](https://github.com/ome/omero-insight/pull/114)
- Bump omero-gateway-java to 5.6.3

5.5.9 (January 2020)
--------------------

-  Fix issue preventing to open image in ImageJ when using web sockets [#106](https://github.com/ome/omero-insight/pull/106)
-  Fix loading of icons on Windows [#104](https://github.com/ome/omero-insight/pull/104)

5.5.8 (December 2019)
---------------------

- Bump tomero-gateway-java to 5.6.1 with units fix

5.5.7 (December 2019)
---------------------

- Major new metadata feature from @sukunis [#69](https://github.com/ome/omero-insight/pull/69)
- Enable login via web sockets [#66](https://github.com/ome/omero-insight/pull/66)
- Bump omero-gateway-java to 5.6.0 with support for OMERO 5.6

5.5.6 (September 2019)
----------------------

- Cast ByteBuffer to Byte for JDK8 support [#77](https://github.com/ome/omero-insight/pull/77)

5.5.5 (August 2019)
-------------------

- Bump omero-gateway-java to 5.5.4

5.5.4 (August 2019)
-------------------

- Fix issue preventing to use the OMERO.imagej plugin in ImageJ [#65](https://github.com/ome/omero-insight/pull/65)
- Fix jar conflict when using OMERO.imagej plugin in newer version of Fiji [#72](https://github.com/ome/omero-insight/pull/72)
- Fix import freeze when importing large number of images and canceling import [#58](https://github.com/ome/omero-insight/pull/58)
- Improve error handling when a connection failure occurred during import [#62](https://github.com/ome/omero-insight/pull/62)
- Fix issue when doing a "Cut&Paste" action [#67](https://github.com/ome/omero-insight/pull/67)

5.5.3 (July 2019)
-----------------

- Fix SSL cipher issue to allow Insight to be used from Fedora 30
- Fix stack overflow exception
- Bump omero-gateway-java to 5.5.3

5.5.2 (June 2019)
-----------------

- Check for upgrade.
- Fix issue when submitting feedback.

5.5.1 (June 2019)
-----------------

- Remove script non longer required.
- Improve packaging to allow shortcut for executable.

5.5.0 (May 2019)
----------------

- Allow to import data off-line using `ome-smuggler <https://github.com/openmicroscopy/omero-ms-queue/>`_.
- Indicate in login screen that it is possible to use session ID to connect.
- Improve saving of ROIs and Measurements when using the ImageJ plugin.
- Fix ImageJ packaging.
- Reduce size of packages.
- Add tasks to package applications.
- Add Gradle build system.
- Decouple OMERO.insight.
