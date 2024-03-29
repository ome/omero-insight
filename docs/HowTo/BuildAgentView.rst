How to build an agent's view
============================

.. note:: With the release of OMERO 5.3.0, the OMERO.insight desktop client
    has entered **maintenance mode**, meaning it will only be updated if a
    major bug is discovered. Instead, the OME team will be focusing on
    developing the web clients. As a result, coding against this client is no
    longer recommended.

This section explains how a view of the agent is created. All our
agents follow the same approach.

Using the previous example ``MyBrowserAgent`` (see :doc:`BuildAgent`):

#. Create a ``view`` package in the ``mybrowser`` package.
#. Create the following classes ``MyBrowser`` (interface),
   ``MyBrowserComponent``, ``MyBrowserModel``, ``MyBrowserControl``, and
   ``MyBrowserUI``. If you browse the source code, you will notice that
   we usually have a class used as a toolbar and a class used as a
   status bar. Both classes are initialized by the ``BrowserUI``. For
   clarity, they have been omitted in the following diagram.
#. Create a ``MyBrowserFactory``. This class keeps track of the
   ``MyBrowser`` instances created and not yet discarded. A component is
   only created if none of the tracked ones is displaying the data,
   otherwise the existing component is recycled.

.. figure:: /images/omeroinsight-agent-view.png
  :align: center
  :alt: Agent view

  OMERO.insight agent view

Typical life-cycle of an agent view
-----------------------------------

The object is first created using the ``MyBrowserFactory``

::

    //Somewhere in the MyBrowserFactory code

        /** The sole instance. */
        private static final MyBrowserFactory  singleton = new MyBrowserFactory();
        
        /**
         * Returns a viewer to display the specified images.
         * 
         * @param images The <code>ImageData</code> objects.
         */
        public static MyBrowser getViewer(Set<ImageData> images)
        {
            MyBrowserModel model = new MyBrowserModel(images);
            return singleton.getViewer(model);
        }


        /**
         * Creates or recycles a viewer component for the specified 
         * <code>model</code>.
         * 
         * @param model The component's Model.
         * @return A {@link MyBrowser} for the specified <code>model</code>.  
         */
        private MyBrowser getViewer(MyBrowserModel model)
        {
            Iterator v = viewers.iterator();
            MyBrowserComponent comp;
            while (v.hasNext()) {
                comp = (MyBrowserComponent) v.next();
                if (model.isSameDisplay(comp.getModel())) {
                    comp.refresh(); //refresh the view.
                    return comp;
                }
            }
            comp = new MyBrowserComponent(model);
            comp.initialize();
            comp.addChangeListener(this);
            viewers.add(comp);
            return comp;
        }

After creation, the object is in the ``MyBrowser#NEW`` state and is
waiting for the ``MyBrowser#activate()`` method to be called. Such a
call usually triggers loading of the objects on the server. The object is 
now in the ``MyBrowser#LOADING`` state. After all the data have been 
retrieved, the object is in the ``MyBrowser#READY`` state and the data display 
is built and set on screen.

When the user quits the window, the ``MyBrower#discard()`` method is
invoked and the object transitions to the ``MyBrowser#DISCARDED`` state.
At which point, all clients should de-reference the component to allow
for garbage collection.

.. figure:: /images/omeroinsight-agent-view-init.png
  :align: center
  :alt: Agent view initialization

  OMERO.insight agent view initialization
