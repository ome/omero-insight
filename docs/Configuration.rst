Configuration
=============

.. note:: With the release of OMERO 5.3.0, the OMERO.insight desktop client
    has entered **maintenance mode**, meaning it will only be updated if a
    major bug is discovered. Instead, the OME team will be focusing on
    developing the web clients. As a result, coding against this client is no
    longer recommended.

The container provides a flexible and extensible configuration
mechanism. Each agent has its own configuration file which is parsed at
start-up by the container. The configuration entries in this file are
turned into objects and collected into a map-like object, which is then
passed to the agent. This map object also contains pointers to the
container’s services. Thus, we can think of this object as a *Registry*.
There is one ``Registry`` for each agent, so configuration entries are
private to each agent - container's services are shared among all agents
though. The container also has its own configuration file and
``Registry``.

The container maintains a set of predefined bindings that are used to
convert a configuration entry into an object - such as a ``String``,
``Integer``, ``Font``, ``IconFactory``, etc. However, agents can specify
custom handlers for converting a configuration entry into an object.

Structure
---------

Configuration files are XML files which declares only two elements:

::

    <element name="entry" minOccurs="0" maxOccurs="unbounded">
      <complexType>
        <simpleContent >
          <extension base="string">
            <attribute name="name" type="string" use="required"/>
            <attribute name="type" type="string" default="string"/>
            <simpleType>
              <restriction base="string">
                <enumeration value="string"/>
                <enumeration value="integer"/>
                <enumeration value="float"/>
                <enumeration value="double"/>
                <enumeration value="boolean"/>
              </restriction>
    . . . close all tags
    <element name="structuredEntry" minOccurs="0" maxOccurs="unbounded">
      <complexType>
        <sequence>
          <any maxOccurs="unbounded"/>
        </sequence>
        <attribute name="name" type="string" use="required"/>
        <attribute name="type" type="string" default="map"/>
      </complexType>
    </element>

The *entry* and *structuredEntry* elements are used to specify
name-value pairs in the actual configuration files. Both elements have a
required name attribute whose content is used as a key for accessing the
entry value from within the application. In the case of the entry
element, the value is a string -- this element can thus be used for
"classic" name-value style, such as:

::

    <entry name="ITEM">The item's value</entry>

On the other hand, the value of *structuredEntry* can be made up by any
arbitrary sequence of tags.

In both cases (*entry* and *structuredEntry*), the entry value is
returned to the application as an object and the type attribute dictates
how the entry value is turned into an object. Only "string", "integer",
"float", "double" and "boolean" may be used for the type attribute
within the entry element, whose content is parsed into a Java
``String``, ``Integer``, ``Float``, ``Double`` or ``Boolean`` object
according to the content of the type attribute -- if this attribute is
missing, then "string" is assumed. For example, say you write the
following into an agent's configuration file:

::

    <entry name="/some/name" type="boolean">true</entry>

This will make a ``Boolean`` object (set to hold *true*) available to
the agent -- the key for accessing the object will be the string
"/some/name".

Things work similarly for the *structuredEntry* element. In this case,
the content of the type attribute can be specified to be the fully
qualified name of the class that will handle the transformation of the
entry value into an object. This is provided so that agents may specify
custom handlers for custom configuration entries. For example, an
agent's configuration file could contain the entry:

::

    <structuredEntry name="/some/name" type="some.pkg.SomeHandler">
      <tag_1>aValue</tag_1>
      <tag_2>anotherValue</tag_2>
    </structuredEntry>

In this case, an instance of ``some.pkg.SomeHandler`` will be created to
transform the contents of the entry (that is *tag\_1* and *tag\_2*) into
a custom object. Obviously enough, the tags contained within a
*structuredEntry* have to be exactly the tags that the handler expects.

If no type attribute is specified, then it is assumed *type = "map"*,
which results in the entry's contents being parsed into a ``Map``
object. Each child tag is assumed to be a simple tag with a string
content, like in the following example:

::

    <structuredEntry name="/some/name">
      <key_1>value_1</key_1>
      <key_2>value_2</key_2>
    </structuredEntry>

Each child tag's name is a key in the map and the tag's content is its
value -- the above would generate the map:
``(key_1, value_1), (key_2, value_2)``.

Some predefined structured entries are supplied by the container for
common cases (icons and font entries) and for use by the container only
(OMERO and agents entries). Here's an excerpt from the container's
configuration file:

