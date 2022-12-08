Finding the log file
--------------------

Go to ``Help > Show Log File``.
This will show you the location of the log file - ``omeroinsight.log``.
If there are other log files with ``_1`` or ``_2`` appended to the name, ignore them.
Alternatively you can find the log file in your user folder:
 - Windows: ``C:\Users\you\omero\log\omeroinsight.log``
 - Mac/Linux: ``/yourhomedir/omero/log/omeroinsight.log``

Submitting files
----------------

If an error occurred during import you can upload the files using the submission dialog. Click on the ``Failed`` button next to the image whose import failed and then select ``Submit`` in the following menu. 
Automatic uploading of files to OME QA will not work if the file size is greater than 2GB.
Submission of smaller file sizes can also not work if you are uploading over a slow or
unreliable network. If the file upload to OME QA fails, please contact the team using our
[support page](https://www.openmicroscopy.org/support/) to arrange an alternative mechanism to submit the files.

Adding third parties' JARs
--------------------------

OMERO.insight comes bundled with a number of standard Java Archives (JARs).
However, functionality can be extended by adding JARs to OMERO.insight.
An example of this is when some proprietary file formats require additional
readers to enable OMERO to open and read the files. These readers can be provided
by the owners of the formats as JARs, which need to be added to OMERO.insight to enable the functionality.

If third-party JARs are related to the Bio-Formats import reader,
they will also need to be added to the OMERO.server by your OMERO systems administrator.
There is no need to do this if they are simply OMERO.insight update JARs.

**Mac**:

 - Right-click or control-click on the OMERO.insight app.
 - Choose ``Show Package Contents`` from the contextual menu.
 - Navigate to the ``Contents`` folder.
 - Select ``Java > lib``.
 - Drag and drop, or copy, the third-party ``.jar`` file into the ``lib`` folder.
 - Start the application.

**Windows**:

 - Select the executable in Explorer.
 - Select the ``lib`` folder.
 - Drag and drop, or copy, the third-party ``.jar`` file into the ``lib`` folder.
 - Start the application.

**Linux**:

 - Unzip the app.
 - Go to the ``lib`` directory.
 - Drag and drop, or copy, the third-party ``.jar`` file into the ``lib`` folder.
 - Start the application.

Increasing Memory Allocation
----------------------------

By default, the Desktop client allocates a certain amount of memory
during the start-up process which should be sufficient for most uses.
The default values are ``-Xms256m, -Xmx1024m``.

However, you may encounter out of memory exceptions while trying to import
or display very large images. In these situations, it is beneficial to increase
the memory allocation to the clients as outlined below.

**Mac**:

 - Right-click or control-click on on the omero.insight app.
 - Choose ``Show Package Contents`` from the contextual menu.
 - Navigate to the ``Contents`` folder.
 - Open the file  ``Java > OMERO.insight.cfg``.
 - Edit the ``[JVMOptions]``section.

**Windows**:

You must edit the config file `app\OMERO.insight.cfg` stored in application folder. JVMOptions are available in the `[JVMOptions]` block, e.g.:

```
[JVMOptions]
-Xms256m
-Xmx1024m
```

**Linux**:

 - Unzip the app.
 - Open the file ``bin/omero-insight`` in a text editor.
 - Change the value of ``DEFAULT_JVM_OPTS``.
