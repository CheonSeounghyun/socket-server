package com.temp.socketserver.client;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClientMgr {

    public static final Map<String, ClientHandler> clientHandlers = new ConcurrentHashMap<>();
    public static final Map<String, Future> threadhandlers = new ConcurrentHashMap<>();

    public ClientMgr(int s) throws IOException {
        init(s);
    }

    private void init(int port) throws IOException {

        ServerSocket serverSocket = new ServerSocket(port);

        while (true) {

            Socket clientSocket = serverSocket.accept();

            String address = clientSocket.getRemoteSocketAddress().toString();

            Pattern pattern = Pattern.compile("\\b\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\b");
            Matcher matcher = pattern.matcher(address);
            if(matcher.find()){
                String ipAddress = matcher.group();

                ClientHandler clienthandler = new ClientHandler(clientSocket, ipAddress);
            }

        }
    }
}
