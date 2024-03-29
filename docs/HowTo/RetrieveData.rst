Retrieve data from server
=========================

.. note:: With the release of OMERO 5.3.0, the OMERO.insight desktop client
    has entered **maintenance mode**, meaning it will only be updated if a
    major bug is discovered. Instead, the OME team will be focusing on
    developing the web clients. As a result, coding against this client is no
    longer recommended.

To retrieve data stored in an OMERO server, Agents can either:

-  directly access a Data service (container service) through their
   ``Registry`` (in which case, the call happens in the *Swing* dispatching 
   thread, so it is not possible to give user feedback by showing a progress     
   bar for example):

   ::

       OmeroDataService service = registry.getDataService();
       service.getServerName()


-  or retrieve data asynchronously using a **Data Services View**

Data services view
------------------

Usage
^^^^^

A data services view is a logical grouping of data and operations that
serve a specific purpose, for example to support dataset browsing by
providing easy access to datasets, thumbnails, tags, etc. A data
services view is defined by an interface that extends
``DataServiceView`` and consists of a collection of asynchronous calls
that operate on (possibly) large portions of a data model in the
background.

Agents obtain a reference to a given view through their registry by
specifying the view's defining interface as follows (note the *required*
cast on the returned reference):

::

    XxxView view = (XxxView) registry.getDataServicesView(XxxView.class);

``XxxView`` is obviously a made up name for one of the sub-interfaces of
``DataServiceView`` contained in this package. All calls are carried out
asynchronously with respect to the caller's thread and return a
``CallHandle`` object which can be used to cancel execution. This object
is then typically linked to a button so that the user can cancel the
task, like in the following example:

::

    final CallHandle handle = view.loadSomeDataInTheBg(observer);
        
       //The above call returns immediately, so we don't have to wait.
       //While the task is carried out, we allow the user to change 
       //her mind and cancel the task:
     
       cancelButton.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent e) {
               handle.cancel();
           }
       });

The ``observer`` argument to the above call is an instance of
``AgentEventListener`` (in ``env.event``). Normally all calls within a
view allow to specify this argument, which is used to provide the caller
with feedback on the progress of the task and with its eventual outcome.

Specifically, as the computation proceeds in the background,
``DSCallFeedbackEvent``\ s (in ``env.data.events``) are delivered to the
``observer``. These event objects have a status field which contains a
textual description of the activity currently being carried out within
the computation and a progress indicator which is set to the percentage
of the work done so far. So the indicator will be ``0`` for the first
feedback event and, if the computation runs to completion, ``100`` for
the last feedback event, which will always have its status field set to
``null`` -- note that a ``null`` status is also possible for the
previous events if no description was available at the time the event
was fired. Moreover, any partial result that the computation makes
available will be packed into the feedback event.

It is important to keep in mind that the computation may not run to
completion -- either because of an exception within the computation or
because the agent cancels execution -- ``CallHandle.cancel()`` (in
``env.data.views``). In both cases, the feedback notification will not
run to completion either. However, in any case a final
``DSCallOutcomeEvent`` (in ``env.data.events``) is delivered to the
``observer`` to notify of the computation outcome -- the event's methods
can be used to find out the actual outcome and retrieve any result or
exception. Every call documents what is the returned object and what are
the possible exceptions so that the caller can later cast the returned
value or exception as appropriate.

Here is the code for a prototypical ``observer``:

::

       public void eventFired(AgentEvent ae)
       {
           if (AE instanceof DSCallFeedbackEvent) {  //Progress notification. 
               update((DSCallFeedbackEvent) AE);  //Inform the user.
           } else {  //Outcome notification.
               DSCallOutcomeEvent oe = (DSCallOutcomeEvent) AE;
               switch (oe.getState()) {
               case DSCallOutcomeEvent.CANCELLED:  //The user cancelled.
                   handleCancellation();
                   break;
               case DSCallOutcomeEvent.ERROR:  //The call threw an exception.
                   handleException(oe.getException());
                   break;
               case DSCallOutcomeEvent.NO_RESULT:  //The call returned no value.
                   handleNullResult();
                   break;
               case DSCallOutcomeEvent.HAS_RESULT:  //The call returned a value.
                   handleResult(oe.getResult());
               }
           }
       }

Because the logic is likely to be common to most of the observers, the
``DSCallAdapter`` (in ``env.data.events``) class factors it out to
provide a more convenient way to write observers. Back to our previous
example, the observer could look something like the following:

::

    observer = new DSCallAdapter() {
         public void update(DSCallFeedbackEvent fe) {  //Received some feedback.
             String status = fe.getStatus();
             int percDone = fe.getPercentDone();
             if (status == null) 
                 status = (percDone == 100) ? "Done" :  //Else
                                            ""; //Description was not available.   
             statusBar.setText(status);  //A JLabel object part of the UI.
             progressBar.setValue(percDone); //A JProgressBar object part of the UI.
         }      
         public void onEnd() { //Called right before any of the handleXXX methods.
             progressBar.setVisible(false);  //Because the computation has finished.
         }
         Public void handleResult(Object result) {  //Computation returned a result. 
             //We have a non-null return value.  Cast it to what 
             //loadSomeDataInTheBg() declared to return.
             SomeData data = (SomeData) result;
       
             //Update model, UI, etc.
         }
         public void handleCancellation() {  //Computation was cancelled.
             UserNotifier un = registry.getUserNotifier();
             un.notifyInfo("Data Loading", "SomeData task cancelled.");
         }
         Public void handleException(Throwable exc) {  //An error occurred.
             UserNotifier UN = registry.getUserNotifier();
             un.notifyError("Data Loading Failure",
                            "Couldn't retrieve SomeData.", exc);
         }
       };

Note that the ``observer``'s code in the example above works just like
any other *Swing* listener. In fact, all events are delivered
sequentially and within the *Swing* event dispatching thread. This means
the ``observer`` can run synchronously with respect to the UI and will
not need to worry about concurrency issues -- as long as it runs within
*Swing*. Finally, also note that subsequent feedback events imply
computation progress and the ``DSCallOutcomeEvent`` is always the last
event to be delivered in order of time.

**The xxxLoader classes in agents.treeviewer are a good place to look at
and see how to use data services view.**

Execution
^^^^^^^^^

The next diagram analyzes a concrete call to a view to exemplify the
pattern followed by all asynchronous calls in the various views. The
call is mapped onto a command, the command is transferred to a processor
for asynchronous execution, a handle to the call is returned to the
invoker.

.. figure:: /images/omeroinsight-retrievedata-invocation.png
  :align: center
  :alt: Retrieving data

  Retrieving data invocation

Initialization
^^^^^^^^^^^^^^

The ``DataViewsFactory`` (in ``env.data.views``) needs to be initialized
before any concrete ``BatchCallTree`` (in ``env.data.views``) is
created. The reason for this is that ``BatchCallTree``'s constructor
needs to cache a reference to the registry so that concrete subclasses
can access it later. The ``DataViewsFactory`` takes care of this
initialization task during the container's start-up procedure by calling
``DataViewsFactory.initialize(Container)``. Any data service view should
be created in ``env.data.views`` and declared in
``DataViewsFactory.makeNew(Class)``. The method returns an
implementation of the corresponding view.

.. seealso:: :doc:`../DirectoryContents`
