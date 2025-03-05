package frame.tool;

import common.DataProcessing;
import common.Picture;
import net.coobird.thumbnailator.Thumbnails;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GirdPanel extends JPanel {
    private DataProcessing dp;
    private List<Picture> loadpictures;
    private File losePictureFile;
    private Map<Picture, ImageIcon> imageCache; // 缓存已加载的图片

    public GirdPanel(DataProcessing dataProcessor, List<Picture> loadpictures) {
        this.dp = dataProcessor;
        this.loadpictures = loadpictures;
        this.losePictureFile = new File(dp.getDataPath(), "fail.png");
        this.imageCache = new HashMap<>(); // 初始化缓存

        // 使用JList来显示图片
        JList<Picture> pictureList = new JList<>(loadpictures.toArray(new Picture[0]));
        pictureList.setCellRenderer(new PictureListCellRenderer());
        pictureList.setLayoutOrientation(JList.HORIZONTAL_WRAP); // 水平换行布局
        pictureList.setVisibleRowCount(-1); // 自动计算行数
        pictureList.setFixedCellWidth(258); // 每个单元格的宽度
        pictureList.setFixedCellHeight(180); // 每个单元格的高度

        // 设置每行显示4个单元格
        pictureList.setVisibleRowCount(loadpictures.size() / 4 + 1);

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

        // 使用JScrollPane来支持滚动
        JScrollPane scrollPane = new JScrollPane(pictureList);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(15); // 设置滚轮每次滚动的单位增量
        scrollPane.getVerticalScrollBar().setBlockIncrement(50); // 设置块增量（例如点击滚动条空白处时的滚动量）

        this.setLayout(new BorderLayout());
        this.add(scrollPane, BorderLayout.CENTER);
    }

    // 自定义ListCellRenderer来渲染图片
    private class PictureListCellRenderer extends JPanel implements ListCellRenderer<Picture> {
        private JLabel thumbnailLabel;

        public PictureListCellRenderer() {
            setLayout(new BorderLayout());
            setPreferredSize(new Dimension(120, 150));
            setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

            thumbnailLabel = new JLabel();
            thumbnailLabel.setHorizontalAlignment(SwingConstants.CENTER);
            add(thumbnailLabel, BorderLayout.CENTER);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends Picture> list, Picture picture, int index, boolean isSelected, boolean cellHasFocus) {
            // 检查缓存中是否已经加载过该图片
            if (imageCache.containsKey(picture)) {
                thumbnailLabel.setIcon(imageCache.get(picture)); // 使用缓存的图片
            } else {
                // 如果缓存中没有，则异步加载图片
                thumbnailLabel.setIcon(null); // 清空当前显示的图片
                SwingWorker<ImageIcon, Void> worker = new SwingWorker<ImageIcon, Void>() {
                    @Override
                    protected ImageIcon doInBackground() throws Exception {
                        return resizeImageWithThumbnailator(dp.tryGetPictureFile(picture).getPath(), 180, 150);
                    }

                    @Override
                    protected void done() {
                        try {
                            ImageIcon icon = get();
                            imageCache.put(picture, icon); // 将加载的图片放入缓存
                            if (list.getModel().getElementAt(index).equals(picture)) {
                                thumbnailLabel.setIcon(icon); // 确保当前渲染的项仍然是同一个Picture
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
            return null;
        }
    }
}