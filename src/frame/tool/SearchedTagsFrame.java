package frame.tool;

import frame.headpanel.PictureListHead;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class SearchedTagsFrame extends JFrame{
    private Set<String> searchedTags;
    private JTextArea inputTags;
    private JButton confirm;
    private PictureListHead pictureListHead;
    public SearchedTagsFrame(Set<String> searchedTags, PictureListHead pictureListHead){
        this.searchedTags = searchedTags;
        this.pictureListHead = pictureListHead;
        setTitle("查找标签");
        setSize(500,300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());


        if(searchedTags!=null&&searchedTags.size()!=0){
        inputTags= new JTextArea(String.join(" ", searchedTags), 3, 30);}
        else{
            inputTags= new JTextArea(3, 30);
        }
        inputTags.setFont(new Font("Microsoft YaHei Mono", Font.BOLD, 20));
        inputTags.setLineWrap(true);
        add(inputTags, BorderLayout.CENTER);

        confirm = new JButton("确定");
        confirm.setPreferredSize(new Dimension(80, 30));
        confirm.setFont(new Font("Microsoft YaHei Mono", Font.BOLD, 20)); // 设置字体
        confirm.addActionListener(e -> handleConfirmButtonClick());
        add(confirm, BorderLayout.SOUTH);



        setVisible(true);
    }
    void handleConfirmButtonClick(){
        Set<String> gettags =   Arrays.stream(inputTags.getText().split("\\s+")) // 分割成数组
                .filter(tag -> !tag.isEmpty())      // 过滤掉空字符串
                .collect(Collectors.toSet());
        pictureListHead.setSearchedTags(gettags);

        dispose();

    }


}
