package frame.tool;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TagLabel extends JLabel {

    // 固定的颜色序列
    private static final Color[] COLOR_SEQUENCE = {
            new Color(255, 99, 132), // 红色
            new Color(255, 159, 64), // 橙色
            new Color(255, 205, 86), // 黄色
            new Color(75, 192, 192), // 青色
            new Color(54, 162, 235), // 蓝色
            new Color(153, 102, 255), // 紫色
            new Color(129, 134, 140), // 灰色
            new Color(255, 87, 34), // 深橙色
            new Color(76, 175, 80), // 绿色
            new Color(156, 39, 176), // 深紫色
    };
    private static int colorIndex = 0; // 用于记录当前颜色序列的索引
    private Color backgroundColor;
    private Font font;

    public TagLabel(String text) {
        super(text);
        font = new Font("Microsoft YaHei Mono", Font.BOLD, 20);
        if ("私密".equals(text)) {
            this.backgroundColor = Color.BLACK; // 私密标签为黑色
        } else {
            this.backgroundColor = getNextColor(); // 使用颜色序列中的颜色
        }
        setOpaque(false); // 设置为透明
        setForeground(Color.WHITE);
        setHorizontalAlignment(SwingConstants.CENTER); // 文字居中
        setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8)); // 设置内边距

        setFont(font);
        FontMetrics fm = getFontMetrics(getFont());
        int width = fm.stringWidth(getText()) + 16; // 文本宽度 + 左右内边距
        int height = fm.getHeight() + 8; // 文本高度 + 上下内边距
        setPreferredSize(new Dimension(width, height));
    }

    public TagLabel(String text, Color color) {
        super(text);
        font = new Font("Microsoft YaHei Mono", Font.BOLD, 20);
        this.backgroundColor = color;
        setOpaque(false); // 设置为透明，用于自定义背景
        setForeground(Color.WHITE); // 设置文字颜色
        setHorizontalAlignment(SwingConstants.CENTER); // 文字居中
        setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8)); // 设置内边距

        // 动态调整大小
        setFont(font);
        FontMetrics fm = getFontMetrics(getFont());
        int width = fm.stringWidth(getText()) + 16; // 文本宽度 + 左右内边距
        int height = fm.getHeight() + 8; // 文本高度 + 上下内边距
        setPreferredSize(new Dimension(width, height));
    }

    // 获取颜色序列中的下一个颜色
    private static Color getNextColor() {
        Color color = COLOR_SEQUENCE[colorIndex];
        colorIndex = (colorIndex + 1) % COLOR_SEQUENCE.length; // 循环使用颜色序列
        return color;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 绘制圆角矩形背景
        g2d.setColor(backgroundColor);
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20); // 圆角半径为 20

        super.paintComponent(g2d); // 绘制文字
        g2d.dispose();
    }

    // 返回相邻标签不同颜色的 List<TagLabel>
    public static List<TagLabel> createTagLabels(Set<String> tags) {
        List<TagLabel> tagLabels = new ArrayList<>();
        colorIndex = 0; // 重置颜色索引

        for (String tag : tags) {
            Color color;
            if ("私密".equals(tag)) {
                color = Color.BLACK; // 私密标签为黑色
            } else {
                color = getNextColor(); // 使用颜色序列中的颜色
            }
            TagLabel tagLabel = new TagLabel(tag, color);
            tagLabels.add(tagLabel);
        }

        return tagLabels;
    }
}