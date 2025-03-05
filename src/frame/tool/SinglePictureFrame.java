package frame.tool;

import common.DataProcessing;
import common.Picture;
import org.imgscalr.Scalr;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class SinglePictureFrame extends JFrame {
    private Picture picture;
    private DataProcessing dp;
    private JTextField idField;
    private JTextField artistField;
    private JTextField pidField;
    private JTextArea tagsArea;
    private JTextField filePathField;
    private JPanel picturePanel;
    private File losePictureFile ;

    // 定义通用字体
    private Font commonFont = new Font("Microsoft YaHei Mono", Font.BOLD, 20);

    public SinglePictureFrame(Picture picture, DataProcessing dp) {
        this.picture = picture;
        this.dp = dp;
        losePictureFile= new File(dp.getDataPath(), "fail.png");

        setTitle("图片" + picture.getId());
        setSize(1250, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // 主面板，使用垂直布局
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        // 1. Picture Panel
        picturePanel = new JPanel();
        ImageIcon imageIcon = null;
        try {
            imageIcon = loadImage(dp.tryGetPictureFile(picture));
        } catch (Exception e) {
            imageIcon = loadImage(losePictureFile);
        }

        JLabel imageLabel = new JLabel(imageIcon);
        picturePanel.add(imageLabel);
        mainPanel.add(picturePanel);

        // 2. ID Panel
        idField = new JTextField(String.valueOf(picture.getId()), 20);
        JPanel idPanel = createEditablePanel("  序号 ", idField, false);
        mainPanel.add(idPanel);

        // 3. Artist Panel
        artistField = new JTextField(picture.getArtist(), 20);
        JPanel artistPanel = createEditablePanel("  画师 ", artistField, true);
        mainPanel.add(artistPanel);

        // 4. PID Panel
        pidField = new JTextField(String.valueOf(picture.getPid()), 20);
        JPanel pidPanel = createEditablePanel("  PID  ", pidField, true);
        mainPanel.add(pidPanel);

        // 5. Tags Panel
        tagsArea = new JTextArea(String.join(" ", picture.getTags()), 3, 30);
        JPanel tagsPanel = createTagsPanel("  标签 ", tagsArea);
        mainPanel.add(tagsPanel);

        // 6. FilePath Panel
        filePathField = new JTextField(picture.getLocalfile().getAbsolutePath(), 35);
        JPanel filePathPanel = createFilePathPanel("  路径 ", filePathField);
        mainPanel.add(filePathPanel);

        // 7. 修改和取消按钮
        JPanel buttonPanel = new JPanel();
        JButton modifyButton = new JButton("修改");
        modifyButton.addActionListener(e -> handlemodifyButtpon());
        JButton uploadButton=new JButton("上传");
        uploadButton.addActionListener(e -> handleUploadButton());
        JButton deleteButton=new JButton("删除");
        deleteButton.addActionListener(e -> handleDeleteButton());
        JButton encrypyButton=new JButton("加密");
        encrypyButton.addActionListener(e -> handleEncryptButton());
        JButton cancelButton = new JButton("取消");
        cancelButton.addActionListener(e -> dispose());

        modifyButton.setPreferredSize(new Dimension(100, 30));
        cancelButton.setPreferredSize(new Dimension(100, 30));
        deleteButton.setPreferredSize(new Dimension(100, 30));
        encrypyButton.setPreferredSize(new Dimension(100, 30));
        uploadButton.setPreferredSize(new Dimension(100, 30));
        modifyButton.setFont(commonFont);
        deleteButton.setFont(commonFont);
        cancelButton.setFont(commonFont);
        encrypyButton.setFont(commonFont);
        uploadButton.setFont(commonFont);
        buttonPanel.add(modifyButton);
        buttonPanel.add(uploadButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(encrypyButton);
        buttonPanel.add(cancelButton);
        mainPanel.add(buttonPanel);

        // 添加主面板到窗口
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
                int units = e.getUnitsToScroll() * 20; // 将滚轮速度提高 20 倍
                verticalScrollBar.setValue(verticalScrollBar.getValue() + units);
                e.consume(); // 阻止默认的滚轮行为
            }
        });
        add(scrollPane);

        setVisible(true);
    }

    private ImageIcon loadImage(File file) {
        try {
            BufferedImage image = ImageIO.read(file);
            if (image == null) {
                throw new IOException("无法加载图片文件: " + file.getAbsolutePath());
            }

            // 最大尺寸
            int maxWidth = 1200;
            int maxHeight = 800;
            int originalWidth = image.getWidth();
            int originalHeight = image.getHeight();

            // 如果图像尺寸小于或等于最大尺寸，不进行缩放
            if (originalWidth <= maxWidth && originalHeight <= maxHeight) {
                return new ImageIcon(image); // 直接返回原图
            }

            // 如果图片尺寸超过最大尺寸，进行高质量缩放
            // 计算保持宽高比的缩放尺寸
            double widthRatio = (double) maxWidth / originalWidth;
            double heightRatio = (double) maxHeight / originalHeight;
            double ratio = Math.min(widthRatio, heightRatio);

            int scaledWidth = (int) (originalWidth * ratio);
            int scaledHeight = (int) (originalHeight * ratio);

            // 使用 Imgscalr 进行高质量缩放
            image = Scalr.resize(image, Scalr.Method.ULTRA_QUALITY, Scalr.Mode.FIT_EXACT, scaledWidth, scaledHeight);

            return new ImageIcon(image); // 返回缩放后的图片

        } catch (IOException e) {
            return new ImageIcon(losePictureFile.getAbsolutePath()); // 返回空图标以示错误
        }
    }

    private JPanel createEditablePanel(String labelText, JTextField textField, boolean editable) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel label = new JLabel(labelText);
        label.setFont(commonFont); // 设置字体
        textField.setFont(commonFont); // 设置字体
        textField.setEditable(false);
        panel.add(label);
        panel.add(textField);

        if (editable) {
            textField.addMouseListener(new MouseInputAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        textField.setEditable(true);
                        textField.requestFocus();
                    }
                }
            });
        }

        return panel;
    }

    private JPanel createTagsPanel(String labelText, JTextArea textArea) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel label = new JLabel(labelText);
        label.setFont(commonFont); // 设置字体
        textArea.setFont(commonFont); // 设置字体
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        panel.add(label);
        panel.add(new JScrollPane(textArea)); // 使用 JScrollPane 包裹 JTextArea

        textArea.addMouseListener(new MouseInputAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    textArea.setEditable(true);
                    textArea.requestFocus();
                }
            }
        });

        return panel;
    }

    private JPanel createFilePathPanel(String labelText, JTextField textField) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel label = new JLabel(labelText);
        label.setFont(commonFont); // 设置字体
        textField.setFont(commonFont); // 设置字体
        textField.setEditable(false);

        JButton browseButton = new JButton("打开");
        browseButton.setPreferredSize(new Dimension(80, 30));
        browseButton.setFont(new Font("Microsoft YaHei Mono", Font.PLAIN, 18)); // 设置字体

        browseButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setPreferredSize(new Dimension(900, 500));

            // 1. 设置默认目录为 textField 中的文件父目录
            if (!textField.getText().isEmpty()) {
                File currentFile = new File(textField.getText());
                if (currentFile.exists()) {
                    fileChooser.setCurrentDirectory(currentFile.getParentFile());
                }
            }

            // 2. 添加图片预览面板
            JLabel previewLabel = new JLabel();
            previewLabel.setPreferredSize(new Dimension(180, 320)); // 设置预览面板大小
            fileChooser.setAccessory(previewLabel);

            // 3. 添加文件选择监听器以更新预览
            fileChooser.addPropertyChangeListener(evt -> {
                if (JFileChooser.SELECTED_FILE_CHANGED_PROPERTY.equals(evt.getPropertyName())) {
                    File selectedFile = fileChooser.getSelectedFile();
                    if (selectedFile != null && isImageFile(selectedFile)) {
                        ImageIcon icon = new ImageIcon(selectedFile.getAbsolutePath());
                        Image image = icon.getImage().getScaledInstance(180, 320, Image.SCALE_SMOOTH);
                        previewLabel.setIcon(new ImageIcon(image));
                    } else {
                        previewLabel.setIcon(null); // 清除预览
                    }
                }
            });

            // 4. 显示文件选择对话框
            int returnValue = fileChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                textField.setText(selectedFile.getAbsolutePath());
                updatePicturePanel(); // 更新图片面板
            }
        });

        panel.add(label);
        panel.add(textField);
        panel.add(browseButton);

        return panel;
    }



    private void handlemodifyButtpon() {
        try {
            dp.changePictureInfo(picture,artistField.getText(),pidField.getText(),tagsArea.getText(),filePathField.getText());
            JOptionPane.showMessageDialog(this, "修改成功", "成功", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "修改失败 "+e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }

    }
    private void handleDeleteButton() {
        // 弹出确认删除的弹窗
        int option = JOptionPane.showConfirmDialog(
                this, // 父组件
                "确认删除此图片吗？", // 提示信息
                "确认删除", // 弹窗标题
                JOptionPane.YES_NO_OPTION, // 按钮选项（是/否）
                JOptionPane.WARNING_MESSAGE // 弹窗类型（警告图标）
        );

        // 如果用户点击了“是”
        if (option == JOptionPane.YES_OPTION) {
            // 调用删除方法
            if (dp.removePicture(picture)) {
                JOptionPane.showMessageDialog(
                        this, // 父组件
                        "删除成功", // 提示信息
                        "成功", // 弹窗标题
                        JOptionPane.INFORMATION_MESSAGE // 弹窗类型（信息图标）
                );
                this.dispose(); // 关闭窗口
            } else {
                JOptionPane.showMessageDialog(
                        this, // 父组件
                        "删除失败", // 提示信息
                        "错误", // 弹窗标题
                        JOptionPane.ERROR_MESSAGE // 弹窗类型（错误图标）
                );
            }
        }
    }
    private void updatePicturePanel() {
        picturePanel.removeAll(); // 清空当前面板内容
        ImageIcon imageIcon = null;
        try {
            File imageFile = new File(filePathField.getText()); // 获取文件路径
            imageIcon = loadImage(imageFile); // 加载图片
        } catch (Exception e) {
            imageIcon = loadImage(losePictureFile); // 加载失败时显示默认图片
        }

        JLabel imageLabel = new JLabel(imageIcon);
        picturePanel.add(imageLabel); // 添加图片到面板
        picturePanel.revalidate(); // 刷新面板
        picturePanel.repaint(); // 重新绘制面板
    }
    private void handleUploadButton() {
        // 弹出确认对话框
        int option = JOptionPane.showConfirmDialog(
                this, // 父组件
                "确认上传此图片吗？", // 提示信息
                "确认上传", // 弹窗标题
                JOptionPane.YES_NO_OPTION, // 按钮选项（是/否）
                JOptionPane.WARNING_MESSAGE // 弹窗类型（警告图标）
        );

        // 如果用户点击了“是”
        if (option == JOptionPane.YES_OPTION) {
            String user = JOptionPane.showInputDialog(
                    this, // 父组件
                    "请输入上传用户：", // 提示信息
                    "输入上传用户", // 弹窗标题
                    JOptionPane.QUESTION_MESSAGE // 弹窗类型（问题图标）
            );

            // 如果用户输入了内容并且点击了确定
            if (user != null && !user.trim().isEmpty()) {
                // 调用上传方法，传入用户输入的 tester
                if (dp.uploadPicture(picture, user)) {
                    JOptionPane.showMessageDialog(
                            this, // 父组件
                            "上传成功", // 提示信息
                            "成功", // 弹窗标题
                            JOptionPane.INFORMATION_MESSAGE // 弹窗类型（信息图标）
                    );
                    this.dispose(); // 关闭窗口
                } else {
                    JOptionPane.showMessageDialog(
                            this, // 父组件
                            "上传失败", // 提示信息
                            "错误", // 弹窗标题
                            JOptionPane.ERROR_MESSAGE // 弹窗类型（错误图标）
                    );
                }
            } else {
                // 如果用户没有输入内容或点击了取消
                JOptionPane.showMessageDialog(
                        this, // 父组件
                        "未输入用户名称，上传取消", // 提示信息
                        "取消", // 弹窗标题
                        JOptionPane.WARNING_MESSAGE // 弹窗类型（警告图标）
                );
            }
        }
    }
    private void handleEncryptButton(){
        File outputFile = null;
        try {
             outputFile = dp.encryptFile(picture.getLocalfile());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "加密失败", "错误", JOptionPane.ERROR_MESSAGE);
        }
        if(!outputFile.exists()){
            JOptionPane.showMessageDialog(this, "加密失败", "错误", JOptionPane.ERROR_MESSAGE);
        }
        if(outputFile.length()==0){
            JOptionPane.showMessageDialog(this, "加密失败", "错误", JOptionPane.ERROR_MESSAGE);
        }else {
            JOptionPane.showMessageDialog(this, "加密成功", "成功", JOptionPane.INFORMATION_MESSAGE);
        }


    }


    private boolean isImageFile(File file) {
        String name = file.getName().toLowerCase();
        return name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png") || name.endsWith(".gif");
    }
}
