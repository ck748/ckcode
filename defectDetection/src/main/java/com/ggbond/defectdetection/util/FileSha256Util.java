package com.ggbond.defectdetection.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 文件SHA-256哈希工具类
 * 用于计算文件或输入流的SHA-256摘要值
 */
public class FileSha256Util {

    /**
     * 计算文件的SHA-256
     * 核心逻辑：读取二进制流 -> 更新摘要 -> 转十六进制字符串
     *
     * @param file 要计算的文件
     * @return SHA-256 Hash值
     */
    public static String calculateFileSHA256(File file) {
        if (file == null || !file.exists()) {
            return null;
        }

        try (FileInputStream fis = new FileInputStream(file)) {
            return calculateStreamSHA256(fis);
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 计算输入流的SHA-256 (适用于Web上传的MultipartFile)
     */
    public static String calculateStreamSHA256(InputStream inputStream) throws NoSuchAlgorithmException, IOException {
        // 1. 获取 SHA-256 摘要算法对象
        MessageDigest digest = MessageDigest.getInstance("SHA-256");

        // 2. 定义缓冲区（比如 8KB），分块读取，防止大文件撑爆内存
        byte[] buffer = new byte[8192];
        int bytesRead;

        // 3. 循环读取二进制数据并更新 digest
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            digest.update(buffer, 0, bytesRead);
        }

        // 4. 完成哈希计算，得到二进制的 hash 数组
        byte[] hashBytes = digest.digest();

        // 5. 将二进制 hash 数组转换为 十六进制字符串
        return bytesToHex(hashBytes);
    }

    /**
     * 辅助方法：将字节数组转为十六进制字符串（带 0x 前缀）
     */
    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length + 2); // +2 是为了 "0x"
        hexString.append("0x"); // 添加 0x 前缀
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
