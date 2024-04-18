package com.temp.socketserver.message;

import com.temp.socketserver.message.parser.MessageParserRcvEvt;

public class MessageParseInit {
    public static MessageParser createMessageParser(byte opcode) {
        return new MessageParserRcvEvt();
    }
}
