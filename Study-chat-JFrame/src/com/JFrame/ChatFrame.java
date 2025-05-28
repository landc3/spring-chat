package com.JFrame;

import javax.swing.*;
import java.awt.*;
import java.io.DataOutputStream;
import java.net.Socket;

public class ChatFrame extends JFrame {

    private  JTextArea chatTextArea=new JTextArea(13,50);// 聊天内容区域
    private JList<String> onlineUsersList;// 在线人员列表
    private DefaultListModel<String> onlineUsersModel;// 在线人员列表数据模型
    private JTextArea messageField=new JTextArea(4,40);// 输入框
    private JButton sendButton;// 发送按钮
    private Socket socket;
    private String nickname;

    public ChatFrame() {
        initData();
        setVisible(true);
    }

    public ChatFrame(String nickname, Socket socket) {//用来接收昵称和socket
        this();//先调用上面的无参构造方法，初始化数据
        //展示昵称到窗口
        setTitle("局域网聊天室 - " + nickname + " 的群聊界面");
        this.nickname = nickname;
        this.socket = socket;
        //交给一个独立的线程专门负责读取客户端socket从服务端收到的在线人数更新数据和群聊数据。
        new ClientReaderThread(socket,this).start();

    }

    public void initData() {
        setTitle("局域网聊天室 - 群聊界面");
        setSize(600, 400);
        setLocationRelativeTo(null); // 居中显示
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 创建主面板
        JPanel mainPanel = new JPanel(new BorderLayout());

        // 左侧聊天内容区域
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BorderLayout());
//        chatTextArea = new JTextArea();
        chatTextArea.setEditable(false);// 不可编辑
        JScrollPane chatScrollPane = new JScrollPane(chatTextArea);// 添加滚动条
        leftPanel.add(chatScrollPane, BorderLayout.CENTER);// 添加到面板
        mainPanel.add(leftPanel, BorderLayout.LINE_START);

        // 右侧在线人员列表
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BorderLayout());
        onlineUsersModel = new DefaultListModel<>();
        onlineUsersList = new JList<>(onlineUsersModel);
        onlineUsersList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        onlineUsersList.setVisibleRowCount(-1); // 自适应高度
        JScrollPane usersScrollPane = new JScrollPane(onlineUsersList);
        rightPanel.add(usersScrollPane, BorderLayout.CENTER);
        mainPanel.add(rightPanel, BorderLayout.LINE_END);

        // 底部输入框和发送按钮
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout());
//        messageField = new JTextArea();
        sendButton = new JButton("发送");

        // 发送按钮事件处理
        sendButton.addActionListener(e->{ {
            String message = messageField.getText();
            messageField.setText(""); // 清空输入框
            if (!message.isEmpty()) {
                try {
                    sendMsgToServer(message);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        });

        bottomPanel.add(messageField, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);

        mainPanel.add(bottomPanel, BorderLayout.PAGE_END);

        // 添加主面板到窗口
        add(mainPanel);



    }

    private void sendMsgToServer(String message) throws Exception {
       // 在这里可以使用socket向服务端发送消息
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
        dos.writeInt(2);
        dos.writeUTF(message);
        dos.flush();
    }

    //更新在线用户列表
    public void updateOnlineList(String[] onLineNames) {
        // 把这个线程读取到的在线人员名称展示到界面上
       /* onlineUsersModel.clear();
        for (String name : onLineNames) {
            onlineUsersModel.addElement(name);
        }

*/
        chatTextArea.append("当前在线人数：" + onLineNames.length + "\n");
        onlineUsersList.setListData(onLineNames);

    }

    //更新群聊消息
    public void setMsgToChatFrame(String msg) {
        // 把这个线程读取到的群聊消息展示到界面上
//        messageField.setText(msg);
        chatTextArea.append(msg);
    }


}
