package frame.mainpanel;


import common.DataProcessing;
import common.PictureInfo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class SharePanel extends JPanel {
    private DataProcessing dp;
    private JTable table;
    private DefaultTableModel tableModel;

    public SharePanel(DataProcessing dp) {
        this.dp = dp;
        setLayout(new BorderLayout());

        // 初始化表格模型
        tableModel = new DefaultTableModel(new Object[]{"序号", "画师", "Pid", "上传用户"}, 0);
        table = new JTable(tableModel);

        // 设置表格选择模式为单行选择
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setDefaultEditor(Object.class, null);

        // 添加表格到滚动面板
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // 添加下载和取消按钮
        JPanel buttonPanel = new JPanel();
        JButton downloadButton = new JButton("下载");
        JButton cancelButton = new JButton("取消");

        buttonPanel.add(downloadButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // 下载按钮事件处理
        downloadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    int networkSeq = (int) tableModel.getValueAt(selectedRow, 0);
                    String savePath = dp.getDownloadPath() + networkSeq + ".jpg"; // 设置保存路径
                    dp.downloadPicture(networkSeq, savePath);
                    JOptionPane.showMessageDialog(SharePanel.this, "下载完成！");
                } else {
                    JOptionPane.showMessageDialog(SharePanel.this, "请先选择一张图片！");
                }
            }
        });

        // 取消按钮事件处理
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                table.clearSelection();
            }
        });
    }

    public void setPanel() {
        // 清空表格
        tableModel.setRowCount(0);

        // 从服务器获取图片信息
        List<PictureInfo> pictureInfos = dp.listPictures();

        if (pictureInfos == null) {
            JOptionPane.showMessageDialog(this, "获取图片信息失败！");
            return;
        }

        if (pictureInfos.isEmpty()) {
            // 如果列表为空，添加一行空白信息
            tableModel.addRow(new Object[]{"", "", "", ""});
        } else {
            // 将图片信息添加到表格中
            for (PictureInfo info : pictureInfos) {
                tableModel.addRow(new Object[]{info.getNetworkSeq(), info.getArtist(), info.getPid(), info.getUser()});
            }
        }

        // 重绘面板
        repaint();
    }
}