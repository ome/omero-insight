package org.openmicroscopy.shoola.util;



public class MonitorAndDebug {



	static public void printConsole(String output)
	{
//		System.out.println(output);
	}

	static public void printLogger(org.slf4j.Logger logger,String output)
	{
		logger.info(output);
	}



}
