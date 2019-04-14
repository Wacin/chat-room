package com.wl.chatroom.server.multi;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.SocketHandler;

/**
 * Auther : Wanglin
 * Created : 2019.4.3
 * Descriptioon :
 * 服务器端处理客户端连接的任务
 * 注册、登录、群聊、退出、显示当前在线用户
 *
 */
public class ExecuteClient implements Runnable{
    /**
     * 在线用户集合
     */

    private static final Map<String,Socket> ONLINE_USER_MAP = new ConcurrentHashMap<>();//存储当前在线用户

    private final Socket client;

    public ExecuteClient(Socket client){

        this.client = client;
    }

    @Override
    public void run(){
        try{
            //获取客户端输入
            InputStream clientInput = this.client.getInputStream();
            Scanner scanner = new Scanner(clientInput);
            while(true){
                String line = scanner.nextLine();
                /**
                 * 登录：login:<name>:<password>
                 * 注册：username:<name>:<password>
                 * 私聊：private:<name>:<message>
                 * 群聊：group:<message>
                 * 退出：bye
                 */

                if(line.startsWith("login")){
                    String[] segments = line.split("\\:");
                    String useName = segments[1];
                    String password = segments[2];
                    continue;
                }

                if (line.startsWith("userName")){
                    String userName = line.split("\\:")[1];
                    this.register(userName, client);
                    continue;
                }

                if (line.startsWith("private")){
                    String[] segments = line.split("\\:");
                    String userName = segments[1];
                    String message = segments[2];
                    this.privateChat(userName,message);
                    continue;
                }

                if (line.startsWith("group")){
                    String message = line.split("\\:")[1];
                    //参数校验
                    this.groupChat(message);
                    continue;
                }

                if (line.equals("bye")){
                    this.quit();
                    break;
                }
            }

        }catch(IOException e){
            e.printStackTrace();
        }
    }

    private void quit(){
        String currentUserName = this.getCurrentUserName();
        System.out.println("用户："+currentUserName+"下线");
        Socket socket = ONLINE_USER_MAP.get(currentUserName);
        this.sendMessage(socket,"bye");
        ONLINE_USER_MAP.remove(currentUserName);
        printOnlineUser();
    }

    private void groupChat(String message) {

        for (Socket socket : ONLINE_USER_MAP.values()) {

            if (socket.equals(this.client)) {

                continue;

            }

            this.sendMessage(socket, this.getCurrentUserName() + " 说：" + message);

        }

    }



    private void privateChat(String userName, String message) {

        String currentUserName = this.getCurrentUserName();

        Socket target = ONLINE_USER_MAP.get(userName);

        if (target != null) {

            this.sendMessage(target, currentUserName + " 对你说： " + message);

        }

    }



    private void register(String userName, Socket client) {

        System.out.println(userName + "加入到聊天室 " + client.getRemoteSocketAddress());

        ONLINE_USER_MAP.put(userName, client);

        printOnlineUser();

        sendMessage(this.client, userName + "注册成功!");

    }



    private String getCurrentUserName() {

        String currentUserName = "";

        for (Map.Entry<String, Socket> entry : ONLINE_USER_MAP.entrySet()) {

            if (this.client.equals(entry.getValue())) {

                currentUserName = entry.getKey();

                break;

            }

        }

        return currentUserName;

    }



    private void sendMessage(Socket socket, String message) {

        try {

            OutputStream clientOutput = socket.getOutputStream();

            OutputStreamWriter writer = new OutputStreamWriter(clientOutput);

            writer.write(message + "\n");

            writer.flush();

        } catch (IOException e) {

            e.printStackTrace();

        }

    }



    private static void printOnlineUser() {

        System.out.println("当前在线人数：" + ONLINE_USER_MAP.size() + " 用户名如下列表：");

        for (Map.Entry<String, Socket> entry : ONLINE_USER_MAP.entrySet()) {

            System.out.println(entry.getKey());

        }

    }
}
