package edu.vn.hcmuaf.layer0.network.websocket;

import edu.vn.hcmuaf.layer2.proto.Proto;

import jakarta.websocket.Decoder;
import jakarta.websocket.EndpointConfig;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MessageDecoder implements Decoder.BinaryStream<Proto.PacketWrapper> {

    @Override
    public Proto.PacketWrapper decode(InputStream inputStream) throws IOException {
        byte[] bytes2 = readAllBytes(inputStream);
        return Proto.PacketWrapper.parseFrom(bytes2);
    }

    @Override
    public void init(EndpointConfig endpointConfig) {

    }

    @Override
    public void destroy() {

    }

    public byte[] readAllBytes(InputStream is) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[2048];
        int bytesRead;

        while ((bytesRead = is.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }

        return output.toByteArray();
    }


}