package com.rj.notify;

import java.util.Arrays;

import midiReference.MidiReference;
import midiReference.NoteReference;
import midiReference.ScaleReference;
import android.annotation.SuppressLint;
import android.util.Log;


@SuppressLint("DefaultLocale") 
public class AudioSynthesis {
	private String TAG = this.getClass().getSimpleName();

	public static interface AudioGenerator {
		public double[] generateAudioPattern(int samples, int sampleRate, NotePattern pattern);
	}
	
	private static class NotePattern {
		public final double[] frequencies;
		public final double[] volumes;
		
		public NotePattern(double[] frequencies, double[] volumes) {
			this.frequencies = frequencies;
			this.volumes = volumes;
		}
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
		NotePattern notes = generateNotes(user, app, text);
		playback.playSound(audioGenerator.generateAudioPattern(44100/5, 44100/2, notes));
		// At the moment, I don't think this is needed.
		// removePlayerWhenDone();
	}
	
	private void removePlayerWhenDone() {
		new Thread() {
			public void run() {
				try {
					Thread.sleep(3000);
					playback.destroyAudioTrack();
				} catch (Exception e) {
					// Don't care.
					e.printStackTrace();
				}
			}
		}.start();
	}
	
	private NotePattern generateNotes(String user, String app, String text) {
		int[] scaleSmall = MidiReference.createScale(ScaleReference.MAJOR, NoteReference.C);
		int[] scale = new int[scaleSmall.length * 2];
		for (int i = 0; i < scaleSmall.length; i++) {
			scale[i] = scaleSmall[i];
			scale[i + scaleSmall.length] = scaleSmall[i] + 12;
		}
		int startNote = 60; // This number can be tweaked to give very different characteristics.
		Log.d(TAG, "Scale: "+Arrays.toString(scale));
		String appCharacters = parsePackage(app);
		String textCharacters = parseText(text);
		String allChars = appCharacters + textCharacters;
		allChars = allChars.toLowerCase();
		Log.d(TAG, String.format("App: %s, App chars: %s, Text: %s, Text chars: %s, Total: %s", app, appCharacters, text, textCharacters, allChars));
		int[] notes = new int[allChars.length()];
		double[] frequencies = new double[notes.length];
		double[] volumes = new double[notes.length];
		for (int i = 0; i < allChars.length(); i++) {
			int c = allChars.codePointAt(i);
			int indexInScale = c % scale.length;
			int note = scale[indexInScale];
			notes[i] = note;
			frequencies[i] = midiReference.getNoteFrequency(note + startNote);
			volumes[i] = Math.min(1, ((double)scale.length - indexInScale) / scale.length + 0.4);
		}
		Log.d(TAG, String.format("Notes: %s, Frequencies: %s, Volumes: %s", Arrays.toString(notes), Arrays.toString(frequencies), Arrays.toString(volumes)));
		return new NotePattern(frequencies, volumes);
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
		if (text.length() == 0) {
			return "  ";
		} else if (text.length() == 1) {
			return text + " ";
		} else {
			return text.substring(0, 2);
		}
	}
	
	
	
	private class DefaultAudioGenerator implements AudioGenerator {
		@Override
		public double[] generateAudioPattern(int samples, int sampleRate, NotePattern pattern) {
			double[] sample = new double[samples];
			final double vol = 0.8;
			final int numFrequencies = pattern.frequencies.length;
			final double samplesPerFrequency = samples / numFrequencies;
			int itotal = 0;
			for (int f = 0; f < numFrequencies; f++) {
				final double frequency = pattern.frequencies[f];
				final double noteVolume = pattern.volumes[f];
				final double samplesPerCycle = sampleRate / frequency;
				for (double i = 0; i < samplesPerFrequency; i++) {
					double amp = Math.sin(i / samplesPerFrequency * Math.PI) * noteVolume * vol;
					// sample[itotal++] = Math.sin(2 * Math.PI * i / (sampleRate / frequency)) * amp; // SINE WAVE
					// sample[itotal++] = Math.abs( (i % (samplesPerCycle)) / samplesPerCycle - 0.5 ) * 2 * amp; // TRIANGLE WAVE
					sample[itotal++] = Math.pow(Math.abs( (i % (samplesPerCycle)) / samplesPerCycle - 0.5 ) * 2, 1.2) * amp; // TRIANGLE CURVE WAVE
				}
			}
			return sample;
		}
	}
}
