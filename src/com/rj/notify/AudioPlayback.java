/**
 * Taken from https://github.com/MasterEx/BeatKeeper/blob/master/src/pntanasis/android/metronome/AudioGenerator.java
 */
package com.rj.notify;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.AudioTrack.OnPlaybackPositionUpdateListener;
import android.util.Log;

public class AudioPlayback {
	private static final String TAG = AudioPlayback.class.getSimpleName();

	private int sampleRate;

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
		Log.d(TAG, "Creating audio track ");
		// FIXME sometimes audioTrack isn't initialized
		final AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_NOTIFICATION, sampleRate,
				AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT,
				samples.length, AudioTrack.MODE_STATIC);
		audioTrack.write(samples, 0, samples.length);
		audioTrack.setNotificationMarkerPosition(samples.length / 2);
		audioTrack.setPlaybackPositionUpdateListener(new OnPlaybackPositionUpdateListener() {
			@Override
			public void onPeriodicNotification(AudioTrack track) {
			}
			@Override
			public void onMarkerReached(AudioTrack track) {
				try {
					destroyAudioTrack(audioTrack);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		audioTrack.play();
		Log.d(TAG, "Playing audio track "+audioTrack);
	}

	private void destroyAudioTrack(AudioTrack audioTrack) {
		Log.d(TAG, "Destroying audio track "+audioTrack);
		audioTrack.stop();
		audioTrack.release();
	}

}