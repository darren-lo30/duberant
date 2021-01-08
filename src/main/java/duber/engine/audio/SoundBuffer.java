package duber.engine.audio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import static org.lwjgl.openal.AL10.*;
import org.lwjgl.stb.STBVorbisInfo;
import java.nio.ShortBuffer;
import static org.lwjgl.stb.STBVorbis.*;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import duber.engine.utilities.Utils;

import static org.lwjgl.system.MemoryUtil.*;

public class SoundBuffer {
    private final int bufferId;
    private ShortBuffer pulseCodedModulation;

    public SoundBuffer(String file) throws IOException {
        bufferId = alGenBuffers();
        try(STBVorbisInfo info = STBVorbisInfo.malloc()){
            pulseCodedModulation = readVorbis(file, 32 * 1024, info);
            alBufferData(bufferId, info.channels() == 1 ? AL_FORMAT_MONO16 : AL_FORMAT_STEREO16, 
                pulseCodedModulation, info.sample_rate());
        }
    }

    public int getBufferId(){
        return bufferId;
    }

    private ShortBuffer readVorbis(String file, int bufferSize, STBVorbisInfo info) throws IOException {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            ByteBuffer vorbis = Utils.ioResourceToByteBuffer(file, bufferSize);
            IntBuffer error = stack.mallocInt(1);
            long decoder = stb_vorbis_open_memory(vorbis, error, null);
            if (decoder == NULL) {
                throw new IOException("Failed to open Ogg Vorbis file. Error: " + error.get(0));
            }

            stb_vorbis_get_info(decoder, info);

            int channels = info.channels();

            int lengthSamples = stb_vorbis_stream_length_in_samples(decoder);

            pulseCodedModulation = MemoryUtil.memAllocShort(lengthSamples);

            pulseCodedModulation.limit(stb_vorbis_get_samples_short_interleaved(decoder, channels, pulseCodedModulation) * channels);
            stb_vorbis_close(decoder);

            return pulseCodedModulation;
        }
    }

    public void cleanup(){
        alDeleteBuffers(bufferId);
        if(pulseCodedModulation != null){
            MemoryUtil.memFree(pulseCodedModulation);
        }
    }


}