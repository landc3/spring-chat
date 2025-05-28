package com.JFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.DataOutputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ChatEntryFrame extends JFrame {

    private JTextField nicknameField;
    //    private final String placeholder = "请输入昵称:";
    private boolean isPlaceholderVisible = true;
    private Socket socket;//记住当前客户端的Socket管道

    public ChatEntryFrame() {
        //初始化登录界面
        initLoginJFrame();
        //让界面显示出来
        this.setVisible(true);

    }

    public void initLoginJFrame() {
        setTitle("局域网聊天室 - 进入界面");
        setSize(300, 150);
        setLocationRelativeTo(null); // 居中显示
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(null); // 使用绝对布局

        // 昵称标签
        JLabel nicknameLabel = new JLabel("昵称:");
        nicknameLabel.setBounds(10, 10, 80, 25);
        panel.add(nicknameLabel);

        // 昵称输入框
        nicknameField = new JTextField(20);
        nicknameField.setEditable(false); // 初始状态不可编辑
//        nicknameField.setText(placeholder);
        nicknameField.setForeground(Color.GRAY); // 设置灰色文本
        nicknameField.setBounds(100, 10, 160, 25);
        nicknameField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (isPlaceholderVisible) {
                    nicknameField.setText("");
                    nicknameField.setEditable(true); // 可编辑
                    nicknameField.setForeground(Color.BLACK); // 黑色文本
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (nicknameField.getText().isEmpty()) {
//                    nicknameField.setText(placeholder);
                    nicknameField.setEditable(false); // 不可编辑
                    nicknameField.setForeground(Color.GRAY); // 灰色文本
                    isPlaceholderVisible = true;
                } else {
                    isPlaceholderVisible = false;
                }
            }
        });

        panel.add(nicknameField);

        // 按钮面板
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBounds(100, 50, 200, 30);

        JButton enterButton = new JButton("进入");
        JButton cancelButton = new JButton("取消");

        buttonPanel.add(enterButton);
        buttonPanel.add(cancelButton);

        // 将按钮面板添加到主面板
        panel.add(buttonPanel);

        // 添加主面板到窗口
        add(panel);

        // 按钮点击事件处理
        enterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nickname = nicknameField.getText().trim();
                if (!nickname.isEmpty()) {
                    //立即发送登陆消息给服务器端
                    try {
                        login(nickname);//只接收1个参数,昵称,用来登录
                        //进入聊天室
                        new ChatFrame(nickname, socket);//启动聊天界面，将呢称和Socket管道传给聊天界面
                        dispose();//关闭当前窗口

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    JOptionPane.showMessageDialog(ChatEntryFrame.this, "欢迎 " + nickname + " 加入聊天室！");
                    // 这里可以添加启动聊天应用主逻辑的代码

                } else {
                    JOptionPane.showMessageDialog(ChatEntryFrame.this, "请输入昵称！", "提示", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0); // 关闭应用
            }
        });
    }
    private void login(String nickname) throws Exception{
        //立即发送登陆消息给服务器端
        //1.创建Socket管道请求与服务器端的Socket建立连接
            socket = new Socket(Constant.SERVER_IP, Constant.PORT);
            //2.从Socket管道中得到一个字节输出流
            OutputStream os = socket.getOutputStream();
            //3.把字节流改装成自己需要的流对象
            DataOutputStream dos = new DataOutputStream(os);
            //4.把数据写入到输出流中
            dos.writeInt(1);//消息类型：1代表登录消息
            dos.writeUTF(nickname);//
            dos.flush();

    }
}
/*
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            ChatEntryFrame entryFrame = new ChatEntryFrame();
            entryFrame.setVisible(true);
        });
    }
}*/
