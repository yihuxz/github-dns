package me.zjh.dns.util;

import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.io.file.FileWriter;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.List;

public class FileUtil {

    public static File getFile(String fileRelativePath) {
        String projectPath = System.getProperty("user.dir"); // 用户的当前工作目录
        String fileFullPath = projectPath + File.separator + fileRelativePath;
        File file = new File(fileFullPath);
        return file;
    }

    public static String getFullText(File file) {
        FileReader fileReader = new FileReader(file);
        String fullText = fileReader.readString();

        return fullText;
    }

    public static String getFullText(String fileRelativePath) {
        File file = getFile(fileRelativePath);
        FileReader fileReader = new FileReader(file);
        String fullText = fileReader.readString();

        return fullText;
    }

    public static List<String> getLineStrList(String fileRelativePath) {
        File file = getFile(fileRelativePath);
        FileReader fileReader = new FileReader(file);
        List<String> lineStrList = fileReader.readLines();

        return lineStrList;
    }

    public static File appendString(String content, File file) throws IORuntimeException {
        return cn.hutool.core.io.FileUtil.appendUtf8String(content, file);
    }

    public static void rewriteByKeyword(File file, String content, String keyword) {
        int i = StringUtils.indexOf(content, keyword);
        String substring = StringUtils.substring(content, 0, i);
        FileWriter writer = new FileWriter(file);
        writer.write(substring);
    }
}
