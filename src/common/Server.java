package common;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Server {
    private static final int PORT = 9081;
    private static final String STORAGE_PATH = "server_storage/";
    private static AtomicInteger sequenceGenerator = new AtomicInteger(1);
    private static List<PictureInfo> pictureInfos = Collections.synchronizedList(new ArrayList<>());
    private static final String PICTURE_INFO_FILE = "pictureInfos.ser";
    private static final String SEQUENCE_FILE = "sequence.ser";

    public static void main(String[] args) throws IOException {
        loadPictureInfosFromFile();
        loadSequenceFromFile();
        Files.createDirectories(Paths.get(STORAGE_PATH));
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("服务器启动，监听端口：" + PORT);
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                savePictureInfosToFile();
                saveSequenceToFile();
            }));

            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(() -> handleClient(clientSocket)).start();
            }
        }
    }

    private static void handleClient(Socket socket) {
        try (ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
             ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream())) {

            InetAddress clientAddress = socket.getInetAddress();
            System.out.printf("[%s] 客户端连接 - %s%n", new Date(), clientAddress);

            String command = (String) ois.readObject();
            System.out.printf("[%s] 收到指令: %s%n", new Date(), command);

            switch (command) {
                case "UPLOAD":
                    handleUpload(ois, oos, clientAddress);
                    break;
                case "LIST":
                    handleList(oos);
                    break;
                case "DOWNLOAD":
                    handleDownload(ois, oos);
                    break;
            }

            System.out.printf("[%s] 指令处理完成: %s%n", new Date(), command);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void handleUpload(ObjectInputStream ois, ObjectOutputStream oos, InetAddress clientAddress)
            throws Exception {
        // 读取上传的文件
        PictureInfo info = (PictureInfo) ois.readObject();
        int seq = sequenceGenerator.getAndIncrement();

        // 保存文件
        Path filePath = Paths.get(STORAGE_PATH + seq + ".dat");
        try (FileOutputStream fos = new FileOutputStream(filePath.toFile())) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = ois.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
            // 检查是否接收到了上传完成的标志
            String uploadCompleteFlag = (String) ois.readObject();
            if ("UPLOAD_COMPLETE".equals(uploadCompleteFlag)) {
                // 上传成功，继续处理
                PictureInfo newInfo = new PictureInfo(seq, info.getArtist(), info.getPid(), info.getUser());
                pictureInfos.add(newInfo);
                oos.writeObject("上传成功，序号: " + seq);
            } else {
                oos.writeObject("上传失败: 没有接收到完整的文件数据");
            }
        } catch (IOException e) {
            e.printStackTrace();
            oos.writeObject("上传失败: " + e.getMessage());
        }
    }

    private static void handleList(ObjectOutputStream oos) throws Exception {
        oos.writeObject(new ArrayList<>(pictureInfos));
    }

    private static void handleDownload(ObjectInputStream ois, ObjectOutputStream oos) throws Exception {
        int seq = ois.readInt();  // 获取客户端请求的文件序号
        Path filePath = Paths.get(STORAGE_PATH + seq + ".dat");

        if (Files.exists(filePath)) {
            // 文件存在，准备读取并发送
            try (FileInputStream fis = new FileInputStream(filePath.toFile())) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    oos.write(buffer, 0, bytesRead);
                }
                oos.flush();  // 确保数据已发送
            }

            // 文件发送完毕后，通知客户端下载成功
            oos.writeObject("下载成功");
        } else {
            // 文件不存在时，发送失败消息
            oos.writeObject("文件不存在");
        }
    }

    private static void savePictureInfosToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(PICTURE_INFO_FILE))) {
            oos.writeObject(pictureInfos);
            System.out.println("图片信息已保存");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadPictureInfosFromFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(PICTURE_INFO_FILE))) {
            pictureInfos = (List<PictureInfo>) ois.readObject();
            System.out.println("图片信息已加载");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("未能加载图片信息，文件不存在或格式错误");
        }
    }

    private static void saveSequenceToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SEQUENCE_FILE))) {
            oos.writeInt(sequenceGenerator.get());
            System.out.println("序号生成器已保存");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadSequenceFromFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(SEQUENCE_FILE))) {
            sequenceGenerator.set(ois.readInt());
            System.out.println("序号生成器已加载");
        } catch (IOException e) {
            System.out.println("未能加载序号生成器，文件不存在或格式错误");
        }
    }
}