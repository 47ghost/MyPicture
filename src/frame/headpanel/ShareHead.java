package frame.headpanel;

import common.DataProcessing;
import frame.mainpanel.SharePanel;

import javax.swing.*;
import java.awt.*;

public class ShareHead extends JPanel {
    private  DataProcessing dataProcessor;
    private SharePanel sharePanel;
    private JButton  refreshButton;

    public ShareHead(DataProcessing dp,SharePanel sharePanel){
        this.dataProcessor = dp;
        this.sharePanel = sharePanel;
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setBorder(BorderFactory.createEmptyBorder(10, 5, 10,50));
        setBackground(new Color(0xB5C3C3)); // 设置背景色

        refreshButton = createIconButton(dataProcessor.getDataPath()+"icon/refresh.png", "刷新");

        add(Box.createHorizontalStrut(20));
        add(refreshButton);
        refreshButton.addActionListener(e -> handleRefreshButtonClick());


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
    private void handleRefreshButtonClick() {

        sharePanel.setPanel();
    }
}
