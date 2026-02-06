package com.example.jschtest;

import java.util.Properties;

import com.jcraft.jsch.*;

public class SftpClientUtil {
    public ChannelSftp initConnect(String host, String account, String password) throws JSchException {
        Session jschSession = initConnSchSession(host, account, password);
        ChannelSftp channelSftp = connectChannelSftp(jschSession);
        return channelSftp;
    }

    private ChannelSftp connectChannelSftp(Session jschSession) {
        try {
            Channel channel = jschSession.openChannel("sftp");
            channel.connect();
            return (ChannelSftp) channel;
        } catch (JSchException e) {
            throw new RuntimeException("Failed to connect SFTP channel: " + e.getMessage(), e);
        }
    }

    private Session initConnSchSession(String host, String account, String password) throws JSchException {
        Session newJshSession;
        JSch jsch = new JSch();
        int port = 22;
        newJshSession = jsch.getSession(account, host, port);
        newJshSession.setPassword(password);
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        int connectionTimeout = 30000; // 30 seconds
        int keepAliveInterval = 15; // 15 seconds
        int keepAliveCountMax = 3; // Max 3 keep-alive messages
        config.put("ConnectTimeout", String.valueOf(connectionTimeout));
        config.put("ServerAliveInterval", String.valueOf(keepAliveInterval));
        config.put("ServerAliveCountMax", String.valueOf(keepAliveCountMax));
        newJshSession.setConfig(config);
        newJshSession.connect(connectionTimeout);
        return newJshSession;
    }
}
