# OMERO.insight

[![Actions Status](https://github.com/ome/omero-insight/workflows/Build/badge.svg)](https://github.com/ome/omero-insight/actions)
[![Documentation Status](https://readthedocs.org/projects/omero-insight/badge/?version=latest)](https://omero-insight.readthedocs.io/en/latest/)

The OMERO.insight Project is a sub-project of the Open Microscopy Environment
Project, [OME](https://www.openmicroscopy.org/) that focuses on delivering a
client for the visualization and manipulation of both image data and metadata
maintained at an OMERO server site.
OMERO.insight is completely written in Java.
Technical documentation can be found [here](https://omero-insight.readthedocs.io/en/latest/).

## Directory Contents

This directory is the repository of the software artifacts of the
OMERO.insight Project. Its contents are as follows:

  + build.gradle, settings.gradle: build for build system
  + src: Contains the application source files, various configuration
         files required by the application to run and the test code.
  + README.md: This file.
  + LICENSE.txt: The license covering this software.
  + CHANGELOG.md: The release history
  + FAQ.md: Commons questions

##  Building OMERO.insight

The compilation, testing, launch, and delivery of the application are
automated by means of a [Gradle](https://gradle.org/) build file.
In order to perform a build, all you need is
a JDK -- version 17 or later. From the command line, enter:

    ./gradlew build

This will compile, build, test and create a distribution bundle.
To run OMERO.insight from the distribution bundle unzip
the zip under `build/distributions` then go to the unzipped
directory and from the command line enter, for example:

    bin/OMERO.insight

To run the OMERO.importer, enter:

    bin/OMERO.insight containerImporter.xml

To run OMERO.insight, from the command line, enter:

    ./gradlew run

To run OMERO.importer, from the command line, enter:

    ./gradlew runImporter

## Packaging OMERO.insight

Packaging OMERO.insight requires version 21 or later of the JDK version.  The
build system uses the [jpackage](https://docs.oracle.com/en/java/javase/21/docs/specs/man/jpackage.html)
command via the Gradle [org.beryx.runtime](https://plugins.gradle.org/plugin/org.beryx.runtime) plugin
to create an installer for the platform the deployment task is run on.

To run the application packager, from the command line enter:

    ./gradlew jpackage

##  Developing OMERO.insight

  See https://omero.readthedocs.io/en/stable/developers/index.html#insight

##  OMERO.insight extension

To use the metadata extension [OMERO.mde](mde-extension.md),
set the following entry in [container.xml](src/dist/config/container.xml) or in
[containerImporter.xml](src/dist/config/containerImporter.xml) to `true`:

  
    <entry name="omero.client.import.mde.enabled" type="boolean">true</entry>

## FAQ

See [FAQ.md](FAQ.md)

## Licensing

This software is licensed under the terms of the GNU General Public
License (GPL), the full text of which can be found in
the top level LICENSE.txt. See https://www.openmicroscopy.org/licensing/
for further details.


##  Copyright

Copyright (C) 2006-2024 University of Dundee. All rights reserved.
