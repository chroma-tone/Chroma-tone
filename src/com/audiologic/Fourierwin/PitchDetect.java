package com.audiologic.Fourierwin;

public class PitchDetect {
	
	public PitchDetect(){
		
		
			System.loadLibrary("Fourierwin");
		
		
	}
	
	public native void fprocess(double[] data, int size);

}
