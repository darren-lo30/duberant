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

/**
 * A buffer that stores sound data.
 * @author Darren Lo
 * @version 1.0
 */
public class SoundBuffer implements Cleansable {
    /** This sound buffer's id. */
    private final int id;

    /** This sound buffer's pcm. */
    private ShortBuffer pulseCodedModulation;

    /**
     * Constructs a SoundBuffer from a file.
     * @throws IOException if the sound file could not be loaded
     */
    public SoundBuffer(String file) throws IOException {
        id = alGenBuffers();
        try(STBVorbisInfo info = STBVorbisInfo.malloc()){
            pulseCodedModulation = readVorbis(file, 32 * 1024, info);
            alBufferData(id, info.channels() == 1 ? AL_FORMAT_MONO16 : AL_FORMAT_STEREO16, 
                pulseCodedModulation, info.sample_rate());
        }
    }

    /**
     * Gets this SoundBuffer's id.
     * @return the id
     */
    public int getId(){
        return id;
    }

    /**
     * Reads a vorbis file.
     * @param file the file to read
     * @param bufferSize the size of the buffer
     * @param info the vorbis info
     * @throws IOException if the file could not be loaded
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void cleanup(){
        alDeleteBuffers(id);
        if (pulseCodedModulation != null){
            MemoryUtil.memFree(pulseCodedModulation);
        }
    }


}