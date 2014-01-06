package com.audiologic.Fourierwin;
/*
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.StringTokenizer;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.media.*;
import android.media.AudioRecord.OnRecordPositionUpdateListener;
import android.media.MediaRecorder.AudioSource;
import android.view.View.OnClickListener;

;

public class Newfourier extends Activity implements OnClickListener {

	
	private static int buffersize;
	private static int count = 0;
	public static AudioRecord AT;
	private short[][] buffers;
	public int lastBuffer = 0;
	private static short[] audioData;



	@Override
	public void onResume() {
		super.onResume();
		Thread recordingThread = new Thread() {
			@Override
			public void run() {
				intitializeRecording() ;
				
			}
		};

		recordingThread.start();

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_newfourier);
		
		
		System.out.println("fgg");
		TextView tv = new TextView(this);
		Button b = new Button(this);

		try {
			String property = System.getProperty("java.library.path");
			StringTokenizer parser = new StringTokenizer(property, ";");
			while (parser.hasMoreTokens()) {
				System.out.println(parser.nextToken());
			}

			setContentView(tv);
		} catch (java.lang.UnsatisfiedLinkError e) {
			System.err
					.println("--------------------------------------------------------");
			e.printStackTrace();
			System.err
					.println("--------------------------------------------------------");
		}
		// public AudioTrack (int streamType, int sampleRateInHz,
		// int channelConfig, int audioFormat, int bufferSizeInBytes, int mode)

		int sizeInBytes = 1024;
		int offsetInBytes = 120;

		
	}

	

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}

	private void intitializeRecording() {

		try {

			int[] rates = new int[] { 44100, 22050, 16000, 11025, 8000 };

			// add the rates you wish to check against
			buffersize = -1;
			int i = 0;
			while (buffersize < 0 && i < 5) {
				// buffer size is valid, Sample rate supported
				buffersize = 2 * AudioTrack.getMinBufferSize(rates[i],
						AudioFormat.CHANNEL_IN_STEREO,
						AudioFormat.ENCODING_PCM_16BIT);

			}

			Log.i(AUDIO_SERVICE,
					"buffersize will be :" + String.valueOf(buffersize)
							+ "\n sample rate is " + String.valueOf(rates[i]));
			// audioSource, sampleRateInHz, channelConfig, audioFormat,
			// bufferSizeInBytes
			AT = new AudioRecord(AudioSource.MIC, rates[i],
					AudioFormat.CHANNEL_IN_MONO,
					AudioFormat.ENCODING_PCM_16BIT, buffersize);

			Log.i(AUDIO_SERVICE, "\n" + String.valueOf((AT.getState())));
			double c = 0.1;// duration between notifications

			//AT.setPositionNotificationPeriod();
			tracklistener tracklisteneri = new tracklistener();
			AT.setRecordPositionUpdateListener(tracklisteneri);

			
			 * short[] audioData = new short[buffersize];
			 * 
			 * 
			 * AT.read(audioData, 0, buffersize);
			 

			int audioRecordState = AT.getState();
			if (audioRecordState != AudioRecord.STATE_INITIALIZED) {
				finish();
			}
			audioData = new short[buffersize/2];
			AT.startRecording();
			while(true)
			{
				tracklisteneri.onPeriodicNotification(AT);
				Thread.sleep(100000);//(int) c * rates[i]);
			}

		} catch (Exception e) {
			System.err
			.println("--------------------------------------------------------");
	e.printStackTrace();
	System.err
			.println("--------------------------------------------------------");
		}
	}

	public class tracklistener implements OnRecordPositionUpdateListener {

		@Override
		public void onPeriodicNotification(AudioRecord AT) {

			int audioRecordingState = AT.getRecordingState();
		    if(audioRecordingState != AudioRecord.RECORDSTATE_RECORDING) {
		      finish();
		    }
		    String[] pi = new String[audioData.length];
		        int samplesRead =  AT.read(audioData, 0, audioData.length);
			double[] transformed = new double[audioData.length*2];
			
			
			for (int i = 0; i < audioData.length; i++) {
				transformed[i * 2] = audioData[i];
				transformed[i * 2 + 1] = 0;
			}
			try{
			PitchDetect pt = new PitchDetect();
			 pt.fprocess(transformed,transformed.length/2);
			 for (int i = 0; i < transformed.length; i++) {
				 
			 }
			}
			 catch (Exception e)
			 {
				 
				 System.err
					.println("--------------------------------------------------------");
			e.printStackTrace();
			System.err
					.println("--------------------------------------------------------");
			 }
			 
		}

		@Override
		public void onMarkerReached(AudioRecord recorder) {
		}
	}

}*/



/** Copyright (C) 2009 by Aleksey Surkov.
 **
 ** Permission to use, copy, modify, and distribute this software and its
 ** documentation for any purpose and without fee is hereby granted, provided
 ** that the above copyright notice appear in all copies and that both that
 ** copyright notice and this permission notice appear in supporting
 ** documentation.  This software is provided "as is" without express or
 ** implied warranty.
 */      



import java.lang.Thread;
import java.util.HashMap;

import com.audiologic.Fourierwin.DrawableView;
import com.audiologic.Fourierwin.PitchDetect;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;

public class Newfourier extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		tv_ = new DrawableView(this);
		setContentView(tv_);
	}

	@Override
	public void onStart() {
		super.onStart();
		pitch_detector_thread_ = new Thread(new PitchDetect(this, new Handler()));
		pitch_detector_thread_.start();
	}

	@Override
	public void onStop() {
		super.onStop();
		pitch_detector_thread_.interrupt();
	}

	
	public void ShowPitchDetectionResult(
			final HashMap<Double, Double> frequencies,
			final double pitch) {
		tv_.setDetectionResults(frequencies, pitch);
	}

	DrawableView tv_;
	Thread pitch_detector_thread_;
}