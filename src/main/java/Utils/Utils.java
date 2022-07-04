package Utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.Random;

public class Utils {
    private static Logger log = LogManager.getLogger(Utils.class);

    public static String buildErrorMessage(String actual, String expected, String prefix) {
        return String.format("%s - Expected value is '%s' but was '%s'", prefix, expected, actual);
    }

    public static String buildErrorMessage(Object actual, Object expected, String prefix) {
        return String.format("%s - Expected value is '%s' but was '%s'", prefix, expected, actual);
    }

    public static String buildErrorMessage(String actual, String expected) {
        return String.format("Expected value is '%s' but was '%s'", expected, actual);
    }

    public static String buildErrorMessageExists(String prefix) {
        return String.format("%s does not exist", prefix);
    }

    public static String buildErrorMessageNotExist(String prefix) {
        return String.format("%s exists", prefix);
    }

    public static void delay(int timeInSecond) {
        try {
            Thread.sleep(timeInSecond);
        } catch (InterruptedException ignored) {
        }
    }

    public static boolean compareLists(List<String> actual, List<String> expected) {
        return actual.size() == expected.size() && actual.containsAll(expected);
    }

    public static <T> T getRandomElement(List<T> list) {
        return list.get(new Random().nextInt(list.size()));
    }

    public static void writeJsonToFile(File file, Object content) {
        try {
            Writer writer = new FileWriter(file);
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(content, writer);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int randomInt(int min, int max) {
        Random rand = new Random();
        return min + rand.nextInt((max - min) + 1);
    }

    public static void writeFile(String fileName, String content) {
        try {
            RandomAccessFile stream = new RandomAccessFile(fileName, "rw");
            FileChannel channel = stream.getChannel();
            byte[] strBytes = content.getBytes();
            ByteBuffer buffer = ByteBuffer.allocate(strBytes.length);
            buffer.put(strBytes);
            buffer.flip();
            channel.write(buffer);
            stream.close();
            channel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
