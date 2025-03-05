package frame.mainpanel;

import common.DataProcessing;
import common.Picture;
import frame.tool.GirdPanel;
import frame.tool.ListPanel;
import frame.headpanel.PictureListHead;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class PictureListPanel extends JPanel {
    private DataProcessing dp;
    private List<Picture> loadpictures;

    public PictureListPanel(DataProcessing dataProcessing) {
        dp=dataProcessing;
        this.loadpictures=dp.getPicturesListExceptPrivate();
        setLayout(new BorderLayout());
        JLabel label = new JLabel("刷新获得图片", SwingConstants.CENTER);
        Font largerFont = new Font("Serif", Font.BOLD, 36);
        label.setFont(largerFont);
        add(label, BorderLayout.CENTER);

    }
    public void setPanel(int modeltag){
        removeAll();
        switch (modeltag) {
            case PictureListHead.pListModelTAG:
                add(new ListPanel(dp,loadpictures), BorderLayout.CENTER);
                break;
            case PictureListHead.pGirdModelTAG:
                add(new GirdPanel(dp,loadpictures), BorderLayout.CENTER);
                break;
            default:
                add(new JLabel("错误，刷新获得图片", SwingConstants.CENTER), BorderLayout.CENTER);
                break;
        }
        revalidate();
        repaint();


    }

    public void setLoadpictures(List<Picture> loadpictures) {
        this.loadpictures = loadpictures;
    }
}
