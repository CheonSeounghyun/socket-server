package com.temp.socketserver.message;

import java.io.IOException;

public interface MessageParser {
    void receiveMessageParse(byte[] messageData);
    byte[] sendMessageParse(byte messageOpcode) throws IOException;
}
