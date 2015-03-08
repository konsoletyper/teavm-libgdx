package org.teavm.libgdx;

import com.badlogic.gdx.Audio;
import com.badlogic.gdx.audio.AudioDevice;
import com.badlogic.gdx.audio.AudioRecorder;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;

/**
 *
 * @author Alexey Andreev
 */
public class TeaVMAudio implements Audio {
    @Override
    public AudioDevice newAudioDevice(int samplingRate, boolean isMono) {
        throw new GdxRuntimeException("AudioDevice not supported by TeaVM backend");
    }

    @Override
    public AudioRecorder newAudioRecorder(int samplingRate, boolean isMono) {
        throw new GdxRuntimeException("AudioDevice not supported by TeaVM backend");
    }

    @Override
    public Sound newSound(FileHandle fileHandle) {
        return new TeaVMSound((TeaVMFileHandle)fileHandle);
    }

    @Override
    public Music newMusic(FileHandle file) {
        return new TeaVMMusic((TeaVMFileHandle)file);
    }
}
