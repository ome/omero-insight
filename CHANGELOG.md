5.5.4 (August 2019)
-------------------

- Fix issue preventing to use the OMERO.imagej plugin in ImageJ
- Fix jar conflict when using OMERO.imagej plugin in newer version of Fiji
- Fix import freeze when importing large number of images and cancelling import
- Improve error handling when a connection failure occurred during import
- Fix issue when doing a "Cut&Paste" action.

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
