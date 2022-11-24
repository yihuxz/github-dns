package me.zjh.dns;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.file.FileWriter;
import lombok.extern.slf4j.Slf4j;
import me.zjh.dns.util.FileUtil;
import me.zjh.dns.model.HostDns;
import me.zjh.dns.util.HttpsUtil;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;

@Slf4j
public class DnsApp {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void main(String[] args) {
        HttpsUtil.trustEveryone();

        String githubHostPath = "hosts/github_domains2.txt";
        String githubUpdateKeyword = "# GitHub更新时间";

        List<HostDns> list = getDnsList(githubHostPath);
        parseDnsList(list);
        genDnsHosts(list, githubUpdateKeyword);
    }

    private static void parseDnsList(List<HostDns> list) {
        for (HostDns hostDns : list) {
            String ipaddressUrl = "https://www.ipaddress.com/site/" + hostDns.getHost();

            Document doc = null;
            try {
                doc = Jsoup.connect(ipaddressUrl).get();
            } catch (IOException e) {
                log.error("dns解析异常：", e);
                return;
            }

            Elements elementsByClass = doc.body().getElementsByClass("comma-separated");
            Element first = elementsByClass.first();
            String text = first.child(0).text();
            hostDns.setIp(text);

            log.info("抓取dns地址是 {} , 抓取地址：{} ", text, ipaddressUrl);
        }
    }

    private static void genDnsHosts(List<HostDns> list, String keyword) {
        // 读取README.md
        File file = FileUtil.getFile("README.md");
        String reader = FileUtil.getFullText(file);

        // 重写README.md
        FileUtil.rewriteByKeyword(file, reader, keyword);

        // 末尾添加最新的github ip地址
        FileUtil.appendString(keyword + " " + formatter.format(LocalDateTime.now()) + "\n", file);
        FileUtil.appendString("```" + "\n", file);
        OptionalInt max = list.stream().map(HostDns::getIp).mapToInt(String::length).max();
        list.forEach(val -> {
            FileUtil.appendString(val.getIp() + completionFormatter(val, max.orElse(0)) + val.getHost() + "\n", file);
        });
        FileUtil.appendString("```", file);
    }

    private static String completionFormatter(HostDns githubDns, int maxIpLength) {
        int length = githubDns.getIp().length();
        int completion = maxIpLength - length;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 15 + completion; i++) {
            sb.append(" ");
        }
        return sb.toString();
    }

    private static List<HostDns> getDnsList(String path) {
        List<String> hostList = FileUtil.getLineStrList(path);

        List<HostDns> list = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(hostList)) {
            for (String host : hostList) {
                list.add(new HostDns(host, ""));
            }
        }

        return list;
    }
}