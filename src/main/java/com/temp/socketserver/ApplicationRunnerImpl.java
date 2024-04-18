package com.temp.socketserver;

import com.temp.socketserver.client.ClientMgr;
import com.temp.socketserver.client.ClientThreadPool;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class ApplicationRunnerImpl implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) throws Exception {

        ClientThreadPool.init(5);
        ClientMgr clientMgr = new ClientMgr(8081);

    }
}
