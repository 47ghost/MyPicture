package common;

import java.io.File;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

public class Picture implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;//唯一标识符
    private File localfile;//本地引用
    private File encryptedfile;//加密后引用
    private String artist;//画师
    private String pid;//pixiv平台
    private Timestamp timestamp;//图片添加时间
    private Set<String> tags = new HashSet<>();

    public Picture(int id, File localfile, File encryptedfile, String artist, String pid, Timestamp timestamp, Set<String> tags) {
        this.id = id;
        this.localfile = localfile;
        this.encryptedfile = encryptedfile;
        this.artist = artist;
        this.pid = pid;
        this.timestamp = timestamp;
        this.tags = tags;
    }

    public Picture(int id, File localfile, String artist, String pid, Timestamp timestamp, Set<String> tags) {
        this.id = id;
        this.localfile = localfile;
        this.artist = artist;
        this.pid = pid;
        this.timestamp = timestamp;
        this.tags = tags;
    }

    public boolean addTag(String tag) {
        if (tags.contains(tag)) {
            return false;
        } else {
            tags.add(tag);
            return true;
        }

    }

    public boolean removeTag(String tag) {
        if (tags.contains(tag)) {
            tags.remove(tag);
            return true;
        } else {
            return false;
        }
    }

    public String printTags() {
        if (tags == null || tags.isEmpty()) {
            return "无标签";
        }

        StringBuilder result = new StringBuilder(); // 用于拼接结果
        int count = 0; // 计数器，记录当前已拼接的标签数量

        for (String tag : tags) {
            result.append(tag).append(" "); // 拼接标签和空格
            count++;

            if (count % 4 == 0) { // 每 4 个标签换行
                result.append("\n");
            }
        }

        return result.toString().trim(); // 返回结果并去掉末尾多余的空格或换行
    }





    public boolean searchTags(String... tag) {
        for (String t : tag) {
            if (!tags.contains(t)) {
                return false;
            }
        }
        return true;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public File getLocalfile() {
        return localfile;
    }


    public void setLocalfile(File localfile) {
        this.localfile = localfile;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public File getEncryptedfile() {
        return encryptedfile;
    }

    public void setEncryptedfile(File encryptedfile) {
        this.encryptedfile = encryptedfile;
    }

    @Override
    public String toString() {
        return "Picture{" +
                "id=" + id +
                ", localfile=" + localfile +
                '}';
    }


}