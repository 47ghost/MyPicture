package frame.headpanel;

import common.DataProcessing;
import common.Picture;
import frame.mainpanel.PictureListPanel;
import frame.tool.AddPictureFrame;
import frame.tool.DecryptFrame;
import frame.tool.SearchedTagsFrame;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class PictureListHead extends JPanel {
    private  DataProcessing dataProcessor;
    private PictureListPanel pictureListPanel;
    public final static int pListModelTAG= 1;
    public final static int pGirdModelTAG= 2;
    private int modelTag = pListModelTAG;
    private int privateTag =0;
    private int reverseOrderTag =0;
    private JButton addButton, refreshButton, saveButton,privataButton,decryptButton,
            labelButton, searchButton, girdbutton,listbutton,reverseorderbutton;
    private JTextField searchField;
    private Set<String> searchedTags;


public PictureListHead(DataProcessing dp, PictureListPanel pictureListPanel) {
    dataProcessor = dp;
    this.pictureListPanel = pictureListPanel;
    setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
    setBorder(BorderFactory.createEmptyBorder(10, 5, 10,50));

    setBackground(new Color(0xB5C3C3)); // 设置背景色（仅用于测试）
    String dataPath = dataProcessor.getDataPath();

    addButton = createIconButton(dataPath + "icon/add.png", "添加");
    refreshButton = createIconButton(dataPath + "icon/refresh.png", "刷新");
    saveButton = createIconButton(dataPath + "icon/save.png", "保存");
    decryptButton = createIconButton(dataPath + "icon/decrypt.png", "解密");
    labelButton = createIconButton(dataPath + "icon/tag.png", "标签");
    reverseorderbutton = createIconButton(dataPath + "icon/reverseorder.png", "倒序");
    girdbutton = createIconButton(dataPath + "icon/table.png", "网格视图");
    listbutton = createIconButton(dataPath + "icon/list.png", "列表视图");
    privataButton= createIconButton(dataPath + "icon/private.png", "隐私视图");
    searchField = new JTextField(20);
    searchField.setFont(new Font("Dialog", Font.PLAIN, 20));
    searchButton = createIconButton(dataPath + "icon/search.png", "搜索");

    add(addButton);
    addButton.addActionListener(e -> handleAddButtonClick());
    add(Box.createHorizontalStrut(10)); // 添加水平间隔
    add(refreshButton);
    refreshButton.addActionListener(e -> handleRefreshButtonClick());
    add(Box.createHorizontalStrut(10));
    add(saveButton);
    saveButton.addActionListener(e -> handleSaveButtonClick());
    add(Box.createHorizontalStrut(10));
    add(girdbutton);
    girdbutton.addActionListener(e -> handleGirdButtonClick());
    add(Box.createHorizontalStrut(10));
    add(listbutton);
    listbutton.addActionListener(e -> handleListButtonClick());
    add(Box.createHorizontalStrut(10));
    add(privataButton);
    privataButton.addActionListener(e -> handlePrivataButtonClick());
    add(Box.createHorizontalStrut(10));
    add(decryptButton);
    decryptButton.addActionListener(e -> handleDecryptButtonClick());
    add(Box.createHorizontalStrut(130));
    add(labelButton);
    labelButton.addActionListener(e -> handleLabelButtonClick());
    add(Box.createHorizontalStrut(5));
    add(searchField);
    add(Box.createHorizontalStrut(5));
    add(searchButton);
    searchButton.addActionListener(e -> handleSearchButtonClick());
    add(Box.createHorizontalStrut(5));
    add(reverseorderbutton);
    reverseorderbutton.addActionListener(e -> handleReverseOrderButtonClick());

    // 让组件在垂直方向上居中
    setAlignmentY(Component.CENTER_ALIGNMENT); // 设置面板内组件垂直居中



}

    private JButton createIconButton(String iconPath, String tooltip) {
        // 直接加载图片
        ImageIcon icon = new ImageIcon(iconPath);

        // 创建按钮并设置图标
        JButton button = new JButton(icon);

        // 根据图标大小设置按钮大小
        button.setPreferredSize(new Dimension(48, 48));

        // 移除按钮的边框和背景
        button.setBorderPainted(false);

        button.setFocusPainted(false);

        // 设置按钮提示文本
        button.setToolTipText(tooltip);

        return button;
    }

    private void updatePrivateButtonIcon() {
        // 根据 privateTag 的值选择图片路径
        String datapath=dataProcessor.getDataPath();
        String iconPath = (privateTag == 1) ? datapath+"icon/isPrivate.png" : datapath+"icon/private.png";

        // 加载图片
        ImageIcon icon = new ImageIcon(iconPath);

        // 设置按钮的图标
        privataButton.setIcon(icon);

        // 更新按钮的提示文本
        privataButton.setToolTipText((privateTag == 1) ? "隐藏隐私" : "展示隐私");
    }

    // 增加按钮点击事件处理方法
    private void handleAddButtonClick() {
        System.out.println("增加按钮被点击");
        new AddPictureFrame(dataProcessor);
    }

    // 刷新按钮点击事件处理方法
    private void handleRefreshButtonClick() {
    List<Picture> list=new ArrayList<Picture>();
        if(privateTag ==1&&reverseOrderTag==1){
            list=dataProcessor.getPicturesList();
            Collections.reverse(list);
            pictureListPanel.setLoadpictures(list);
            pictureListPanel.setPanel(modelTag);
          //  System.out.println("case 1刷新按钮被点击");
        }else if(privateTag ==1&&reverseOrderTag==0){
            list=dataProcessor.getPicturesList();
            pictureListPanel.setLoadpictures(list);
            pictureListPanel.setPanel(modelTag);
           // System.out.println("case 2刷新按钮被点击");
        }else if(privateTag ==0&&reverseOrderTag==1){
            list=dataProcessor.getPicturesListExceptPrivate();
            Collections.reverse(list);
            pictureListPanel.setLoadpictures(list);
            pictureListPanel.setPanel(modelTag);
            //System.out.println("case 3刷新按钮被点击");
        }else{
            list=dataProcessor.getPicturesListExceptPrivate();
            pictureListPanel.setLoadpictures(list);
            pictureListPanel.setPanel(modelTag);
            //System.out.println("case 4刷新按钮被点击");
        }

    }

    // 保存按钮点击事件处理方法
    private void handleSaveButtonClick() {
        System.out.println("保存按钮被点击");


        // 弹出确认保存的弹窗
        int option = JOptionPane.showConfirmDialog(
                this.getParent().getParent(), // 父组件
                "确认进行保存吗？", // 提示信息
                "确认保存", // 弹窗标题
                JOptionPane.YES_NO_OPTION, // 按钮选项（是/否）
                JOptionPane.WARNING_MESSAGE // 弹窗类型（警告图标）
        );

        if (option == JOptionPane.YES_OPTION) {
            try {
                dataProcessor.saveData();

                JOptionPane.showMessageDialog(
                        this.getParent().getParent(),
                        "保存成功！",
                        "成功",
                        JOptionPane.INFORMATION_MESSAGE
                );
            } catch (Exception e) {
                JOptionPane.showMessageDialog(
                        this.getParent().getParent(),
                        "保存失败：" + e.getMessage(),
                        "错误",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    // 表格按钮点击事件处理方法
    private void handleGirdButtonClick() {
        System.out.println("表格按钮被点击");
        modelTag=pGirdModelTAG;
        if(privateTag ==1){
            pictureListPanel.setLoadpictures(dataProcessor.getPicturesList());
            pictureListPanel.setPanel(modelTag);
        }else{
            pictureListPanel.setLoadpictures(dataProcessor.getPicturesListExceptPrivate());
            pictureListPanel.setPanel(modelTag);
        }

    }

    // 列表按钮点击事件处理方法
    private void handleListButtonClick() {
        modelTag=pListModelTAG;
        pictureListPanel.setPanel(modelTag);
        System.out.println("列表按钮被点击");
    }

    // 标签按钮点击事件处理方法
    private void handleLabelButtonClick() {
        System.out.println("标签按钮被点击");
        new SearchedTagsFrame(searchedTags, this);
    }

    // 搜索按钮点击事件处理方法
    private void handleSearchButtonClick() {
        System.out.println("搜索按钮被点击");
        List<Picture> list = dataProcessor.searchPictureByTagsAndText(searchedTags,searchField.getText(), privateTag);

        if(reverseOrderTag==1){
            Collections.reverse(list);
            pictureListPanel.setLoadpictures(list);
            pictureListPanel.setPanel(modelTag);

        }else{
            pictureListPanel.setLoadpictures(list);
            pictureListPanel.setPanel(modelTag);
        }

    }

    // 倒序按钮点击事件处理方法
    private void handleReverseOrderButtonClick() {
        System.out.println("倒序按钮被点击");
        if(reverseOrderTag==0){
            reverseOrderTag=1;
            handleRefreshButtonClick();
        }else{
            reverseOrderTag=0;
            handleRefreshButtonClick();
        }
    }

    // 隐私按钮点击事件处理方法
    private void handlePrivataButtonClick() {
        System.out.println("隐私按钮被点击");
    if(privateTag ==0){
        privateTag =1;
    }else{
        privateTag =0;
    }
        updatePrivateButtonIcon();

        handleRefreshButtonClick();


    }

    private void handleDecryptButtonClick(){
       new DecryptFrame(dataProcessor);
    }


    public void setSearchedTags(Set<String> searchedTags) {
        this.searchedTags = searchedTags;
    }

    public Set<String> getSearchedTags() {
        return searchedTags;
    }
}

