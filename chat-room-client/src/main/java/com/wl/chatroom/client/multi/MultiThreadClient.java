package com.wl.chatroom.client.multi;

import java.io.IOException;
import java.net.Socket;

/**
 * Auther : Wanglin
 * Created : 2019.4.3
 * Descriptioon :
 */
public class MultiThreadClient {
    public static void main(String[] args) {
        try{
            //通过命令行获取参数
            int port = 6666;
            if(args.length>0){
                try{
                    port = Integer.parseInt(args[0]);
                }catch(NumberFormatException e){
                    System.out.println("端口参数不正确，采用默认端口号"+port);
                }
            }

            String host = "127.0.0.1";
            if (args.length>1){
                host = args[1];
                //host格式校验
            }
            final Socket socket = new Socket(host,port);

            //向服务器发送数据
            new WriteDataToServerThread(socket).start();

            //从服务器读取数据
            new ReadDataFromServerThread(socket).start();

        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
