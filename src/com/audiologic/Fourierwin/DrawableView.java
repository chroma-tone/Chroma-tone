package com.audiologic.Fourierwin;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.audiologic.Fourierwin.PitchDetectionRepresentation;

public class DrawableView extends View {

	
	private Double[] NotePitches;
	private String[] NoteNames;
	private Map<Double, String> NotePitchesMap; 
	
	public DrawableView(Context context) {
		super(context);
		
		 NotePitches = new Double[30];
		 NoteNames = new String[12];
		 Map<Double,String> NotePicthesMap = new HashMap<Double,String>();
		
		NotePitches[0] = 82.41;
		NotePitches[1] = 87.31;
		NotePitches[2] = 92.5;
		NotePitches[3] = 98.0;
		NotePitches[4] = 103.8;
		NotePitches[5] = 110.0;
		NotePitches[6] = 116.54;
		NotePitches[7] = 123.48;
		NotePitches[8] = 130.82;
		NotePitches[9] = 138.59;
		NotePitches[10] = 147.83;
		NotePitches[11] = 155.56;
		NotePitches[12] = 164.81;
		NotePitches[13] = 174.62;
		NotePitches[14] = 185.0;
		NotePitches[15] = 196.0;
		NotePitches[16] = 207.0;
		NotePitches[17] = 220.0;
		NotePitches[18] = 233.08;
		NotePitches[19] = 246.96;
		NotePitches[20] = 261.63;
		NotePitches[21] = 277.18;
		NotePitches[22] = 293.66;
		NotePitches[23] = 311.13;
		NotePitches[24] = 329.63;
		NotePitches[25] = 349.23;
		NotePitches[26] = 369.99;
		NotePitches[27] = 392.0;
		NotePitches[28] = 415.3;
		NotePitches[29] = 440.0;
		
		
		NoteNames[0] = "E";
		NoteNames[1] = "F";
		NoteNames[2] = "F#";
		NoteNames[3] = "G";
		NoteNames[4] = "G#";
		NoteNames[5] = "A";
		NoteNames[6] = "A#";
		NoteNames[7] = "B";
		NoteNames[8] = "C";
		NoteNames[9] = "C#";
		NoteNames[10] = "D";
		NoteNames[11] = "D#";
		
			for (int freq = 0; freq< 30; freq++) {
				
				  NotePitchesMap.put(NotePitches[freq],  NoteNames[freq%12]);  
				
			
		}
	}

	public void setDetectionResults(HashMap<Double, Double> frequencies,
			double pitch) {
		TextView tv = (TextView)findViewById(R.id.textView1);  
		tv.setText("Text to set");
		
	}
			
	}
	
	
	
