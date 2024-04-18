package com.temp.socketserver.client;

import com.temp.socketserver.message.Command;
import com.temp.socketserver.message.MessageController;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class ClientHandler implements Runnable {

    private Socket socket;
    private String clientAddress;
    private InputStream inputStream;
    private OutputStream outputStream;
    private MessageController messageController;
    private boolean socketAlive = false;

    public ClientHandler(Socket clientSocket, String clientAddress) {
        this.socket = clientSocket;
        this.messageController = new MessageController();
        this.clientAddress = clientAddress;
    }

    public boolean initSocket() throws IOException {
        this.socket.setSoTimeout(600 * 1000);
        this.socket.setSoLinger(true, 0);
        this.inputStream = this.socket.getInputStream();
        this.outputStream = this.socket.getOutputStream();
        this.socketAlive = this.socket.isConnected();

        return this.socketAlive;
    }

    public void closeSocket() throws IOException {
        if (this.inputStream != null) try {
            this.inputStream.close();
        } catch (IOException e) {
        }
        this.inputStream = null;

        if (this.outputStream != null) try {
            this.outputStream.close();
        } catch (IOException e) {
        }
        this.outputStream = null;

        if (this.socket != null || !this.socket.isClosed() || socket.isConnected()) {
            this.socket.close();
        }
        this.socket = null;
        this.socketAlive = false;
    }

    @Override
    public void run() {
        try {
            Queue<Byte> packetQueue = new LinkedBlockingQueue<Byte>();  //하나의 완성된 패킷
            Queue<Byte> accPacketQueue = new LinkedBlockingQueue<Byte>();   //패킷의 끝이 EOM이 아닐때 적재하는 대기큐
            byte[] responseBuffer;
            int offset = 1;
            int interfaceDataLength = 0;

            if (!this.socketAlive) {
                    if (this.initSocket()) {

                        byte[] messageBuffer;
                        while (this.socketAlive) {
                            byte[] buffer;
                            int dataSize = 0;

                            buffer = new byte[2048];
                            dataSize = inputStream.read(buffer);

                            if (dataSize > 0) {
                                for (int i = 0; i < dataSize; i++) {
                                    byte currentByte = buffer[i];

                                    if(i == 0){ //buffer 배열의 첫번째 바이트가
                                        if(Command.isSOM(currentByte)){ //SOM인 경우
                                            if(accPacketQueue.peek() != null){  //대기큐에 적재된 바이트가 있을경우
                                                accPacketQueue.offer(currentByte);
                                            }else{
                                                accPacketQueue.clear();
                                                packetQueue.clear();
                                                packetQueue.offer(currentByte);

                                                //전달받은 패킷의 데이터 길이 판별
                                                byte[] headerLengthByte = new byte[4];
                                                System.arraycopy(buffer, 5, headerLengthByte, 0, 4);
                                                interfaceDataLength = Command.byteArrayToInt(headerLengthByte) - 3; // Interface Data Length = Header Length - Tail Length
                                            }
                                        }else if(Command.isEOM(currentByte)){ //EOM인 경우
                                            if((9 < offset) && (offset < interfaceDataLength+9)){ //EOM바이트가 메세지의 InterfaceData 길이 안에 포함되어 있으면 대기큐에 넣는다.
                                                accPacketQueue.offer(currentByte);
                                            }else{
                                                while (accPacketQueue.peek() != null){
                                                    byte accByte = accPacketQueue.poll();
                                                    packetQueue.offer(accByte);
                                                }
                                                packetQueue.offer(currentByte);
                                                messageBuffer = makePacket(packetQueue);

                                                responseBuffer = messageController.dataProcess(messageBuffer);

                                                if(responseBuffer.length > 0){
                                                    byte[] completePacket = Command.makeComplePacket(responseBuffer, Command.MESSAGE_HEADER_TYPE_RES);
                                                    this.socket.getOutputStream().write(completePacket,0,completePacket.length);
                                                    this.socket.getOutputStream().flush();

                                                }
                                                offset = 1;

                                                continue;
                                            }

                                        }else { //SOM, EOM이 아닐경우 대기큐에 넣는다.
                                            accPacketQueue.offer(currentByte);
                                        }
                                    }else{  //buffer 배열 중간 바이트가
                                        if(Command.isEOM(currentByte)){   //EOM인 경우
                                            if((9 < offset) && (offset < interfaceDataLength+9)){ //메세지의 InterfaceData길이 범위 안에 포함되어 있으면 대기큐에 넣는다.
                                                accPacketQueue.offer(currentByte);
                                            }else{  //범위 밖의 EOM바이트면 패킷을 완성한다.
                                                while (accPacketQueue.peek() != null){
                                                    byte accByte = accPacketQueue.poll();
                                                    packetQueue.offer(accByte);
                                                }
                                                packetQueue.offer(currentByte);
                                                messageBuffer = makePacket(packetQueue);

                                                responseBuffer = messageController.dataProcess(messageBuffer);

                                                if(responseBuffer.length > 0) {
                                                    byte[] completePacket = Command.makeComplePacket(responseBuffer, Command.MESSAGE_HEADER_TYPE_RES);
                                                    this.socket.getOutputStream().write(completePacket, 0, completePacket.length);
                                                    this.socket.getOutputStream().flush();
                                                }
                                                offset = 1;

                                                continue;
                                            }

                                        }else { //SOM, EOM이 아닐경우 중간에 들어온 패킷이므로 대기큐에 넣는다.
                                            accPacketQueue.offer(currentByte);
                                        }
                                    }
                                    offset++;
                                }
                            }
                        }
                    }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private byte[] makePacket(Queue<Byte> packet) {
        byte[] packetArray = new byte[packet.size()];
        int i = 0;
        while(packet.peek() != null) {
            packetArray[i] = packet.poll();
            i++;
        }
        return packetArray;
    }
}
