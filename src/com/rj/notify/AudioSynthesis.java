package com.rj.notify;

import java.util.Arrays;

import midiReference.MidiReference;
import midiReference.NoteReference;
import midiReference.ScaleReference;
import android.util.Log;


public class AudioSynthesis {
	private String TAG = this.getClass().getSimpleName();

	public static interface AudioGenerator {
		public double[] generateAudioPattern(int samples, int sampleRate, double[] frequencies);
	}

	private final AudioPlayback playback;
	private AudioGenerator audioGenerator;
	private MidiReference midiReference;
	
	public AudioSynthesis() {
		playback = new AudioPlayback(44100/2);
		audioGenerator = new DefaultAudioGenerator();
		midiReference = MidiReference.getMidiReference();
	}
	
	public void onNotification(String user, String app, String text) {
		double[] notes = generateNotes(user, app, text);
		playback.playSound(audioGenerator.generateAudioPattern(44100/5, 44100/2, notes));
	}
	
	private double[] generateNotes(String user, String app, String text) {
		int[] scale = MidiReference.createScale(ScaleReference.MAJOR, NoteReference.C);
		int startNote = 55;
		Log.d(TAG, "Scale: "+Arrays.toString(scale));
		String appCharacters = parsePackage(app);
		String textCharacters = parseText(text);
		String allChars = appCharacters + textCharacters;
		allChars = allChars.toLowerCase();
		Log.d(TAG, String.format("App: %s, App chars: %s, Text: %s, Text chars: %s, Total: %s", app, appCharacters, text, textCharacters, allChars));
		int[] notes = new int[allChars.length()];
		double[] frequencies = new double[notes.length];
		for (int i = 0; i < allChars.length(); i++) {
			int c = allChars.codePointAt(i);
			int indexInScale = c % scale.length;
			int note = scale[indexInScale] + startNote;
			notes[i] = note;
			frequencies[i] = midiReference.getNoteFrequency(note);
		}
		Log.d(TAG, String.format("Notes: %s, Frequencies: %s", Arrays.toString(notes), Arrays.toString(frequencies)));
		return frequencies;
	}
	
	/**
	 *  Returns the 'most important 3' characters from a package name, as defined by my
	 *  completely arbitrary metrics.
	 * @param packagename
	 * @return
	 */
	private String parsePackage(String packagename) {
		packagename = packagename.replaceFirst("com.", "");
		String[] packages = packagename.split("\\.");
		for (int i = 0; i < packages.length; i++) {
			if (packages[i].length() < 3) {
				packages[i] = packages[i] + "   ";
			}
		}
		Log.d(TAG, "Packages: "+Arrays.toString(packages));
		if (packages.length == 1) {
			return packages[0].substring(0, 3);
		} else if (packages.length == 2) {
			return packages[0].substring(0, 2) + packages[1].substring(0, 1);
		} else {
			return packages[0].substring(0, 1) +
				   packages[packages.length - 2].substring(0, 1) + 
				   packages[packages.length - 1].substring(0, 1);
		}
	}
	
	/**
	 * Returns 2 arbitrary characters from a snippit of text.
	 * @param text
	 * @return
	 */
	private String parseText(String text) {
		return "" + text.charAt(0) + text.charAt(text.length() - 1);
	}
	
	
	
	private class DefaultAudioGenerator implements AudioGenerator {
		@Override
		public double[] generateAudioPattern(int samples, int sampleRate, double[] frequencies) {
			double[] sample = new double[samples];
			final int numFrequencies = frequencies.length;
			final double samplesPerFrequency = samples / numFrequencies;
			int itotal = 0;
			for (int f = 0; f < numFrequencies; f++) {
				final double frequency = frequencies[f];
				final double samplesPerCycle = sampleRate / frequency;
				for (double i = 0; i < samplesPerFrequency; i++) {
					double amp = Math.sin(i / samplesPerFrequency * Math.PI);
					// sample[itotal++] = Math.sin(2 * Math.PI * i / (sampleRate / frequency)) * amp; // SINE WAVE
					// sample[itotal++] = Math.abs( (i % (samplesPerCycle)) / samplesPerCycle - 0.5 ) * 2 * amp; // TRIANGLE WAVE
					sample[itotal++] = Math.pow(Math.abs( (i % (samplesPerCycle)) / samplesPerCycle - 0.5 ) * 2, 1.5) * amp; // TRIANGLE CURVE WAVE
				}
			}
			return sample;
		}
	}
}
