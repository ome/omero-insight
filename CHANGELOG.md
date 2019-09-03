5.5.7 (September 2019)
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
