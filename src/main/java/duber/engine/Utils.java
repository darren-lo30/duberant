package duber.engine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

import org.lwjgl.BufferUtils;

import static org.lwjgl.BufferUtils.*;

public class Utils {
    private Utils(){}

    public static String loadResource(String fileName) throws IOException {
        String result;
        try(InputStream in = Utils.class.getResourceAsStream(fileName);
            Scanner sc = new Scanner(in, java.nio.charset.StandardCharsets.UTF_8.name())){
                result = sc.useDelimiter("\\A").next();
        }

        return result;
    }

    public static List<String> readAllLines(String fileName) throws IOException {
        List<String> allLines = new ArrayList<>();
        try(BufferedReader br = new BufferedReader(new InputStreamReader(Utils.class.getResourceAsStream(fileName)))){
            String nextLine;
            while((nextLine = br.readLine()) != null){
                allLines.add(nextLine);
            }
        }
        return allLines;
    }

    public static float[] listToFloatArray(List<Float> list){
        float[] floatArr = new float[list.size()];
        for(int i = 0; i<list.size(); i++){
            floatArr[i] = list.get(i);
        }

        return floatArr;
    }

    public static int[] listToIntArray(List<Integer> list){
        return list.stream().mapToInt(i -> i).toArray();
    }

    public static ByteBuffer ioResourceToByteBuffer(String resource, int bufferSize) throws IOException {
        ByteBuffer buffer;

        Path path = Paths.get(resource);
        if (Files.isReadable(path)) {
            try (SeekableByteChannel fc = Files.newByteChannel(path)) {
                buffer = BufferUtils.createByteBuffer((int) fc.size() + 1);
                while (fc.read(buffer) != -1) ;
            }
        } else {
            try (
                    InputStream source = Utils.class.getResourceAsStream(resource);
                    ReadableByteChannel rbc = Channels.newChannel(source)) {
                buffer = createByteBuffer(bufferSize);

                while (true) {
                    int bytes = rbc.read(buffer);
                    if (bytes == -1) {
                        break;
                    }
                    if (buffer.remaining() == 0) {
                        buffer = resizeBuffer(buffer, buffer.capacity() * 2);
                    }
                }
            }
        }

        buffer.flip();
        return buffer;
    }

    private static ByteBuffer resizeBuffer(ByteBuffer buffer, int newCapacity) {
        ByteBuffer newBuffer = BufferUtils.createByteBuffer(newCapacity);
        buffer.flip();
        newBuffer.put(buffer);
        return newBuffer;
    }
}