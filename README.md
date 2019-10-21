  OMERO.insight Project
  ---------------------

  The OMERO.insight Project is a sub-project of the Open Microscopy Environment
  Project, [OME](https://www.openmicroscopy.org/) that focuses on delivering a
  client for the visualization and manipulation of both image data and metadata
  maintained at an OMERO server site.
  OMERO.insight is completely written in Java.


  Directory Contents
  ------------------

  This directory is the repository of the software artifacts of the
  OMERO.insight Project. Its contents are as follows:

    + build: This directory contains the tools to compile, run, test and
             deliver the application.
    + src: Contains the application source files, various configuration
           files required by the application to run and the test code.
    + README: This file.

  Building OMERO.insight
  ----------------------

  The compilation, testing, launch, and delivery of the application are
  automated by means of a Gradle (https://gradle.org/) build file.
  In order to perform a build, all you need is
  a JDK -- version 1.8 or later. From the command line, enter:

     gradle build

  This will compile, build, test and create a distribution bundle.
  To run OMERO.insight from the distribution bundle unzip
  `omero-insight.zip` then go to the `omero-insight` directory and from
  the command line enter, for example:

     bin/omero-insight

  To run the OMERO.importer, enter:

     bin/omero-insight containerImporter.xml

  To run OMERO.insight, from the command line, enter:

     gradle run

  To run OMERO.importer, from the command line, enter:

     gradle runImporter

  Packaging OMERO.insight
  -----------------------

  OMERO.insight uses the Gradle [java-packager-plugin](https://github.com/ome/omero-javapackager-plugin)
  and is capable of creating an installer for the platform the deployment task is run on.

  __Note, a Java runtime is included with the bundled installer. It is recommended to use
  [OpenJDK](https://openjdk.java.net) as the system's JRE to avoid potential licensing issues.__

  In order to be able to successfully create an installer, a JDK with
  [`javapackager`](https://docs.oracle.com/javase/8/docs/technotes/tools/unix/javapackager.html) or
  an [OpenJFX SDK](https://gluonhq.com/products/javafx/) matching the version of the system JDK
  is required (particularly if you intend to build with JDK 11 or higher).

  ### OSX

  If you are using [Homebrew](https://brew.sh/), you can install, for example, [OpenJDK 8](https://www.azul.com/downloads/zulu/)
  which comes bundled with JavaFX.
  To install run:

      brew cask install zulu8

  ### Windows

  #### Scoop:

      scoop bucket add java

      scoop install zulufx8

  #### Chocolatey:

      choco install zulu8

  #### Manually:

  To set up a build environment with Windows without using a package manager such as [Scoop](https://scoop.sh) or
  [Chocolatey](https://chocolatey.org) perform the following:

  **JDK:**

  1. Download [ojdkbuild](https://github.com/ojdkbuild/ojdkbuild/releases/download/1.8.0.191-1/java-1.8.0-openjdk-1.8.0.191-1.b12.ojdkbuild.windows.x86_64.msi).
  2. Run the installer.
  3. Once at the _custom setup_ page of the setup select the _OpenJDK Runtime_ drop-down and install the _JAVA_HOME_
     feature.
  4. Whilst on the _custom setup_ page, select to include the _OpenJFX Runtime_.

  **Inno Setup (required to create .exe installer):**

  1. Download and install [Inno Setup](http://www.jrsoftware.org/isdl.php).
  2. Add the Inno Setup install directory (default `C:\Program Files (x86)\Inno Setup 5`) to the PATH.

  **WiX (required to build .msi installer):**

  1. Download and install [WiX 3.0 or greater](http://wixtoolset.org/).
  2. Add WiX to the PATH.

  ### Packaging

  To run the application packager, from the command line enter:

  **Windows**

     gradle packageApplicationExe

  **OSX**

     gradle packageApplicationDmg

  Developing OMERO.insight
  ------------------------

  See https://docs.openmicroscopy.org/latest/omero/developers/index.html#insight.

  OMERO.insight extension
  -----------------------

  1. To use the metadata extension [OMERO.mde](mde-extension.md),
  set the following entry in [container.xml](src/config/container.xml) or in
  [containerImporter.xml](src/config/containerImporter.xml) to `true`:
  ```
  <entry name="omero.client.import.mde.enabled" type="boolean">false</entry>
  ```


  Licensing
  ---------

  This software is licensed under the terms of the GNU General Public
  License (GPL), the full text of which can be found in
  the top level LICENSE.txt. See https://www.openmicroscopy.org/licensing/
  for further details.


  Copyright
  ---------

  Copyright (C) 2006-2019 University of Dundee. All rights reserved.
