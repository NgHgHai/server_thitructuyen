package edu.vn.hcmuaf.layer2;

import org.apache.log4j.Logger;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class CompressUtils {

    private static Logger logger = Logger.getLogger(CompressUtils.class);

    public static byte[] compress(String json) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             ZipOutputStream zipOut = new ZipOutputStream(outputStream)) {
            ZipEntry zipEntry = new ZipEntry("data.json");
            zipOut.putNextEntry(zipEntry);
            zipOut.write(json.getBytes());
            zipOut.closeEntry();
            return outputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new byte[]{};
    }


    public static <T extends Serializable> byte[] compress(T obj) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
            // Write the object to the ObjectOutputStream
            objectOutputStream.writeObject(obj);
            objectOutputStream.close();
            // Get the byte array from the ByteArrayOutputStream
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T extends Serializable> T decompress(byte[] data, Class<T> desire) {
        try (ByteArrayInputStream byteIn = new ByteArrayInputStream(data);
             ObjectInputStream objectIn = new ObjectInputStream(byteIn)) {
            Object deserializedObject = objectIn.readObject();
            return desire.cast(deserializedObject);
        } catch (IOException | ClassNotFoundException e) {
            logger.error("decompress error", e);
            return null;
        }
    }
}
