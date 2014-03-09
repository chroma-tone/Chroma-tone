package com.audiologic.Fourierwin;


/** Copyright (C) 2009 by Aleksey Surkov.
 **
 ** Permission to use, copy, modify, and distribute this software and its
 ** documentation for any purpose and without fee is hereby granted, provided
 ** that the above copyright notice appear in all copies and that both that
 ** copyright notice and this permission notice appear in supporting
 ** documentation.  This software is provided "as is" without express or
 ** implied warranty.
 */



import java.lang.Runnable;
import java.lang.Thread;
import java.util.HashMap;

import com.audiologic.Fourierwin.Newfourier;

import android.app.AlertDialog;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder.AudioSource;
import android.os.Handler;
import android.util.Log;

public class PitchDetect implements Runnable {
	// Currently, only this combination of rate, encoding and channel mode
	// actually works.
	private int RATE = 8000;
	private final static int CHANNEL_MODE = AudioFormat.CHANNEL_IN_STEREO;
	private final static int ENCODING = AudioFormat.ENCODING_PCM_16BIT;
	private int[] rates = new int[] { 44100, 22050, 16000, 11025, 8000 };

	private int CHUNK_SIZE_IN_SAMPLES = 4096;
	
	private  int CHUNK_SIZE_IN_MS = 1000 * CHUNK_SIZE_IN_SAMPLES
			/ RATE; 
	
	private  int BUFFER_SIZE_IN_BYTES = -1;
	private int CHUNK_SIZE_IN_BYTES = RATE * CHUNK_SIZE_IN_MS
			/ 1000 * 2;
	private int MIN_FREQUENCY = 50; // HZ
	private int MAX_FREQUENCY = 600; // HZ - it's for guitar,
													// should be enough
	private int DRAW_FREQUENCY_STEP = 5;
	private static final String AUDIO_SERVICE = "Fourier Applicaton";

	public native void fprocess(double[] data, int size);  // an NDK library 'fft-jni'
	
	public PitchDetect(Newfourier parent, Handler handler) {
		parent_ = parent;
		handler_ = handler;
		System.loadLibrary("Fourierwin");
 	}

	public void run() {
		android.os.Process
				.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
		
		
		
		
		SelectSamplingFrequency();
		
		
		
		recorder_ = new AudioRecord(AudioSource.MIC, RATE, CHANNEL_MODE,
				ENCODING, BUFFER_SIZE_IN_BYTES);
		if (recorder_.getState() != AudioRecord.STATE_INITIALIZED) {
			ShowError("Can't initialize AudioRecord");
			return;
		}
		short[] audio_data = new short[BUFFER_SIZE_IN_BYTES / 2];
		double[] data = new double[BUFFER_SIZE_IN_BYTES];
		final int min_frequency_fft = Math.round(MIN_FREQUENCY
				* CHUNK_SIZE_IN_SAMPLES / RATE);
		final int max_frequency_fft = Math.round(MAX_FREQUENCY
				* CHUNK_SIZE_IN_SAMPLES / RATE);
		while (!Thread.interrupted()) {
			recorder_.startRecording();
			recorder_.read(audio_data, 0, BUFFER_SIZE_IN_BYTES / 2);
			recorder_.stop();
			for (int i = 0; i < BUFFER_SIZE_IN_BYTES ; i++) {
				data[i*2] = new Short(audio_data[i]).doubleValue();
				data[i*2+1] = 0.0;
			}
			fprocess(data, CHUNK_SIZE_IN_SAMPLES * 2);
			double best_frequency = min_frequency_fft;
			double best_amplitude = 0;
			HashMap<Double, Double> frequencies = new HashMap<Double, Double>();
			final double draw_frequency_step = 1.0 * RATE
					/ CHUNK_SIZE_IN_SAMPLES;
			for (int i = min_frequency_fft; i <= max_frequency_fft; i++) {
				final double current_frequency = i * 1.0 * RATE
						/ CHUNK_SIZE_IN_SAMPLES;
				final double draw_frequency = Math
						.round((current_frequency - MIN_FREQUENCY)
								/ DRAW_FREQUENCY_STEP)
						* DRAW_FREQUENCY_STEP + MIN_FREQUENCY;
				final double current_amplitude = Math.pow(data[i * 2], 2)
						+ Math.pow(data[i * 2 + 1], 2);
				final double normalized_amplitude = current_amplitude * 
				    Math.pow(MIN_FREQUENCY * MAX_FREQUENCY, 0.5) / current_frequency; 
				Double current_sum_for_this_slot = frequencies
						.get(draw_frequency);
				if (current_sum_for_this_slot == null)
					current_sum_for_this_slot = 0.0;
				frequencies.put(draw_frequency, Math
						.pow(current_amplitude, 0.5)
						/ draw_frequency_step + current_sum_for_this_slot);
				if (normalized_amplitude > best_amplitude) {
					best_frequency = current_frequency;
					best_amplitude = normalized_amplitude;
				}
			}
 			PostToUI(frequencies, best_frequency);
		}
	}
	
	private void SelectSamplingFrequency()
	{
		
		

		// add the rates you wish to check against
		int i = 0;
		while (BUFFER_SIZE_IN_BYTES < 0 && i < rates.length) {
			// buffer size is valid, Sample rate supported

			BUFFER_SIZE_IN_BYTES = AudioRecord.getMinBufferSize(rates[i], CHANNEL_MODE, ENCODING );

		}
		RATE = rates[i];
		CHUNK_SIZE_IN_SAMPLES = (int) Math.round(RATE * 0.8);
		
		CHUNK_SIZE_IN_MS = CHUNK_SIZE_IN_SAMPLES * 1000 /RATE;
		CHUNK_SIZE_IN_BYTES = RATE * CHUNK_SIZE_IN_MS/ 1000 * 2;
		

		Log.i(AUDIO_SERVICE,
				"buffersize will be :" + String.valueOf(BUFFER_SIZE_IN_BYTES)
						+ "\n sample rate is " + String.valueOf(RATE));
		
		
		
		
	}

	private void PostToUI(
			final HashMap<Double, Double> frequencies,
			final double pitch) {
		handler_.post(new Runnable() {
			public void run() {
				parent_.ShowPitchDetectionResult(frequencies, pitch);
			}
		});
	}
	
	private void ShowError(final String msg) {
		handler_.post(new Runnable() {
			public void run() {
		       new AlertDialog.Builder(parent_)
	           .setTitle("GuitarTuner")
	           .setMessage(msg)
	           .show();
			}
	    });
	}

	private Newfourier parent_;
	private AudioRecord recorder_;
	private Handler handler_;
}
