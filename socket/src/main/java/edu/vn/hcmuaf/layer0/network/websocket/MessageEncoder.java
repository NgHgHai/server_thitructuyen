package edu.vn.hcmuaf.layer0.network.websocket;

import edu.vn.hcmuaf.layer2.proto.Proto;

import jakarta.websocket.EncodeException;
import jakarta.websocket.Encoder;
import jakarta.websocket.EndpointConfig;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;

public class MessageEncoder implements Encoder.BinaryStream<Proto.PacketWrapper> {

//        @SneakyThrows
    @Override
    public void encode(Proto.PacketWrapper packetWrapper, OutputStream outputStream) throws IOException {
//        outputStream.write(packetWrapper.toByteArray());
//        outputStream.flush();
        if(packetWrapper == null || packetWrapper.toByteArray().length<=1) return;

        ByteArrayInputStream baos = new ByteArrayInputStream(packetWrapper.toByteArray());
        byte[] bytes = new byte[1024];
        int i;
        while ((i = baos.read(bytes)) != -1) {
            outputStream.write(bytes, 0, i);
            outputStream.flush();
        }
        outputStream.close();
    }

    @Override
    public void init(EndpointConfig endpointConfig) {

    }

    @Override
    public void destroy() {

    }


}