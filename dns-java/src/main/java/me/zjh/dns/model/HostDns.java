package me.zjh.dns.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HostDns {

    private String host;

    private String ip;

}