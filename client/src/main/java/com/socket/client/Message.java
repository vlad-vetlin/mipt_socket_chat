package com.socket.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import java.io.IOException;

@Data
public class Message {
    private static ObjectMapper objectMapper = new ObjectMapper();

    private String text;

    private boolean fileType;

    private byte[] file;

    private String path;

    private String author;

    public static Message createFromBytes(byte[] bytes, int len) throws IOException {
        return objectMapper.readValue(bytes, 0, len, Message.class);
    }

    public Message() {

    }

    public Message(String author, String text) {
        this.text = text;
        this.fileType = false;
        this.file = null;
        this.author = author;
        this.path = null;
    }

    public Message(String author, byte[] file, String path) {
        this.text = null;
        this.fileType = true;
        this.file = file;
        this.author = author;
        this.path = path;
    }

    public byte[] toByte() {
        try {
            return objectMapper.writeValueAsBytes(this);
        } catch (JsonProcessingException e) {
            System.out.println("Невозможно преобразовать!");
        }

        return new byte[] {};
    }
}
