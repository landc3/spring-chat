package com.Server.Client;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class Server {
    //定义一个集合容器存储所有登陆进来的客户端管道，以便给他们群发消息
    //--map集合，键是客户端的管道，值是客户端的用户名称
    public static final HashMap<Socket, String> onLineSockets = new HashMap<>();
    public static void main(String[] args){
        System.out.println("服务器启动");
        try {
            //1.创建一个服务器端Socket，即ServerSocket，指定绑定的端口，并监听此端口
            ServerSocket serverSocket = new ServerSocket(Constant.PORT);
            //2.主线程负责接收客户端连接请求
            while (true){
                System.out.println("等待客户端连接...");
                //调用accept()方法开始监听，等待客户端的连接
                //把这个管道交给一个独立的线程处理，以支持很多客户端进入通信
                new ServerThread(serverSocket.accept()).start();
                System.out.println("一个客户端连接了");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
