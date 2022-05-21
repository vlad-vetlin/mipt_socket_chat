package com.socket.client;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Controller {
    private String name = null;

    private final Socket socket;

    private OutputStream outputStream = null;

    public Controller(Socket socket) throws IOException {
        this.socket = socket;
    }

    public boolean hasData(String command) {
        return !command.equals("QUIT");
    }

    public boolean processCommand(String command, String data) throws IOException {
        switch (command) {
            case "SEND_TEXT":
                sendMessage(data);
                return true;
            case "SEND_FILE":
                sendFile(data);
                return true;
            case "QUIT":
                quit();
                return false;
            case "CONNECT":
                connect(data);
                return true;
        }

        return false;
    }

    private void sendMessage(String message) throws IOException {
        if (name == null) {
            System.out.println("Для начала введите имя при помощи команды CONNECT");
            return;
        }

        Message preparedMessage = new Message(name, message);
        outputStream.write(preparedMessage.toByte());
        outputStream.flush();
    }

    private void sendFile(String path) throws IOException {
        if (name == null) {
            System.out.println("Для начала введите имя при помощи команды CONNECT");
            return;
        }

        File file = new File(path.trim());
        Message preparedMessage = new Message(name, Files.readAllBytes(file.toPath().toAbsolutePath()), file.toPath().toString());

        outputStream.write(preparedMessage.toByte());
        outputStream.flush();
    }

    private void quit() throws IOException {
        outputStream.close();
    }

    private void connect(String name) throws IOException {
        outputStream = socket.getOutputStream();
        new Thread(() -> recvMessage(socket)).start();
        this.name = name;
        System.out.println("Добро пожаловать, " + name);
    }

    private static void recvMessage(Socket socket) {
        try {
            while (true) {
                byte[] bytes = new byte[10 * 1024 * 1024];
                InputStream inputStream = socket.getInputStream();
                int len = inputStream.read(bytes);

                Message message = Message.createFromBytes(bytes, len);

                if (message.isFileType()) {
                    Path path = Files.createFile(Paths.get(message.getPath()));
                    BufferedWriter writer = new BufferedWriter(new FileWriter(path.toString()));
                    writer.write(new String(
                            message.getFile(),
                            0,
                            message.getFile().length
                    ));
                    writer.flush();

                    System.out.println("Пользователь " + message.getAuthor() + " отправил вам файл " + path);
                } else {
                    System.out.println("Пользователь " + message.getAuthor() + " прислал вам сообщение " + message.getText());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
