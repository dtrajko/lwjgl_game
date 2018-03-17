package audio;

import org.lwjgl.openal.AL10;

public class Source {

	private int sourceId;

	public Source() {
		sourceId = AL10.alGenSources();
	}

	public void play(int buffer) {
		stop();
		AL10.alSourcei(sourceId, AL10.AL_BUFFER, buffer);
		continuePlaying();
	}

	public void delete() {
		// stop();
		AL10.alDeleteSources(sourceId);
	}

	public void setVolume(float volume) {
		AL10.alSourcef(sourceId, AL10.AL_GAIN, volume);
	}

	public void setPitch(float pitch) {
		AL10.alSourcef(sourceId, AL10.AL_PITCH, pitch);
	}

	public void setPosition(float x, float y, float z) {
		AL10.alSource3f(sourceId, AL10.AL_POSITION, x, y, z);
	}

	public void setVelocity(float x, float y, float z) {
		AL10.alSource3f(sourceId, AL10.AL_VELOCITY, x, y, z);
	}

	public void pause() {
		AL10.alSourcePause(sourceId);
	}

	public void continuePlaying() {
		AL10.alSourcePlay(sourceId);
	}

	public void stop() {
		AL10.alSourceStop(sourceId);
	}

	public boolean isPlaying() {
		return AL10.alGetSourcei(sourceId, AL10.AL_SOURCE_STATE) == AL10.AL_PLAYING;
	}

	public void setLooping(boolean loop) {
		AL10.alSourcei(sourceId, AL10.AL_LOOPING, loop ? AL10.AL_TRUE : AL10.AL_FALSE);
	}

	public void setDistanceModel() {
		/**
		 * AL_INVERSE_DISTANCE
		 * AL_INVERSE_DISTANCE_CLAMPED
		 */
		AL10.alDistanceModel(AL10.AL_INVERSE_DISTANCE);
	}

	public void setRolloffFactor(float value) {
		AL10.alSourcef(sourceId, AL10.AL_ROLLOFF_FACTOR, value);
	}

	public void setReferenceDistance(float value) {
		AL10.alSourcef(sourceId, AL10.AL_REFERENCE_DISTANCE, value);
	}

	public void setMaxDistance(float value) {
		AL10.alSourcef(sourceId, AL10.AL_MAX_DISTANCE, value);
	}
}
