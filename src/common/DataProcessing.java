package common;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.Socket;
import java.security.Key;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class DataProcessing {
    private Map<Integer, Picture> pictures = new ConcurrentHashMap<>();
    private static volatile DataProcessing instance;
    private  String picturePath="pictures//";
    private  String temporaryPicturesPath;
    private  String useraddPicturePath;
    private  UserData userData;
    private  String encryptedPath;
    private  String decryptedPath;
    private  String downloadPath ;
    private final String dataPath ;
    private static final String ALGORITHM = "AES";
    private static final String SECRET_KEY = "J4v0rP3mJ5KuP6Ru9bHx6Q==";
    private String serverIP ;
    private int serverPort = 9081;


    // 私有构造方法（处理初始化异常）
    private DataProcessing()  {

        dataPath="data//";
        userData=new UserData();
        loadData();
        picturePath=userData.getPicturePath();
        temporaryPicturesPath=userData.getTemporaryPicturesPath();
        encryptedPath=userData.getEncryptedPath();
        decryptedPath=userData.getDecryptedPath();
        downloadPath=userData.getDownloadPath();
        serverIP=userData.getServerIP();
        useraddPicturePath=userData.getUseraddPicturePath();

    }
    // 双检锁实现线程安全
    public static DataProcessing getInstance() {
        if (instance == null) {
            synchronized (DataProcessing.class) {
                if (instance == null) {
                    try {
                        instance = new DataProcessing();

                    } catch (Exception e) {
                        throw new RuntimeException("初始化失败", e);
                    }
                }
            }
        }
        return instance;
    }

    //添加图片
    public boolean addPicture(Picture picture) {
        int nextid = getNextid();

        if (picture.getLocalfile().length() == 0) {
            return false;
        } else if (pictures.containsKey(picture.getId())) {
            return false;
        } else if (nextid != picture.getId()) {
            return false;
        } else if (picture.searchTags("私密")) {
            return false;
        } else {
            pictures.put(picture.getId(), picture);
            return true;
        }
    }
    public boolean addPrivatePicture(Picture picture) throws Exception {
        int nextid = getNextid();
        if (picture.getLocalfile().length() == 0) {
            return false;
        } else if (pictures.containsKey(picture.getId())) {
            return false;
        } else if (nextid != picture.getId()) {
            return false;
        } else if (!picture.searchTags("私密")) {
            return false;
        } else {
            encryptFile(picture.getLocalfile());
            picture.setEncryptedfile(new File(encryptedPath+picture.getLocalfile().getName()));
            pictures.put(picture.getId(), picture);
            return true;

        }

    }
    public int getNextid(){
        int max=0;
        if(pictures.isEmpty()){
            return 1;
        }
        for (Map.Entry<Integer, Picture> entry : pictures.entrySet()){
            if(entry.getKey()>max){
                max=entry.getKey();
            }
        }
        return max+1;


    }
    //SAVE&LOAD
    public void saveData() {
        try {
            File saveFile = new File(dataPath, "pictures.dat");
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(saveFile))) {
                oos.writeObject(pictures);
                oos.writeObject(userData);
                System.out.println("数据已保存：" + saveFile.getAbsolutePath());
            }
        } catch (Exception e) {
            System.out.println("保存失败");
            e.printStackTrace();
        }
    }

    public void loadData() {
        try {
            File dataFile = new File(dataPath, "pictures.dat");

            if (!dataFile.exists()) {
                System.out.println("文件不存在,加载默认数据");
                return;
            }

            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(dataFile))) {
                pictures = (ConcurrentHashMap<Integer, Picture>) ois.readObject();
                userData = (UserData) ois.readObject();
                System.out.println("数据已加载：" + dataFile.getAbsolutePath());
            }
        } catch (Exception e) {
            throw new RuntimeException("数据加载失败", e);
        }
    }


    //刷新
    private boolean isIdsContinuous() {
        int expectedId = 1;
        for (Map.Entry<Integer, Picture> entry : pictures.entrySet()) {
            if (entry.getKey() != expectedId) {
                return false; // 如果发现不符合连续 id，返回 false
            }
            expectedId++;
        }
        return true; // 全部符合连续 id，返回 true
    }
    public void refresh() {
        // 1. 删除 file 大小为0的 Picture 对象
        Iterator<Map.Entry<Integer, Picture>> iterator = pictures.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, Picture> entry = iterator.next();
            Picture picture = entry.getValue();
            File file = picture.getLocalfile();
            if (file.length() == 0) { // 如果文件大小为0
                iterator.remove(); // 移除该 Picture 对象
            }
        }

        // 2. 检查当前 id 是否已经连续
        if (!isIdsContinuous()) {
            // 如果 id 不连续，重新整理 id
            List<Picture> pictureList = new ArrayList<>(pictures.values());
            pictures.clear(); // 清空原始表
            for (int i = 0; i < pictureList.size(); i++) {
                Picture picture = pictureList.get(i);
                picture.setId(i + 1); // 重新设置 id
                pictures.put(i + 1, picture); // 重新放入表中
            }
        }
    }
    //删除图片
    public boolean removePicture(Picture picture) {
        if (pictures.containsKey(picture.getId())) {
            // 删除 Picture 对象
            pictures.remove(picture.getId());

            // 删除对应的 encryptedfile 文件
            File encryptedFile = picture.getEncryptedfile();
            if (encryptedFile != null && encryptedFile.exists()) {
                boolean isDeleted = encryptedFile.delete();
                if (!isDeleted) {
                    System.err.println("Failed to delete encrypted file: " + encryptedFile.getAbsolutePath());
                }
            }

            // 自动刷新集合
            refresh();
            return true;
        } else {
            return false;
        }
    }
    //修改图片
    public void changePictureInfo(Picture picture,String artist,String pid,String tags,String filePath) throws Exception {
        String[] tagArray = tags.split("\\s+");
        Set<String> tempTags =   Arrays.stream(tags.split("\\s+")) // 分割成数组
                              .filter(tag -> !tag.isEmpty())      // 过滤掉空字符串
                              .collect(Collectors.toSet());
        File tempFile = new File(filePath);

        picture.setArtist(artist);
        picture.setPid(pid);
        picture.setTags(tempTags);
        picture.setLocalfile(tempFile);
        if(tempTags.contains("私密")){
            encryptFile(picture.getLocalfile());
        }

    }

    //加密
    public static Key getKey() throws Exception {
        return new SecretKeySpec(SECRET_KEY.getBytes(), ALGORITHM);
    }
    public File encryptFile(File inputFile) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, getKey());
        File outputFile=new File(encryptedPath,inputFile.getName());

        try (InputStream is = new BufferedInputStream(new FileInputStream(inputFile));
             OutputStream os = new BufferedOutputStream(new FileOutputStream(outputFile));
             CipherOutputStream cos = new CipherOutputStream(os, cipher)) {
            byte[] buffer = new byte[8192]; // 8KB 缓冲区
            int len;
            while ((len = is.read(buffer)) != -1) {
                cos.write(buffer, 0, len);
            }
        }
        return outputFile;
    }
    public  void decryptFile(File inputFile) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, getKey());
        File outputFile=new File(decryptedPath,inputFile.getName());

        try (InputStream is = new BufferedInputStream(new FileInputStream(inputFile));
             CipherInputStream cis = new CipherInputStream(is, cipher);
             OutputStream os = new BufferedOutputStream(new FileOutputStream(outputFile))) {
            byte[] buffer = new byte[8192]; // 8KB 缓冲区
            int len;
            while ((len = cis.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
        }
    }
    public File decryptToTemporary(Picture picture) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, getKey());
        File outputFile=new File(temporaryPicturesPath,picture.getEncryptedfile().getName());

        try (InputStream is = new BufferedInputStream(new FileInputStream(picture.getEncryptedfile()));
             CipherInputStream cis = new CipherInputStream(is, cipher);
             OutputStream os = new BufferedOutputStream(new FileOutputStream(outputFile))) {
            byte[] buffer = new byte[8192]; // 8KB 缓冲区
            int len;
            while ((len = cis.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
        }
        return outputFile;

    }
    //工具
    public void printPicturesList() {
        for (Picture picture : getPicturesList()) {
            System.out.println(picture.getId() + " " + picture.getLocalfile()+ " " + picture.getTimestamp());
        }

    }
    public Map<Integer, Picture> getPictures() {
        return pictures;
    }
    public List<Picture> getPicturesList() {
        List<Picture> pictureList = new ArrayList<>(pictures.values());
        pictureList.sort(Comparator.comparingInt(Picture::getId));
        return pictureList;
    }
    public Picture getPicture(int id) {
        return pictures.get(id);
    }
    public File tryGetPictureFile(Picture picture) throws Exception {


        File localfile= picture.getLocalfile();
        File encryptedFile =picture.getEncryptedfile();

        if(localfile.exists()&&localfile.length()!=0){
            return localfile;
        }else if (encryptedFile.exists()&&encryptedFile.length()!=0){
            return decryptToTemporary(picture);
        }else if (picture.searchTags("私密")&&localfile.length()!=0){
            encryptFile(localfile);
            picture.setEncryptedfile(new File(encryptedPath+picture.getLocalfile().getName()));
            System.out.println("丢失加密图片");
            return decryptToTemporary(picture);

        }else{
            return null;
        }


    }
    public List<Picture> getPicturesListExceptPrivate() {
        List<Picture> result = new ArrayList<>();
        for (Picture picture : pictures.values()) {
            if (!picture.getTags().contains("私密")) {
                result.add(picture);
            }
        }
        return result;
    }
    public List<Picture> searchPictureByTagsAndText(Set<String> tags, String txt, int privateTag) {

        if (tags == null) {
            tags = new HashSet<>(); // 初始化 tags 集合
        }

        // 根据 privateTag 的值决定是否添加或移除“私密”标签
        if (privateTag == 0) {
            tags.remove("私密");
        }

        // 去除 txt 中的回车，按空格拆分
        String[] keywords = txt.replaceAll("\n", " ") // 去除回车
                .trim() // 去除前后空格
                .split("\\s+"); // 按空格拆分（支持多个空格）

        if (privateTag == 0) {
            keywords = Arrays.stream(keywords)
                    .filter(keyword -> !"私密".equals(keyword))
                    .toArray(String[]::new);
        }

        List<Picture> result = new ArrayList<>();

        // 遍历所有 Picture
        for (Picture picture : pictures.values()) {

            if (!tags.isEmpty()) {
                if (picture.getTags() == null || !picture.getTags().containsAll(tags)) {
                    continue;
                }
            }
            // 检查 txt 是否匹配
            boolean isMatched = false;
            if (keywords.length > 0) { // 只有 keywords 不为空时才进行匹配
                for (String keyword : keywords) {
                    boolean keywordMatched = (picture.getArtist() != null && picture.getArtist().contains(keyword))
                            || (picture.getPid() != null && picture.getPid().contains(keyword))
                            || (picture.getTags() != null && picture.getTags().contains(keyword));
                    if (keywordMatched) {
                        isMatched = true;
                        break;
                    }
                }
            } else {
                isMatched = false;
            }

            if (isMatched) {
                if(privateTag==1){
                result.add(picture);
                }else{
                    if(!picture.getTags().contains("私密")){
                        result.add(picture);
                    }
                }

            }
        }

        // 按照 id 升序排序
        result.sort(Comparator.comparingInt(Picture::getId));
        return result;
    }

    public UserData getUserData() {
        return userData;
    }
    public void setUserData(UserData userData) {
        this.userData = userData;
    }

    public String getEncryptedPath() {
        return encryptedPath;
    }
    public String getDataPath() {
        return dataPath;
    }

    public String getDecryptedPath() {
        return decryptedPath;
    }

    public String getUseraddPicturePath() {
        return useraddPicturePath;
    }

    public String getDownloadPath() {
        return downloadPath;
    }

    public void setUseraddPicturePath(String useraddPicturePath) {
        this.useraddPicturePath = useraddPicturePath;
    }
    //网络编程
    public Boolean uploadPicture(Picture picture, String user) {
        Boolean result = false;
        try (Socket socket = new Socket(serverIP, serverPort);
             ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
             FileInputStream fis = new FileInputStream(picture.getLocalfile())) {

            // 发送上传指令和元数据
            oos.writeObject("UPLOAD");
            PictureInfo info = new PictureInfo(0, picture.getArtist(), picture.getPid(), user);
            oos.writeObject(info);

            // 发送文件数据
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                oos.write(buffer, 0, bytesRead);
            }
            oos.writeObject("UPLOAD_COMPLETE");
            oos.flush();

            System.out.println("上传结果: " + ois.readObject());
            result = true;
            return result;

        } catch (Exception e) {
            e.printStackTrace();
            return result;
        }
    }
    public List<PictureInfo> listPictures() {
        try (Socket socket = new Socket(serverIP, serverPort);
             ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())) {

            oos.writeObject("LIST");
            return (List<PictureInfo>) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public void downloadPicture(int networkSeq, String savePath) {
        try (Socket socket = new Socket(serverIP, serverPort);
             ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
             FileOutputStream fos = new FileOutputStream(savePath)) {

            // 发送下载请求
            oos.writeObject("DOWNLOAD");
            oos.writeInt(networkSeq);
            oos.flush();  // 确保请求被发送

            // 接收文件数据
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = ois.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }

            // 接收结束标志，判断是否下载成功
            String downloadStatus = (String) ois.readObject();
            System.out.println("下载结果: " + downloadStatus);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
