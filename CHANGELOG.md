# CHANGELOG

## [5.5.13]

### Fixed
- Insight picked up wrong user home directory [#156](https://github.com/ome/omero-insight/pull/156), which caused issues with viewing log files and running scripts: [#153](https://github.com/ome/omero-insight/issues/153) and [#154](https://github.com/ome/omero-insight/issues/154)

## [5.5.12]
### Fixed
- Fix bug when no project is selected for a single file import [#149](https://github.com/ome/omero-insight/pull/149)
- Handle case when callback is not returned [#143](https://github.com/ome/omero-insight/pull/143)
- Fix bug in MapAnnotationObject in mde from @sukunis [#140](https://github.com/ome/omero-insight/pull/140)

### Removed
- Remove files no longer needed [#144](https://github.com/ome/omero-insight/pull/144)

### Changed
- Improve error message [#144](https://github.com/ome/omero-insight/pull/144)
- Return root folder 'name' instead of empty string [#145](https://github.com/ome/omero-insight/issues/145)
- Handle the case when no dataset is set during the import process [#138](https://github.com/ome/omero-insight/pull/138)
- Review logging output when used as ImageJ plugin [#136](https://github.com/ome/omero-insight/issues/136)
- Bump to omero-gateway-java 5.6.4

### Added
- Add FAQ from help.openmicroscopy.org [#142](https://github.com/ome/omero-insight/pull/142)
- Add feature export to csv in mde from @sukunis [#147](https://github.com/ome/omero-insight/pull/147)
- Provide error message if unsupported protocol was used [#113](https://github.com/ome/omero-insight/pull/113)


## [5.5.11]
### Fixed
- Fix DnD issue when moving other user's data [#133](https://github.com/ome/omero-insight/pull/133)

### Added
- Allow to replace existing server-side script [#134](https://github.com/ome/omero-insight/pull/134)
- New features in mde from @sukunis [#126](https://github.com/ome/omero-insight/pull/126)

## [5.5.10]
### Fixed
- Fix the bin/omero-importer script [#127](https://github.com/ome/omero-insight/pull/127)

### Changed
- Fiji plugin, use the Bio-Formats crop option when opening a large image in Fiji [#114](https://github.com/ome/omero-insight/pull/114)
- Bump to omero-gateway-java 5.6.3

### Removed
- Remove ant build system [#125](https://github.com/ome/omero-insight/pull/125)

### Added
- Add CSV to the list of the supported formats for the attachments dialog [#118](https://github.com/ome/omero-insight/pull/118)
- Build Windows executable using Travis [#115](https://github.com/ome/omero-insight/pull/115)

## [5.5.9]
### Fixed
-  Fix issue preventing to open image in ImageJ when using web sockets [#106](https://github.com/ome/omero-insight/pull/106)
-  Fix loading of icons on Windows [#104](https://github.com/ome/omero-insight/pull/104)

## [5.5.8]
### Changed
- Bump to omero-gateway-java 5.6.1 with units fix

## [5.5.7]

### Added
- Major new metadata feature from @sukunis [#69](https://github.com/ome/omero-insight/pull/69)
- Enable login via web sockets [#66](https://github.com/ome/omero-insight/pull/66)

### Changed
- Bump to omero-gateway-java 5.6.0 with support for OMERO 5.6

## [5.5.6]
### Fixed
- Cast ByteBuffer to Byte for JDK8 support [#77](https://github.com/ome/omero-insight/pull/77)

## [5.5.5]
### Changed
- Bump to omero-gateway-java 5.5.4

## [5.5.4]
### Fixed
- Fix issue preventing to use the OMERO.imagej plugin in ImageJ [#65](https://github.com/ome/omero-insight/pull/65)
- Fix jar conflict when using OMERO.imagej plugin in newer version of Fiji [#72](https://github.com/ome/omero-insight/pull/72)
- Fix import freeze when importing large number of images and canceling import [#58](https://github.com/ome/omero-insight/pull/58)
- Fix issue when doing a "Cut&Paste" action [#67](https://github.com/ome/omero-insight/pull/67)

### Changed
- Improve error handling when a connection failure occurred during import [#62](https://github.com/ome/omero-insight/pull/62)


## [5.5.3]
### Fixed
- Fix SSL cipher issue to allow Insight to be used from Fedora 30
- Fix stack overflow exception

### Changed
- Bump to omero-gateway-java 5.5.3

## [5.5.2]
### Fixes
- Fix issue when submitting feedback.

### Changed
- Check for upgrade.

## [5.5.1]
### Removed
- Remove script non longer required.

### Changed
- Improve packaging to allow shortcut for executable.

## [5.5.0]
### Added
- Allow to import data off-line using `ome-smuggler <https://github.com/openmicroscopy/omero-ms-queue/>`_.
- Indicate in login screen that it is possible to use session ID to connect.
- Reduce size of packages.
- Add tasks to package applications.
- Add Gradle build system.
- Decouple OMERO.insight.

### Changed
- Improve saving of ROIs and Measurements when using the ImageJ plugin.

### Fixed
- Fix ImageJ packaging.
