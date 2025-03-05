package common;

import java.io.Serializable;

public class PictureInfo implements Serializable {
    private static final long serialVersionUID = 3L;
    private int networkSeq;
    private String artist;
    private String pid;
    private String user;

    public PictureInfo(int networkSeq, String artist, String pid, String user) {
        this.networkSeq = networkSeq;
        this.artist = artist;
        this.pid = pid;
        this.user = user;
    }

    // Getters
    public int getNetworkSeq() { return networkSeq; }
    public String getArtist() { return artist; }
    public String getPid() { return pid; }
    public String getUser() { return user; }
}