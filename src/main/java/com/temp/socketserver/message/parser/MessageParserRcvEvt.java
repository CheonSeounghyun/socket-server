package com.temp.socketserver.message.parser;

import com.temp.socketserver.message.Command;
import com.temp.socketserver.message.MessageParser;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;


public class MessageParserRcvEvt implements MessageParser {

    private int dataLength;
    public String opcode;
    public String file_nm;
    public String file_ext;
    public String file_size;
    public byte[] file_data;

    @Override
    public void receiveMessageParse(byte[] messageData) {

        dataLength = Command.getDataLength(messageData);
        byte[] dataBytes = new byte[dataLength];
        System.arraycopy(messageData, Command.MESSAGE_SOM_LENGTH+Command.MESSAGE_TYPE_LENGTH+Command.MESSAGE_HEADER_LENGTH, dataBytes, 0, dataLength);

        int offset = 0;

        opcode = Command.byteToHexASCII(dataBytes[0]);
        offset++;

        file_nm = new String(dataBytes, offset, Evt.INTERIDX_RCVEVTDATA_FILENM, Charset.forName("UTF-8")).trim();
        offset += Evt.INTERIDX_RCVEVTDATA_FILENM;

        file_ext = new String(dataBytes, offset, Evt.INTERIDX_RCVEVTDATA_FILEEXT, Charset.forName("UTF-8")).trim();
        offset += Evt.INTERIDX_RCVEVTDATA_FILEEXT;

        byte[] fileSizeBytes = new byte[Evt.INTERIDX_RCVEVTDATA_FILESIZE];
        System.arraycopy(dataBytes, offset, fileSizeBytes, 0, Evt.INTERIDX_RCVEVTDATA_FILESIZE);
        file_size = String.valueOf(Command.byteArrayToInt(fileSizeBytes));
        offset += Evt.INTERIDX_RCVEVTDATA_FILESIZE;

        byte[] fileDataBytes = new byte[dataLength - offset];
        System.arraycopy(dataBytes, offset, fileDataBytes, 0, dataLength - offset);
        file_data = fileDataBytes;
    }

    @Override
    public byte[] sendMessageParse(byte messageOpcode) {
        //송신 메세지 패킷 생성
        byte[] interfaceData = new byte[14];

        int offset = 0;

        SimpleDateFormat sdformat = new SimpleDateFormat("yyyyMMddHHmmss");
        String currentDate = sdformat.format(new java.util.Date());
        System.arraycopy(currentDate.getBytes(StandardCharsets.UTF_8), 0, interfaceData, offset, 14);
        offset += 14;

        return interfaceData;
    }
}
