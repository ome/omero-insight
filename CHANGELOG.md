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
- Bump to omero-gateway-java 5.6.5 [#164](https://github.com/ome/omero-insight/issues/164)

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
- Bump to omero-gateway-java 5.6.4


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
- Bump to omero-gateway-java 5.6.3

5.5.9 (January 2020)
--------------------

-  Fix issue preventing to open image in ImageJ when using web sockets [#106](https://github.com/ome/omero-insight/pull/106)
-  Fix loading of icons on Windows [#104](https://github.com/ome/omero-insight/pull/104)

5.5.8 (December 2019)
---------------------

- Bump to omero-gateway-java 5.6.1 with units fix

5.5.7 (December 2019)
---------------------

- Major new metadata feature from @sukunis [#69](https://github.com/ome/omero-insight/pull/69)
- Enable login via web sockets [#66](https://github.com/ome/omero-insight/pull/66)
- Bump to omero-gateway-java 5.6.0 with support for OMERO 5.6

5.5.6 (September 2019)
----------------------

- Cast ByteBuffer to Byte for JDK8 support [#77](https://github.com/ome/omero-insight/pull/77)

5.5.5 (August 2019)
-------------------

- Bump to omero-gateway-java 5.5.4

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
- Bump to omero-gateway-java 5.5.3

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
