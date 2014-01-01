/**
 * Taken from https://github.com/MasterEx/BeatKeeper/blob/master/src/pntanasis/android/metronome/AudioGenerator.java
 */
package com.rj.notify;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

public class AudioPlayback {

	private int sampleRate;
	private AudioTrack audioTrack;

	public AudioPlayback(int sampleRate) {
		this.sampleRate = sampleRate;
	}

	public byte[] get16BitPcm(double[] samples) {
		byte[] generatedSound = new byte[2 * samples.length];
		int index = 0;
		for (double sample : samples) {
			// scale to maximum amplitude
			short maxSample = (short) ((sample * Short.MAX_VALUE));
			// in 16 bit wav PCM, first byte is the low order byte
			generatedSound[index++] = (byte) (maxSample & 0x00ff);
			generatedSound[index++] = (byte) ((maxSample & 0xff00) >>> 8);
		}
		return generatedSound;
	}
	
	public void playSound(double[] samples) {
		byte[] generatedSound = get16BitPcm(samples);
		playRawSound(generatedSound);
	}

	public void playRawSound(byte[] samples) {
		// FIXME sometimes audioTrack isn't initialized
		// If we still have a pending audio track, delete it before we create a new one.
		if (audioTrack != null) {
			destroyAudioTrack();
		}
		audioTrack = new AudioTrack(AudioManager.STREAM_NOTIFICATION, sampleRate,
				AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT,
				samples.length, AudioTrack.MODE_STATIC);
		audioTrack.write(samples, 0, samples.length);
		audioTrack.play();
	}

	public void destroyAudioTrack() {
		audioTrack.stop();
		audioTrack.release();
		audioTrack = null;
	}

}