::

    <container>
      <services>
        <structuredEntry name="/services/OMERODS" type="OMERODS">
          <port>1099</port>
        </structuredEntry>
      </services>
      <agents>
        <structuredEntry name="/agents" type="agents">
          <agent>
            <name>Viewer</name>
            <!-- The class tag specifies the FQN of the agent's class. -->
              <class>org.openmiscroscopy.shoola.agents.viewer.Viewer</class>
              <!-- The config tag specifies the name of the agent‘s configuration file.
                   This file has to be placed in the config directory under the 
                   installation directory. -->
              <config>viewer.xml<config>
            </agent>
     . . . a similar entry for every other agent
        </structuredEntry>
      </agents>
      <resources>
        <iconFactories>
          <!-- This entry can be used in agents' configuration files as well.
           It is turned into an instance of: 
           org.openmicroscopy.shoola.env.config.IconFactory
           This object can then be used to retrieve any image file within
           the directory pointed by the location tag. -->
          <structuredEntry name="/resources/icons/DefaultFactory" type="icons">
           <!-- The location tag specifies the FQN of the package that contains the icon files. -->
            <location>org.openmicroscopy.shoola.env.ui.graphx</location>
        </structuredEntry>
     . . . more similar entries
        </iconFactories>
        <fonts>
          <!-- This entry can be used in agents' configuration files as well. 
               It is turned into an instance of java.awt.Font. -->
          <structuredEntry name="/resources/fonts/Titles" type="font">
            <family>SansSerif</family>
            <size>12</size>
            <style>bold</style>
          </structuredEntry>
     . . . more similar entries
        </fonts>
        </resources>
    </container>

The configuration parser only takes the *entry* and *structuredEntry*
tags into account and ignores all the others. It may be useful to group
sets of related entries together, as shown above.

The classes that encompass the machinery for parsing configuration files
and building registries are depicted by the following UML class diagram.

.. figure:: /images/omeroinsight-configuration.png
  :align: center
  :alt: OMERO.insight configuration

  OMERO.insight configuration

The ``Entry`` abstract class sits at the base of a hierarchy of classes
that represent entries in configuration files. It represents a
name-value pair, where the name is the content of the *name* attribute
of a configuration entry (which is stored by the ``name`` field) and the
value is the object representing the entry's content. As the logic for
building an object from the entry's content depends on what is specified
by the *type* attribute, this class declares an abstract ``getValue``
method which subclasses implement to return the desired object -- we use
polymorphism to avoid conditional logic. So we have subclasses
(``StringEntry``, ``IntegerEntry``, ``IconFactoryEntry``, etc.) to
handle the content of an entry tag (either *entry* or *structuredEntry*)
in correspondence of each predefined value of the type attribute
("string", "integer", "icons", and so on). Given an entry tag, the
``createEntryFor`` static method (which can be considered a Factory
Method) creates a concrete ``Entry`` object to handle the conversion of
that tag's content into an object. Subclasses of ``Entry`` implement the
``setContent`` method to grab the tag's content, which is then used for
building the object returned by the implementation of ``getValue()``.

The ``Registry`` Interface declares the operations to be used to access
configuration entries and container's services.

The ``RegistryImpl`` class implements the ``Registry`` interface. It
maintains a map of ``Entry`` objects, which are keyed by their name
attribute and represent entries in the configuration file. It also
maintains references to the container's services into member fields --
as services are accessed frequently, this ensures *o(1)* access time.

The ``Parser`` class is in charge of parsing a configuration file,
extracting entries (only *entry* and *structuredEntry* tags are taken
into account), obtain an ``Entry`` object to represent each of those
entries and add these objects to a given ``RegistryImpl`` object.

Dynamics
--------

How a configuration file is parsed and the corresponding Registry is
built:

.. figure:: /images/omeroinsight-parsing-config-files.png
  :align: center
  :alt: Parsing configuration files

  Parsing configuration files

A ``RegistryImpl`` object is created with an empty map. Then a
``Parser`` object is created passing the path to the configuration file
and the ``RegistryImpl`` object. At this point ``parse()`` is invoked on
the ``Parser`` object. The configuration file is read (the XML parsing
is handled by built-in JAXP libraries) and, for each configuration entry
(that is, either *entry* or *struturedEntry* tag), ``createEntryFor()``
is called to obtain a concrete ``Entry`` object, which will handle the
conversion of the tag's content into an object. This ``Entry`` object is
then added to the map kept by the ``RegistryImpl`` object.

In order to find out which class is in charge of handling a given tag,
the ``Entry`` class maintains a map, ``contentHandlers``, whose keys are
the predefined values used for the type attribute ("string", "integer",
"icons", etc.) and values are the fully qualified names of the handler
classes. Given a tag, ``createEntryFor()`` uses the content of the type
attribute (or "string" if this attribute is missing) to look up the
class name in the map and then creates an instance by reflection - all
``Entry``'s subclasses are supposed to have a no-args constructor. If
the class name is not found in the map, then the content of the type
attribute is assumed to be a valid fully qualified name of an
``Entry``'s subclass. This allows for agents to specify custom handlers
-- as long as the handler extends ``Entry`` and has a public no-args
constructor.

Notice that the ``RegistryImpl`` object adds the couple
``(e.getName(), e)`` to its map. Because the ``Entry`` class takes care
of setting the name field to the content of the name attribute within
the entry tag, the application code can subsequently access e by
specifying the value of the name attribute to ``lookup()``. The above
outlined process is repeated for each configuration file so that the
configuration entries of each agent (and the container) are kept in
separate objects -- a ``RegistryImpl`` is created every time. Because
every agent is then provided with its own ``RegistryImpl`` object, the
configuration entries are private to each agent. However, the container
configures all ``RegistryImpl`` objects with the same references to its
services.

.. seealso:: :doc:`DirectoryContents`
