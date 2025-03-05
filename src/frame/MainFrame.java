package frame;

import common.DataProcessing;
import frame.headpanel.ComicListHead;
import frame.headpanel.PictureListHead;
import frame.headpanel.SettingHead;
import frame.headpanel.ShareHead;
import frame.mainpanel.ComicListPanel;
import frame.mainpanel.PictureListPanel;
import frame.mainpanel.SettingPanel;
import frame.mainpanel.SharePanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainFrame {
    private JPanel root;
    private JPanel leftPanel;
    private JButton button1;
    private JButton button2;
    private JButton button3;
    private JButton button4;
    private JPanel blank_1;
    private JPanel buttonpanel;
    private JPanel functionpanel;
    private JPanel mainfunction;
    private JPanel selectpanel;
    private JLabel title;
    private JLabel userpicture;
    private JPanel blank_2;
    private CardLayout cardLayout;
    private CardLayout headcardLayout;
    private Color leftColor = new Color(0xAEE8E7);
    private Color rightheadColor = new Color(0x28E1E4);
    private Color line = new Color(0x748080);
    private final DataProcessing dataProcessor;

    public MainFrame(DataProcessing dp) {
        dataProcessor = dp;
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainfunction, "Function1");
                headcardLayout.show(selectpanel, "Function1");
            }
        });
        button2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainfunction, "Function2");
                headcardLayout.show(selectpanel, "Function2");

            }
        });
        button3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainfunction, "Function3");
                headcardLayout.show(selectpanel, "Function3");
            }
        });
        button4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainfunction, "Function4");
                headcardLayout.show(selectpanel, "Function4");
            }
        });



    }

    public void initFrame() {
        //按钮
        button1.setFocusPainted(false);
        button2.setFocusPainted(false);
        button3.setFocusPainted(false);
        button4.setFocusPainted(false);

        //左侧
        leftPanel.setPreferredSize(new Dimension(150, 0));
        leftPanel.setBackground(leftColor);
        buttonpanel.setBackground(leftColor);
        buttonpanel.setBorder(BorderFactory.createMatteBorder(2, 0, 0,0, line));
        leftPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 2, line));

        blank_1.setBackground(leftColor);
        blank_2.setBackground(leftColor);

        button1.setPreferredSize(new Dimension(100, 50));
        button2.setPreferredSize(new Dimension(100, 50));
        button3.setPreferredSize(new Dimension(100, 50));
        button4.setPreferredSize(new Dimension(100, 50));

        ImageIcon user = new ImageIcon(dataProcessor.getDataPath()+"user.png");
        userpicture.setIcon(user);
        userpicture.setHorizontalAlignment(JLabel.CENTER);
        userpicture.setVerticalAlignment(JLabel.CENTER);


        //右侧
        selectpanel.setBackground(rightheadColor);
        selectpanel.setPreferredSize(new Dimension(0, 70));

        cardLayout = new CardLayout();
        mainfunction.setLayout(cardLayout);
        PictureListPanel pictureListPanel = new PictureListPanel(dataProcessor);
        mainfunction.add(pictureListPanel, "Function1");
        SharePanel sharePanel=new SharePanel(dataProcessor);
        mainfunction.add(sharePanel, "Function2");
        mainfunction.add(new ComicListPanel(), "Function3");
        mainfunction.add(new SettingPanel(dataProcessor), "Function4");

        headcardLayout = new CardLayout();
        selectpanel.setLayout(headcardLayout);
        selectpanel.add(new PictureListHead(dataProcessor,pictureListPanel), "Function1");
        selectpanel.add(new ShareHead(dataProcessor,sharePanel), "Function2");
        selectpanel.add(new ComicListHead(), "Function3");
        selectpanel.add(new SettingHead(), "Function4");




        //窗口
        JFrame frame = new JFrame("图库");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame. addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // 弹出确认对话框
                int option = JOptionPane.showConfirmDialog(
                        frame, // 父组件
                        "是否保存并关闭窗口？", // 提示信息
                        "关闭窗口", // 标题
                        JOptionPane.YES_NO_CANCEL_OPTION // 选项类型
                );

                if (option == JOptionPane.YES_OPTION) {
                    // 用户选择“是”，调用 save 方法并关闭窗口
                    dataProcessor.saveData();
                    System.exit(0);
                } else if (option == JOptionPane.NO_OPTION) {
                    // 用户选择“否”，直接关闭窗口
                    System.exit(0);
                }
                // 用户选择“取消”，不执行任何操作
            }
        });

        frame.setContentPane(root);
        frame.setSize(1200, 700);
        frame.setLocationRelativeTo(null);
        ImageIcon customIcon = new ImageIcon(dataProcessor.getDataPath()+"title.png");
        frame.setIconImage(customIcon.getImage());
        frame.setVisible(true);

    }


}
