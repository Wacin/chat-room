package com.wl.chatroom.client.multi;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Scanner;

/**
 * Auther : Wanglin
 * Created : 2019.4.3
 * Descriptioon :
 */
public class ReadDataFromServerThread extends Thread{

    private final Socket client;

    public ReadDataFromServerThread(Socket client){
        this.client = client;
    }

    @Override
    public void run(){
        try{
            InputStream clientInput = client.getInputStream();
            Scanner scanner = new Scanner(clientInput);
            while (scanner.hasNext()){
                String message = scanner.nextLine();
                System.out.println("来自服务器的消息："+message);
                if(message.equals("bye")){
                    client.close();
                    break;
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
