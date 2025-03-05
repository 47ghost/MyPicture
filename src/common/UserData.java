package common;

import java.io.File;
import java.io.Serializable;

public class UserData implements Serializable {
    private static final long serialVersionUID = 2L;
    private  String picturePath;//无用
    private  String temporaryPicturesPath;
    private  String encryptedPath;
    private  String decryptedPath;
    private  String downloadPath ;
    private String serverIP;
    private  String useraddPicturePath;



    public UserData() {
        this.picturePath ="pictures//";
        this.temporaryPicturesPath = "temporaryPictures//";
        this.encryptedPath = "encrypted//";
        this.decryptedPath = "decrypted//";
        this.downloadPath = "download//";
        this.serverIP = "127.0.0.1";
        this.useraddPicturePath="AddPictures//";

        createDirectoryIfNotExists(temporaryPicturesPath);
        createDirectoryIfNotExists(encryptedPath);
        createDirectoryIfNotExists(decryptedPath);
        createDirectoryIfNotExists(downloadPath);
        createDirectoryIfNotExists(useraddPicturePath);

    }

    // 创建文件夹（如果不存在）
    private void createDirectoryIfNotExists(String path) {
        File directory = new File(path);
        if (!directory.exists()) {
            boolean success = directory.mkdirs();
            if (!success) {
                System.err.println("无法创建文件夹: " + path);
            }
        }
    }

    public String getPicturePath() {
        return picturePath;
    }

    public void setPicturePath(String picturePath) {
        this.picturePath = picturePath;
    }

    public String getTemporaryPicturesPath() {
        return temporaryPicturesPath;
    }

    public void setTemporaryPicturesPath(String temporaryPicturesPath) {
        this.temporaryPicturesPath = temporaryPicturesPath;
    }

    public String getEncryptedPath() {
        return encryptedPath;
    }

    public void setEncryptedPath(String encryptedPath) {
        this.encryptedPath = encryptedPath;
    }

    public String getDecryptedPath() {
        return decryptedPath;
    }

    public void setDecryptedPath(String decryptedPath) {
        this.decryptedPath = decryptedPath;
    }

    public String getDownloadPath() {
        return downloadPath;
    }

    public void setDownloadPath(String downloadPath) {
        this.downloadPath = downloadPath;
    }

    public String getServerIP() {
        return serverIP;
    }

    public void setServerIP(String serverIP) {
        this.serverIP = serverIP;
    }

    public String getUseraddPicturePath() {
        return useraddPicturePath;
    }

    public void setUseraddPicturePath(String useraddPicturePath) {
        this.useraddPicturePath = useraddPicturePath;
    }
}
