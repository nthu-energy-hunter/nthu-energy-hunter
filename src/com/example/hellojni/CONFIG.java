package com.example.hellojni;

public class CONFIG {
	
	// Sampling Rate
	public static int sampleRate = 1000;
	
	// Where the log is storing
	public static String logFile = "hw_usage_trace.txt";
	
	// Current Device 
	public static enum DEV {
		NEXUS, GOLDFISH
	}
	// Granularity for Process Crowler
	public static enum PC_Gran {
		DETAILED, NUMBER
	}

	/* Current Settings */
	public static DEV currentDev = DEV.NEXUS;
	public static PC_Gran perProcess = PC_Gran.NUMBER;

	
}
