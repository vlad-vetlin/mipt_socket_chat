package com.socket.server;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ChatServer {
    private static final List<Socket> clientSockets = new ArrayList<>();

    private static void initServer(Integer port) throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("Сервер успешно запущен");

        while (true) {
            Socket socket = serverSocket.accept();
            new Thread(() -> transfer(socket)).start();
            clientSockets.add(socket);
        }

    }

    private static void transfer(Socket currentSocket){
        while(true) {
            try {
                byte[] bytes = new byte[10 * 1024 * 1024];
                InputStream inputStream = currentSocket.getInputStream();
                int len = inputStream.read(bytes);
                for (Socket socket : clientSockets) {
                    if (socket == currentSocket){
                        continue;
                    }

                    System.out.println(
                            "Получено сообщение от клиента:" +
                            socket.getRemoteSocketAddress().toString() +
                            ", сообщение " +
                            new String(
                                    bytes,
                                    0,
                                    len,
                                    StandardCharsets.UTF_8
                            )
                    );
                    socket.getOutputStream().write(bytes);
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
                throw new RuntimeException();
            }
        }
    }

    public static void main(String[] args) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            Config config = objectMapper.readValue(new File("../src/main/resources/configs.json"), Config.class);
            System.out.println(config.getPort());
            initServer(config.getPort());
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}
