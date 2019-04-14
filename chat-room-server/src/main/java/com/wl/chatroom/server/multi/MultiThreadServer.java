package com.wl.chatroom.server.multi;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Auther : Wanglin
 * Created : 2019.4.3
 * Descriptioon :
 */
public class MultiThreadServer {
    public static void main(String[] args) {
        int port = 6666;

        if(args.length > 0){
            try{
                port = Integer.parseInt(args[0]);
            }catch(NumberFormatException e){
                System.out.println("端口参数不正确，采用默认端口号"+port);
            }
        }

        final ExecutorService executorService = Executors.newFixedThreadPool(10);
        try{
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("等待客户端连接...");
            while(true){
                Socket client = serverSocket.accept();
                executorService.submit(new ExecuteClient(client));
            }
        }catch (IOException e){
            e.printStackTrace();
        }

    }
}
