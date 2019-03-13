


                                   README

                      OPEN MICROSCOPY ENVIRONMENT (OME)

                                 OMERO.insight







  OMERO.insight Project
  ---------------------

  The OMERO.insight Project is a sub-project of the Open Microscopy Environment
  Project (OME, https://www.openmicroscopy.org/) that focuses on delivering a
  client for the visualization and manipulation of both image data and metadata
  maintained at an OMERO server site.
  OMERO.insight is completely written in Java.


  Directory Contents
  ------------------

  This directory is the repository of the software artifacts of the
  OMERO.insight Project. Its contents are as follows:

    + build: This directory contains the tools to compile, run, test, and
          deliver the application.
    + config: Various configuration files required by the application to run.
    + launch: Its sub-dirs contain further resources to build platform-specific distributions.
    + src: Contains the application source files.
    + test: The test code.
    + README: This file.

  Building OMERO.insight
  ----------------------

  The compilation, testing, launch, and delivery of the application are
  automated by means of a Gradle (https://gradle.org/) build file.
  In order to perform a build, all you need is
  a JDK -- version 1.8 or later. From the command line, enter:
  
      gradle build
  
  This will compile, run, test, and create a
  distribution bundle.

  Developing OMERO.insight
  ------------------------

  See https://docs.openmicroscopy.org/latest/omero/developers/index.html#insight

  Licensing
  ---------

  This software is licensed under the terms of the GNU General Public
  License (GPL), the full text of which can be found in /lib/licenses/gpl.txt or
  the top level LICENSE.txt. See /lib/licenses/README and
  https://www.openmicroscopy.org/licensing/ for further details.


  Copyright
  ---------

  Copyright (C) 2006-2019 University of Dundee. All rights reserved.
