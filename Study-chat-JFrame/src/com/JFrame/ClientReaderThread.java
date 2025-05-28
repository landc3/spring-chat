package com.JFrame;

import java.io.DataInputStream;
import java.net.Socket;

public class ClientReaderThread extends Thread {
    private ChatFrame chatFrame;
    private Socket socket;
    private DataInputStream dis;

    public ClientReaderThread(Socket socket, ChatFrame chatFrame) {
        this.chatFrame = chatFrame;
        this.socket = socket;
    }

    public void run() {
        //接受的消息有很多种类型：1.登陆消息（包含昵称）2.群聊消息3.私聊消息
        try {
             dis = new DataInputStream(socket.getInputStream());
            while (true) {
                int type = dis.readInt();
                switch (type) {
                    case 1:
                        //服务端发来的登陆消息，读取昵称，更新在线列表
                        updateClientOnlineList();
                        break;
                    case 2:
                        //服务端发来的群聊消息，读取消息内容，显示在聊天框中
                        getMsgToChatFrame();
                        break;
                    case 3:
                        break;
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //读取服务端发来的群聊消息
    private void getMsgToChatFrame() throws Exception {
        //读取服务端发来的群聊消息
        String msg = dis.readUTF();
        chatFrame.setMsgToChatFrame(msg);

    }

    //更新在线列表
    private void updateClientOnlineList() throws Exception {
        //1.读取有多少个在线用户
        int count = dis.readInt();
        //2.循环读取在线用户，并显示在客户端的在线列表中
//        ArrayList<String> onLineNames = new ArrayList<>();
        String[] names = new String[count];
        for (int i = 0; i < count; i++) {
            //读取每个用户
            String nickname = dis.readUTF();
            names[i]=nickname;
        }
        chatFrame.updateOnlineList(names);

    }
}
