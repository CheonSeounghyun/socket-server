package com.temp.socketserver.message;

import java.io.IOException;

public class MessageController {

    public byte[] dataProcess(byte[] message) throws IOException {

        byte[] responseBuffer;
        byte opcode = message[9];

        MessageParser messageParser = MessageParseInit.createMessageParser(opcode);
        messageParser.receiveMessageParse(message);
        responseBuffer = messageParser.sendMessageParse(opcode);

        return responseBuffer;
    }

}
