How to build an agent
=====================

.. note:: With the release of OMERO 5.3.0, the OMERO.insight desktop client
    has entered **maintenance mode**, meaning it will only be updated if a
    major bug is discovered. Instead, the OME team will be focusing on
    developing the web clients. As a result, coding against this client is no
    longer recommended.

An agent is created and managed by the container. Building an agent is
done in two steps:

-  write the agent code
-  declare the agent in the :file:`container.xml` file located in the
   ``config`` directory

The agent intercepts the events posted on the event bus.

.. note:: When a new version of the software is delivered, make sure you keep 
          the :file:`container.xml` shipped with the application and add the 
          new agent entry to it.

Writing code
------------

The following example creates a concrete agent ``MyBrowserAgent``:

-  Create a ``myBrowser`` package in the ``agents`` package.
-  Create a class ``MyBrowserAgent``, this class **MUST** implement the
   ``Agent`` interface to be initialized and the ``AgentListener`` to
   interact with other agents.

::

    public class MyBrowserAgent
        implements Agent, AgentEventListener
    {

        /** Reference to the registry. */
        private static Registry         registry;

        //no-arguments constructor required for initialization
        public MyBrowserAgent() {}

        //Follow methods required by the Agent Interface

        //No-op implementation in general
        public void activate()
        {
            //this method will be invoked during the activation by the container
        }

        //invoked before shutting down the application
        public boolean canTerminate() { return true; }

        //not yet implemented: invoked when shutting down the application
        public Map<String, Set> hasDataToSave() { return null; }

        //invoked while shutting down the application
        public void terminate() {}

        public void setContext(Registry ctx)
        {
            //Must be a reference to the Agent Registry to access services.
            registry = ctx; 

            //register the events the agent listens to e.g. BrowseImage
            EventBus bus = registry.getEventBus();
            bus.register(this, BrowseImage.class);

        }



        //Follow methods required by the AgentEventListener Interface
        public void eventFired(AgentEvent e)
        {
            if (e instanceof BrowseImage) {
                //Do something
                browseImage((BrowseImage) e);
            }
        }

    }

Where to create the ``BrowseImage`` event
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

-  Create a ``myBrowser`` package in the ``agents.events`` package.
-  Create a ``BrowseImage`` event in the ``myBrowser`` package.

::

    public class BrowseImage          
        extends RequestEvent
    {

        /** The id of the image to browse. */
        private long imageID;
        
        /**
         * Creates a new instance.
         * 
         * @param imageID The id of image to view.
         */
        public BrowseImage(long imageID)
        {
            if (imageID < 0) 
                throw new IllegalArgumentException("ImageID not valid.");
            this.imageID = imageID;
        }
        
        
        /**
         * Returns the ID of the image to browse.
         * 
         * @return See above. 
         */
        public long getImageID() { return imageID; }

    }

Listening to the ``BrowseImage`` event
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

To listen to events posted on the event bus, the agent **MUST**
implement the ``AgentListener`` Interface and register the events to
listen to.

-  Register ``BrowseImage`` in the ``setContext(Registry)`` method of
   the ``Agent`` interface.
-  Listen to ``BrowseImage`` in the ``eventFired(AgentEvent)`` method of
   the ``AgentListener`` interface.

For example, when clicking on an image in the Data Manager, the following 
event is posted:

::

    EventBus bus = registry.getEventBus();
    bus.post(new BrowseImage(imageID));

The ``MyBrowserAgent`` handles the event

::

        public void eventFired(AgentEvent e)
        {
            if (e instanceof BrowseImage) {
                //Do something
                browseImage((BrowseImage) e);
            }
        }

Creating an agent's view
^^^^^^^^^^^^^^^^^^^^^^^^

See :doc:`BuildAgentView`

Declaring the agent
-------------------

The ``MyBrowserAgent`` needs to be declared in the :file:`container.xml`.

-  Open the :file:`container.xml` located in the ``config`` folder (see
   :doc:`../DirectoryContents`).
-  Add the following:

   ::

       <agents>
           <structuredEntry name="/agents" type="agents">
             
             <!-- NOTE FOR DEVELOPERS
                  Add an agent tag for each of your Agents.
                  The name tag specifies the human-readable name of the Agent.
                  The active tag specifies if the agent is turned on or off.
                  Set to true to turn the agent on, false otherwise.
                  The class tag specifies the FQN of the Agent class.
                  The config tag specifies the name of the Agent's 
                  configuration file within the config directory.
             -->
            <agent>
               <name>My Browser</name>
               <active>true</active>
               <class>org.openmicroscopy.shoola.agents.mybrowser.MyBrowserAgent</class>
               <config>mybrowser.xml</config>
             </agent>
       ...
           </structuredEntry>
         </agents>


-  Create a :file:`mybrowser.xml` and add it to the ``config`` directory:

   ::

       <?xml version="1.0" encoding="utf-8"?>
       <agent name="My Browser">
          <resources>
            <iconFactories>
           <!-- This entry is turned into an instance of: 
              org.openmicroscopy.shoola.env.config.IconFactory
              This object can then be used to retrieve any image file within
              the directory pointed by the location tag. -->
              <structuredEntry name="/resources/icons/Factory" type = "icons">
                <!-- The location tag specifies the FQN of the package that contains
                the icon files. -->
                <location>org.openmicroscopy.shoola.agents.myBrowser.graphx</location>
              </structuredEntry>

            </iconFactories>
            <fonts>
              <!-- This entry is turned into an instance of java.awt.Font. --> 
              <structuredEntry name="/resources/fonts/Titles" type="font">
                <family>SansSerif</family>
                <size>12</size>
                <style>bold</style>
              </structuredEntry>
            </fonts>
       </resources>
       </agent>

   The file :file:`mybrowser.xml` allows the agent to define specific
   parameters.

.. seealso:: 
    :doc:`../ImplementationView`,
    :doc:`RetrieveData`
