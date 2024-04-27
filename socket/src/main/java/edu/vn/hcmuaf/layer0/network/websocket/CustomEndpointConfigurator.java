package edu.vn.hcmuaf.layer0.network.websocket;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.websocket.HandshakeResponse;
import jakarta.websocket.server.HandshakeRequest;
import jakarta.websocket.server.ServerEndpointConfig;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.URL;

public class CustomEndpointConfigurator extends ServerEndpointConfig.Configurator {

    @Override
    public void modifyHandshake(ServerEndpointConfig config, HandshakeRequest request, HandshakeResponse response) {
        HttpServletRequest httpservletRequest = getField(request, HttpServletRequest.class);
        String sClientIP = httpservletRequest.getRemoteAddr();
        config.getUserProperties().put("clientIp", sClientIP);
        config.getUserProperties().put("idPrivateConnect", isPrivateIPAddress(sClientIP));
    }

    //hacking reflector to expose fields...
    private static < I, F > F getField(I instance, Class < F > fieldType) {
        try {
            for (Class < ? > type = instance.getClass(); type != Object.class; type = type.getSuperclass()) {
                for (Field field: type.getDeclaredFields()) {
                    if (fieldType.isAssignableFrom(field.getType())) {
                        field.setAccessible(true);
                        return (F) field.get(instance);
                    }
                }
            }
        } catch (Exception e) {
            // Handle?
        }
        return null;
    }
    public static boolean isPrivateIPAddress(String ipAddress) {
        try {
            // Convert the IP address string to an array of integers
            String[] ipParts = ipAddress.split("\\.");
            int[] ipIntParts = new int[4];
            for (int i = 0; i < 4; i++) {
                ipIntParts[i] = Integer.parseInt(ipParts[i]);
            }

            // Check for private IP address ranges
            if (ipIntParts[0] == 10
                    || (ipIntParts[0] == 172 && ipIntParts[1] >= 16 && ipIntParts[1] <= 31)
                    || (ipIntParts[0] == 192 && ipIntParts[1] == 168)) {
                return true;
            }

            // Check for localhost IP address
            if (ipIntParts[0] == 127 && ipIntParts[1] == 0 && ipIntParts[2] == 0 && ipIntParts[3] == 1) {
                return true;
            }

            // Check for link-local IP address (169.254.0.0/16)
            if (ipIntParts[0] == 169 && ipIntParts[1] == 254) {
                return true;
            }

            // Check for unique local address (ULA) (fc00::/7 in IPv6)
            if (ipParts.length == 8 && ipParts[0].equalsIgnoreCase("fc")) {
                return true;
            }
        }catch (NumberFormatException e){
            return false;
        }
        return false;
    }
}