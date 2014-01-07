package com.audiologic.Fourierwin;

import java.util.HashMap;

import android.app.Activity;
import android.widget.TextView;

public class DrawableView {
	Newfourier j;
	public DrawableView(Newfourier newfourier) {
		this.j = newfourier;
	}

	public void setDetectionResults(HashMap<Double, Double> frequencies,
			double pitch) {
		
		j.setPitch(Double.toString(pitch));
		
		
	}

}
