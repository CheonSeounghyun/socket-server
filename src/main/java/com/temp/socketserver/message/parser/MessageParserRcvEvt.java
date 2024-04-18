package com.temp.socketserver.message.parser;

import com.temp.socketserver.message.MessageParser;


public class MessageParserRcvEvt implements MessageParser {
    @Override
    public void receiveMessageParse(byte[] messageData) {

    }

    @Override
    public byte[] sendMessageParse(byte messageOpcode) {
        return new byte[0];
    }
}
