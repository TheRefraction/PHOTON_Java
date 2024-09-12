package photon.audio;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.LWJGLException;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.util.WaveData;

import photon.system.LogManager;

public class AudioMaster {
	
	private static List<Integer> buffers = new ArrayList<Integer>();;

	public static void init() {
		LogManager.info("Initializing OpenAL...");
		try {
			AL.create();
		} catch (LWJGLException e) {
			LogManager.severe("OpenAL couldn't be initialized!");
			e.printStackTrace();
		}
	}
	
	public static void cleanUp() {
		AL.destroy();
	}
	
	public static int loadSound(String file) {
		int buffer = AL10.alGenBuffers();
		buffers.add(buffer);
		WaveData waveFile = WaveData.create(file);
		AL10.alBufferData(buffer, waveFile.format, waveFile.data, waveFile.samplerate);
		waveFile.dispose();
		return buffer;
	}
	
	public static void setListenerData(float x, float y, float z) {
		AL10.alListener3f(AL10.AL_POSITION, x, y, z);
		AL10.alListener3f(AL10.AL_VELOCITY, 0, 0, 0);
	}
}
