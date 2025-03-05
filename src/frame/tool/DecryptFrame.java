package frame.tool;

import common.DataProcessing;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class DecryptFrame extends JFrame {
    private DataProcessing dp;
    private JTable table;
    private DefaultTableModel tableModel;
    private JButton decryptButton;
    private JButton cancelButton;

    public DecryptFrame(DataProcessing dp) {
        this.dp = dp;
        setTitle("解密图片");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Initialize components
        initUI();

        setVisible(true);
    }

    private void initUI() {
        // Create the JTable and its model
        String[] columns = {"文件名", "文件大小"};
        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Only allow one row to be selected
        table.setDefaultEditor(Object.class, null);
        JScrollPane scrollPane = new JScrollPane(table);

        // Load encrypted files into the table
        loadEncryptedFiles();

        // Create buttons
        decryptButton = new JButton("解密");
        cancelButton = new JButton("取消");

        decryptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    String fileName = (String) table.getValueAt(selectedRow, 0);
                    File encryptedFile = new File(dp.getEncryptedPath(), fileName);
                    try {
                        dp.decryptFile(encryptedFile);
                        JOptionPane.showMessageDialog(DecryptFrame.this, "解密成功！");
                        // Refresh the table after decryption
                        loadEncryptedFiles();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(DecryptFrame.this, "解密失败: " + ex.getMessage());
                    }
                } else {
                    JOptionPane.showMessageDialog(DecryptFrame.this, "请先选择一个文件！");
                }
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                table.clearSelection(); // Deselect any selected row
            }
        });

        // Layout
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(decryptButton);
        buttonPanel.add(cancelButton);

        // Adding components to the frame
        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }


    private void loadEncryptedFiles() {
        File encryptedFolder = new File(dp.getEncryptedPath());
        File[] files = encryptedFolder.listFiles();
        tableModel.setRowCount(0);

        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    long fileSize = file.length();
                    String fileSizeString = String.format("%.2f MB", (double) fileSize / (1024 * 1024)  );
                    tableModel.addRow(new Object[]{file.getName(), fileSizeString});
                }
            }
        }
    }
}