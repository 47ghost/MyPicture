package frame.tool;

import common.DataProcessing;
import common.Picture;
import org.imgscalr.Scalr;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class AddPictureFrame extends JFrame {
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

    public AddPictureFrame( DataProcessing dp) {
        this.dp = dp;
        losePictureFile=new File(dp.getDataPath(),"fail.png");

        setTitle("添加图片");
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
            imageIcon = loadImage(new File(filePathField.getText()));
        } catch (Exception e) {
            imageIcon = loadImage(losePictureFile);
        }

        JLabel imageLabel = new JLabel(imageIcon);
        picturePanel.add(imageLabel);
        mainPanel.add(picturePanel);

        // 2. ID Panel
        idField = new JTextField(String.valueOf(dp.getNextid()), 20);
        JPanel idPanel = createEditablePanel("  序号 ", idField, false);
        mainPanel.add(idPanel);

        // 3. Artist Panel
        artistField = new JTextField( 20);
        JPanel artistPanel = createEditablePanel("  画师 ", artistField, true);
        mainPanel.add(artistPanel);

        // 4. PID Panel
        pidField = new JTextField(20);
        JPanel pidPanel = createEditablePanel("  PID  ", pidField, true);
        mainPanel.add(pidPanel);

        // 5. Tags Panel
        tagsArea = new JTextArea( 3, 30);
        JPanel tagsPanel = createTagsPanel("  标签 ", tagsArea);
        mainPanel.add(tagsPanel);

        // 6. FilePath Panel
        filePathField = new JTextField( 35);
        JPanel filePathPanel = createFilePathPanel("  路径 ", filePathField);
        mainPanel.add(filePathPanel);

        // 7. 修改和取消按钮
        JPanel buttonPanel = new JPanel();
        JButton addButton= new JButton("添加");
        addButton.addActionListener(e -> handleAddButton());
        JButton cancelButton = new JButton("取消");
        cancelButton.addActionListener(e -> dispose());


        cancelButton.setPreferredSize(new Dimension(100, 30));
        addButton.setPreferredSize(new Dimension(100, 30));
        cancelButton.setFont(commonFont);
        addButton.setFont(commonFont);
        buttonPanel.add(addButton);
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
            textField.setEditable(true);
        }

        return panel;
    }

    private JPanel createTagsPanel(String labelText, JTextArea textArea) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel label = new JLabel(labelText);
        label.setFont(commonFont); // 设置字体
        textArea.setFont(commonFont); // 设置字体
        textArea.setEditable(true);
        textArea.setLineWrap(true);
        panel.add(label);
        panel.add(new JScrollPane(textArea)); // 使用 JScrollPane 包裹 JTextArea



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

       if(dp.getUserData().getUseraddPicturePath()!=null) {
    fileChooser.setCurrentDirectory(new File(dp.getUserData().getUseraddPicturePath()));
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

    private void handleAddButton(){
        boolean isPrivate=false;
        int newid=dp.getNextid();
        String newArtist=artistField.getText();
        String newPid=pidField.getText();
        File newFile = new File(filePathField.getText());
        Set<String> newtags =   Arrays.stream(tagsArea.getText().split("\\s+")) // 分割成数组
                .filter(tag -> !tag.isEmpty())      // 过滤掉空字符串
                .collect(Collectors.toSet());
        Timestamp timestamp= new Timestamp(System.currentTimeMillis());
        if(newtags.contains("私密")){
            isPrivate=true;
        }
        Picture newPicture = new Picture(newid, newFile,newArtist, newPid,timestamp,newtags);
        try{
        if(isPrivate){
                dp.addPrivatePicture(newPicture);
        }else{
           dp.addPicture(newPicture);
        }
        JOptionPane.showMessageDialog(this, "添加成功", "成功", JOptionPane.INFORMATION_MESSAGE);
        this.dispose();
        }catch (Exception e) {
            JOptionPane.showMessageDialog(this, "添加失败 "+e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
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
    private boolean isImageFile(File file) {
        String name = file.getName().toLowerCase();
        return name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png") || name.endsWith(".gif");
    }

}
