package com.temp.socketserver.message;

import java.nio.ByteBuffer;

public class Command {

    // Common
    public static final byte SOM = (byte) 0x82;
    public static final byte EOM = (byte) 0x83;
    public static int MESSAGE_HEADER_TYPE_REQ = 1;
    public static int MESSAGE_HEADER_TYPE_RES = 2;

    // Message Packet Length
    public static int MESSAGE_SOM_LENGTH = 1;
    public static int MESSAGE_TYPE_LENGTH = 4;
    public static int MESSAGE_HEADER_LENGTH = 4;
    public static int MESSAGE_CRC_LENGTH = 2;
    public static int MESSAGE_EOM_LENGTH = 1;

    // SOM Check
    public static boolean isSOM(byte first){
        boolean flag = true;
        if(first == SOM){
            flag = true;
        }else{
            flag = false;
        }
        return flag;
    }

    // EOM Check
    public static boolean isEOM(byte first){
        boolean flag = true;
        if(first == EOM){
            flag = true;
        }else{
            flag = false;
        }
        return flag;
    }

    // byteArrayToInt
    public static int byteArrayToInt(byte[] bytes) {
        int value = 0;
        for (int i = 0; i < bytes.length; i++) {
            value |= (bytes[i] & 0xFF) << (8 * i);
        }
        return value;
    }

    // byte to hex
    public static String byteToHexASCII(byte value) {
        // byte 값을 16진수 아스키 코드로 변환
        String hexString = Integer.toHexString(value & 0xFF).toUpperCase();

        // 두 자리로 만들기 위해 앞에 0 추가
        if (hexString.length() < 2) {
            hexString = "0" + hexString;
        }

        // 16진수 아스키 코드로 반환
        return "0x" + hexString;
    }

    // get data length
    public static int getDataLength(byte[] buffer){
        int dataLength = 0;
        int offset = 0 + Command.MESSAGE_SOM_LENGTH + Command.MESSAGE_TYPE_LENGTH;
        //Legnth Check
        byte[] lengthBytes = new byte[Command.MESSAGE_HEADER_LENGTH];
        System.arraycopy(buffer, offset, lengthBytes, 0, Command.MESSAGE_HEADER_LENGTH);
        dataLength = byteArrayToInt(lengthBytes) - Command.MESSAGE_CRC_LENGTH - Command.MESSAGE_EOM_LENGTH;   //Length = Data length + Tail length(CRC+EOM)

        return dataLength;
    }

    // makes interface data into response packet
    public static byte[] makeComplePacket(byte[] interfaceData, int type){

        byte[] completionByte = new byte[Command.MESSAGE_SOM_LENGTH+ Command.MESSAGE_TYPE_LENGTH+ Command.MESSAGE_HEADER_LENGTH+ interfaceData.length+ Command.MESSAGE_CRC_LENGTH+ Command.MESSAGE_EOM_LENGTH];

        int offset = 0;
        completionByte[0] = Command.SOM;
        offset++;

        byte[] typeBytes = ByteBuffer.allocate(4).putInt(type).array();
        System.arraycopy(typeBytes, 0, completionByte, offset, Command.MESSAGE_TYPE_LENGTH);
        offset += Command.MESSAGE_TYPE_LENGTH;

        int length = interfaceData.length + Command.MESSAGE_CRC_LENGTH + Command.MESSAGE_EOM_LENGTH;
        byte[] lengthBytes = ByteBuffer.allocate(4).putInt(length).array();
        System.arraycopy(lengthBytes, 0, completionByte, offset, Command.MESSAGE_HEADER_LENGTH);
        offset += Command.MESSAGE_HEADER_LENGTH;

        System.arraycopy(interfaceData, 0, completionByte, offset, interfaceData.length);
        offset += interfaceData.length;

        byte[] crcBytes = new byte[2];
        crcBytes[0] = (byte) 40;
        crcBytes[1] = (byte) 140;
        System.arraycopy(crcBytes, 0, completionByte, offset, Command.MESSAGE_CRC_LENGTH);
        offset += Command.MESSAGE_CRC_LENGTH;

        completionByte[offset] = Command.EOM;

        return completionByte;
    }
}
