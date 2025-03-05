package frame.mainpanel;

import common.DataProcessing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class SettingPanel extends JPanel {
    private DataProcessing dp;
    private JTextField temporaryPicturesPathField;
    private JTextField encryptedPathField;
    private JTextField decryptedPathField;
    private JTextField useraddPicturePathField;
    private JTextField downloadPathField;
    private JTextField serverIPField;
    private Font commonFont = new Font("Microsoft YaHei Mono", Font.BOLD, 20);

    public SettingPanel(DataProcessing dp) {
        this.dp = dp;
        setLayout(new BorderLayout());
        setBackground(Color.YELLOW); // 设置背景色（仅用于测试）

        // 创建主面板
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 添加临时图片路径面板
        mainPanel.add(createFieldPanel("临时图片路径:", dp.getUserData().getTemporaryPicturesPath(), false));

        // 添加加密路径面板
        mainPanel.add(createFieldPanel("加密路径:", dp.getUserData().getEncryptedPath(), false));

        // 添加解密路径面板
        mainPanel.add(createFieldPanel("解密路径:", dp.getUserData().getDecryptedPath(), false));

        // 添加用户添加图片路径面板
        JPanel useraddPicturePathPanel = createFieldPanel("用户添加图片路径:", dp.getUserData().getUseraddPicturePath(), true);
        JButton useraddPicturePathButton = createFileChooserButton(useraddPicturePathField);
        useraddPicturePathPanel.add(useraddPicturePathButton);
        mainPanel.add(useraddPicturePathPanel);

        // 添加下载路径面板
        JPanel downloadPathPanel = createFieldPanel("下载路径:", dp.getUserData().getDownloadPath(), true);
        JButton downloadPathButton = createFileChooserButton(downloadPathField);
        downloadPathPanel.add(downloadPathButton);
        mainPanel.add(downloadPathPanel);

        // 添加服务器IP面板
        mainPanel.add(createFieldPanel("服务器IP:", dp.getUserData().getServerIP(), true));

        // 添加主面板到中心
        add(mainPanel, BorderLayout.CENTER);

        // 添加修改按钮
        JButton saveButton = new JButton("修改");
        saveButton.setFont(commonFont);
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 更新 UserData 中的路径
                dp.getUserData().setUseraddPicturePath(useraddPicturePathField.getText());
                dp.getUserData().setDownloadPath(downloadPathField.getText());
                dp.getUserData().setServerIP(serverIPField.getText());

                // 调用 save 方法保存修改
                dp.saveData();

                // 提示用户修改成功
                JOptionPane.showMessageDialog(SettingPanel.this, "修改成功！");
            }
        });
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(saveButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    // 创建包含标签和文本框的面板
    private JPanel createFieldPanel(String labelText, String fieldText, boolean editable) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        JLabel label = new JLabel(labelText);
        label.setFont(commonFont);
        panel.add(label);

        JTextField textField = new JTextField(fieldText, 35);
        textField.setFont(commonFont);
        textField.setEditable(editable);
        panel.add(textField);

        // 根据字段类型保存引用
        switch (labelText) {
            case "临时图片路径:":
                temporaryPicturesPathField = textField;
                break;
            case "加密路径:":
                encryptedPathField = textField;
                break;
            case "解密路径:":
                decryptedPathField = textField;
                break;
            case "用户添加图片路径:":
                useraddPicturePathField = textField;
                break;
            case "下载路径:":
                downloadPathField = textField;
                break;
            case "服务器IP:":
                serverIPField = textField;
                break;
        }

        return panel;
    }

    // 创建文件选择器按钮
    private JButton createFileChooserButton(JTextField targetField) {
        JButton button = new JButton("选择文件夹");
        button.setFont(commonFont);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setPreferredSize(new Dimension(900, 500));
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int option = fileChooser.showOpenDialog(SettingPanel.this);
                if (option == JFileChooser.APPROVE_OPTION) {
                    File selectedFolder = fileChooser.getSelectedFile();
                    targetField.setText(selectedFolder.getAbsolutePath());
                }
            }
        });
        return button;
    }
}