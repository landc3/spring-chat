package com.Server.Client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;

public class ServerThread extends Thread{
    private static Socket socket;//线程和客户端通信的Socket管道
    public ServerThread(Socket socket) {
        this.socket = socket;
    }
    @Override
    public void run(){
        try {
            //接受的消息有很多种类型：1.登陆消息（包含昵称）2.群聊消息3.私聊消息
            //所以客户端必须声明协议发送消息
            //发送1、2、3：1.登陆消息（包含昵称）2.群聊消息3.私聊消息
            //先从Socket管道接收消息编号，在判断
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            while (true) {
                int type = dis.readInt();//1,2,3
                switch (type){
                    case 1:
                        //客户端发来登陆消息，读取昵称，在更新在线列表
                        String nickname = dis.readUTF();
                        //把这个登陆成功的客户端Socket存入到在线集合中
                        Server.onLineSockets.put(socket,nickname);
                        //更新在线集合，给每个客户端发送一个上线通知
                        updateSocketOnlineList();
                        break;
                    case 2:
                        //群聊消息,读取消息内容，给每个客户端发送
                        String message = dis.readUTF();
                        sendMsg(message);
                        break;
                    case 3:
                        //私聊消息，读取消息内容，给指定的客户端发送

                }
            }
        } catch (Exception e) {
            System.out.println("客户端下线了"+socket.getInetAddress().getHostAddress());
            Server.onLineSockets.remove(socket);//把下线的客户端从在线集合中移除
            updateSocketOnlineList();
        }


    }
    //给全部在线的客户端发送消息
    private static void sendMsg(String message) {
        //先拼接消息，再发送给全部在线Socket
        StringBuilder sb = new StringBuilder();
        //1.只获取当前名字，放在集合中
        String name = Server.onLineSockets.get(socket);
        //2.获取当前时间
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm:ss");
        String newStr = dtf.format(now);//获取当前时间，转换为字符串
        //3.拼接消息
        String msgResult = sb.append(name).append(" ").append(newStr).append("\r\n").append(message).append("\r\n").toString();

        for (Socket socket : Server.onLineSockets.keySet()) {
            try {
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                dos.writeInt(2);//2代表群聊消息
                dos.writeUTF(msgResult);
                dos.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void updateSocketOnlineList() {
        //跟新全部客户端的在线列表
        //拿到全部在线客户端的用户名称，再把这些用户名称发送给每个客户端
        //1.拿到全部在线的用户名称
        Collection<String> onLineUsers = Server.onLineSockets.values();
        //2.把这个在线列表发送给每个客户端Socket管道
        for (Socket socket : Server.onLineSockets.keySet()) {
            try {
                //把用户名称通过Socket管道发送给客户端
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());//获取的内容是用户名称
                dos.writeInt(1);//1代表上线通知 2代表群聊消息
                dos.writeInt(Server.onLineSockets.size());//告诉客户端接下来发送多少个用户名称
                for (String nickname : Server.onLineSockets.values()) {
                    dos.writeUTF(nickname);//发送用户名称
                }
                dos.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}


