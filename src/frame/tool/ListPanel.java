package frame.tool;

import common.DataProcessing;
import common.Picture;
import net.coobird.thumbnailator.Thumbnails;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListPanel extends JPanel {
    private DataProcessing dp;
    private Font font;
    private List<Picture> loadpictures;
    private File losePictureFile;
    private Map<Picture, ImageIcon> imageCache; // 缓存已加载的图片

    public ListPanel(DataProcessing dataProcessor, List<Picture> loadpictures) {
        this.dp = dataProcessor;
        this.loadpictures = loadpictures;
        this.losePictureFile = new File(dp.getDataPath(), "fail.png");
        this.imageCache = new HashMap<>(); // 初始化缓存
        font = new Font("Microsoft YaHei Mono", Font.BOLD, 20);

        // 创建 JList 并设置其渲染器
        JList<Picture> pictureList = new JList<>(loadpictures.toArray(new Picture[0]));
        pictureList.setCellRenderer(new PictureListCellRenderer());
        pictureList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // 添加双击事件监听器
        pictureList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    Picture selectedPicture = pictureList.getSelectedValue();
                    if (selectedPicture != null) {
                        new SinglePictureFrame(selectedPicture, dp);
                    }
                }
            }
        });

        // 将 JList 放入 JScrollPane 中
        JScrollPane scrollPane = new JScrollPane(pictureList);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        // 修改滚轮速度
        scrollPane.getVerticalScrollBar().setUnitIncrement(15); // 设置滚轮每次滚动的单位增量
        scrollPane.getVerticalScrollBar().setBlockIncrement(50); // 设置块增量（例如点击滚动条空白处时的滚动量）

        scrollPane.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
                int units = e.getUnitsToScroll(); // 获取默认滚轮滚动单位
                verticalScrollBar.setValue(verticalScrollBar.getValue() + (units * 1)); // 调整滚轮速度
                e.consume(); // 阻止默认的滚轮行为
            }
        });



        // 设置 ListPanel 的布局并添加 JScrollPane
        this.setLayout(new BorderLayout());
        this.add(scrollPane, BorderLayout.CENTER);
    }

    // 自定义 JList 的渲染器
    private class PictureListCellRenderer extends JPanel implements ListCellRenderer<Picture> {
        private JLabel thumbnailLabel;
        private JLabel idLabel;
        private JLabel artistLabel;
        private JLabel pidLabel;
        private JPanel tagWrapperPanel;

        public PictureListCellRenderer() {
            setLayout(new BorderLayout());
            setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));

            // 左侧面板：固定大小并居中显示图片
            JPanel leftPanel = new JPanel(new BorderLayout());
            leftPanel.setPreferredSize(new Dimension(434, 244));
            leftPanel.setMaximumSize(new Dimension(434, 244));

            thumbnailLabel = new JLabel(new ImageIcon(losePictureFile.getAbsolutePath()));
            thumbnailLabel.setHorizontalAlignment(SwingConstants.CENTER);
            thumbnailLabel.setVerticalAlignment(SwingConstants.CENTER);
            leftPanel.add(thumbnailLabel, BorderLayout.CENTER);

            // 右侧面板：显示文字信息
            JPanel rightPanel = new JPanel();
            rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
            JLabel empty1 = new JLabel("  ");
            idLabel = new JLabel();
            artistLabel = new JLabel();
            pidLabel = new JLabel();

            tagWrapperPanel = new JPanel();
            tagWrapperPanel.setLayout(new BoxLayout(tagWrapperPanel, BoxLayout.Y_AXIS));
            tagWrapperPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

            idLabel.setFont(font);
            artistLabel.setFont(font);
            pidLabel.setFont(font);
            empty1.setFont(font);
            rightPanel.add(empty1);
            rightPanel.add(idLabel);
            rightPanel.add(artistLabel);
            rightPanel.add(pidLabel);
            rightPanel.add(tagWrapperPanel);

            // 将左侧面板和右侧面板添加到 itemPanel
            add(leftPanel, BorderLayout.WEST);
            add(rightPanel, BorderLayout.CENTER);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends Picture> list, Picture picture, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            // 设置文本信息
            idLabel.setText(" 序号 " + picture.getId());
            artistLabel.setText(" 画师 " + picture.getArtist());
            pidLabel.setText(" pid " + picture.getPid());

            // 清空之前的标签
            tagWrapperPanel.removeAll();
            JPanel currentRowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
            tagWrapperPanel.add(currentRowPanel);

            int tagCount = 0; // 计数器，记录当前行已添加的标签数量
            int maxTagsPerRow = 7; // 每行最多显示的标签数量

            for (TagLabel tagLabel : TagLabel.createTagLabels(picture.getTags())) {
                currentRowPanel.add(tagLabel);
                tagCount++;

                // 如果当前行已添加 5 个标签，则创建新的一行
                if (tagCount % maxTagsPerRow == 0) {
                    currentRowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
                    tagWrapperPanel.add(currentRowPanel);
                }
            }

            // 检查缓存中是否已有图片
            if (imageCache.containsKey(picture)) {
                thumbnailLabel.setIcon(imageCache.get(picture)); // 使用缓存的图片
            } else {
                // 设置默认图片
                thumbnailLabel.setIcon(null);

                // 异步加载图片
                SwingWorker<ImageIcon, Void> worker = new SwingWorker<ImageIcon, Void>() {
                    @Override
                    protected ImageIcon doInBackground() throws Exception {
                        return resizeImageWithThumbnailator(dp.tryGetPictureFile(picture).getPath(), 434, 244);
                    }

                    @Override
                    protected void done() {
                        try {
                            ImageIcon icon = get();
                            imageCache.put(picture, icon); // 缓存图片
                            if (list.getModel().getElementAt(index).equals(picture)) { // 确保当前项仍然是需要更新的项
                                thumbnailLabel.setIcon(icon); // 更新缩略图
                            }
                        } catch (Exception e) {
                            thumbnailLabel.setIcon(new ImageIcon(losePictureFile.getAbsolutePath()));
                        }
                    }
                };
                worker.execute();
            }



            return this;
        }
    }

    private ImageIcon resizeImageWithThumbnailator(String filePath, int width, int height) {
        try {
            // 使用 Thumbnailator 缩放图片
            BufferedImage scaledImage = Thumbnails.of(new File(filePath))
                    .size(width, height)
                    .keepAspectRatio(true) // 保持比例
                    .asBufferedImage();

            return new ImageIcon(scaledImage);
        } catch (IOException e) {
            return new ImageIcon(losePictureFile.getAbsolutePath());
        }
    }
}