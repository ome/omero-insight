Architecture
------------

.. note:: With the release of OMERO 5.3.0, the OMERO.insight desktop client
    has entered **maintenance mode**, meaning it will only be updated if a
    major bug is discovered. Instead, the OME team will be focusing on
    developing the web clients. As a result, coding against this client is no
    longer recommended.

Logical view
~~~~~~~~~~~~

OMERO.insight is logically organized in two layers

.. figure:: /images/omero-insight-architecture-agents.png
  :align: center
  :alt: OMERO.insight agents and containers

  OMERO.insight agents and containers

The **Agents** layer contains the logic to manage user interaction. It
contains coarse grained components which we call **agents**, that are
each responsible for a specific aspect of the application's
functionality:

-  The Data Manager provides the user with the GUI functionality to
   access their data, metadata and visualize large image sets.
-  The Viewer is a tool to visualize and tune 5D images.
-  The Measurement Tool is a tool to perform basic measurement.

.. note::

    If you want to add a new agent, go to :doc:`HowTo/BuildAgent`.

These agents are internally organized according to the MVC
(` Model-View-Controller <https://en.wikipedia.org/wiki/Model-view-controller>`_)
pattern, PAC
(` Presentation-Abstraction-Control <https://en.wikipedia.org/wiki/Presentation-abstraction-control>`_)
pattern, or a combination of the two. They rely on the services provided
by the bottom layer, the **Container**, to accomplish their tasks.

The **Container** layer manages the agents life-cycle and provides them
with services to:

-  Communicate without having to know each other 
   (:doc:`EventBus`).
-  Access the OMERO Server (data management and image services).
-  Transform entries in configuration files into objects and then access
   them (:doc:`Configuration`).
-  Log messages (log service) and notify the user (user notification
   service) of runtime errors.
-  Cache data (cache service).
-  Provide a common top level window to plug their GUI's 
   (:doc:`TaskBar`).

Initialization of Agents
~~~~~~~~~~~~~~~~~~~~~~~~

.. _Fow: https://martinfowler.com/books

Agents let the container create them and then manage their life-cycle.
This is achieved through the use of a common interface, ``Agent``, that
all agents have to implement and by requiring every agent to have a
public no-arguments constructor. The Agent interface plays the role of a
Separated Interface (`Fow`_),
decoupling the container from knowledge of concrete agents. This way,
new agents can be plugged in.

At start-up the container finds out which are the agents' implementation
classes from its configuration file, instantiates every agent by
reflection (using the no-arguments constructor) and then reads each
agent's configuration file
(`Fow`_). The configuration
entries in this file are turned into objects and collected into a
map-like object, which is then passed to the agent. This map object also
contains pointers to the container's services. We can think of this
object as a Registry (`Fow`_).

There is one Registry containing pointers to the container's services
for each agent, so configuration entries are private to each agent -
container's services are shared among all agents though. Agents access
the Registry object through the Registry interface.

The life-cycle of an agent is as follow:

.. figure:: /images/omero-insight-architecture-agent-lifecycle.png
  :align: center
  :alt: OMERO.insight agent lifecycle

  OMERO.insight agent lifecycle

Interaction among Agents
~~~~~~~~~~~~~~~~~~~~~~~~

Interactions among agents are event-driven. Agents communicate by using
a shared :doc:`EventBus` provided by the
container. The event bus is an event propagation mechanism loosely based
on the
` Publisher-Subscriber <https://en.wikipedia.org/wiki/Publish/subscribe>`_
pattern and can be regarded as a time-ordered event queue - if event A
is posted on the bus before event B, then event A is also delivered
before event B.

Process view
~~~~~~~~~~~~

All agents run synchronously within the *Swing* dispatching thread. All
container’s services are called within Swing event handlers and thus run
within the *Swing* dispatching thread. To see how to retrieve data from
an OMERO server, go to the :doc:`HowTo/RetrieveData` page.

.. seealso::

    :doc:`ImplementationView`,
    :doc:`EventBus`
