package com.socket.client;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class ChatClient {
    public static final String ip = "127.0.0.1";

    public static void main(String[] args) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            Config config = objectMapper.readValue(
                    new File("../src/main/resources/configs.json"),
                    Config.class
            );
            initClient(config.getPort());
        }catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void initClient(Integer port) throws IOException {
        Socket socket = new Socket(ip, port);
        Controller controller = new Controller(socket);

        while (true) {
            System.out.println("Введите команду");

            Scanner scanner = new Scanner(System.in);
            String command = scanner.next();

            String data = null;
            if (controller.hasData(command)) {
                data = scanner.nextLine();
            }

            boolean response = controller.processCommand(command, data);
            if (!response) {
                break;
            }
        }
    }


}
