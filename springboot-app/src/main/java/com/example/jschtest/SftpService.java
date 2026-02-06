package com.example.jschtest;

import com.jcraft.jsch.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Properties;
import java.util.Vector;

@Service
public class SftpService {

    private static final Logger logger = LoggerFactory.getLogger(SftpService.class);

    public void testSftpConnection(String host, int port, String user, String password, String serverName) {
        logger.info("========================================================");
        logger.info("Attempting to connect to SFTP server: {}", serverName);
        logger.info("Host: {}, Port: {}, User: {}", host, port, user);
        logger.info("========================================================");

        ChannelSftp channelSftp = null;
        Session session = null;
        try {
            channelSftp = new SftpClientUtil().initConnect(host, user, password);
            session = channelSftp.getSession();
            logger.info("SFTP Channel connected for {}.", serverName);

            // Define remote path and list/download files without changing directory
            // /ftphome2/upload is bound to /mnt/data/ftphome2/upload via bind mount
            String remotePath = "/ftphome2/upload";

            @SuppressWarnings("unchecked")
            Vector<ChannelSftp.LsEntry> fileList = channelSftp.ls(remotePath);
            logger.info("Listing files in '{}' on {}:", remotePath, serverName);
            if (fileList.isEmpty()) {
                logger.info("  -> No files found.");
            } else {
                for (ChannelSftp.LsEntry entry : fileList) {
                    // Filter out directories and parent references from ls result
                    if (entry.getAttrs().isDir()) {
                        continue;
                    }
                    logger.info("  -> {} ({})", entry.getFilename(), entry.getAttrs().getSize() + " bytes");
                }
            }

            // --- BEGIN: Download files ---
            String localDir = "downloads/" + serverName;
            new java.io.File(localDir).mkdirs();

            logger.info("Attempting to download files to local '{}' directory:", localDir);
            for (ChannelSftp.LsEntry entry : fileList) {
                // Skip directories, links, and parent directory references
                if (entry.getAttrs().isDir() || entry.getAttrs().isLink() || entry.getFilename().equals(".")
                        || entry.getFilename().equals("..")) {
                    continue;
                }

                String remoteFilePath = remotePath + "/" + entry.getFilename();
                String localFilePath = localDir + "/" + entry.getFilename();
                try {
                    logger.info("  -> Downloading {}", remoteFilePath);
                    channelSftp.get(remoteFilePath, localFilePath);
                    logger.info("  -> Download of {} to {} successful.", remoteFilePath, localFilePath);
                } catch (SftpException getEx) {
                    logger.error("  -> Failed to download {}: {}", remoteFilePath, getEx.getMessage(), getEx);
                }
            }
            // --- END: Download files ---

            logger.info("SFTP connection test to {} was SUCCESSFUL.", serverName);

        } catch (JSchException e) {
            logger.error("JSchException during SFTP operation for {}: {}", serverName, e.getMessage(), e);
        } catch (SftpException e) {
            logger.error("SftpException during SFTP operation for {}: {}", serverName, e.getMessage(), e);
        } finally {
            if (channelSftp != null) {
                channelSftp.disconnect();
                logger.info("SFTP channel for {} disconnected.", serverName);
            }
            if (session != null) {
                session.disconnect();
                logger.info("Session for {} disconnected.", serverName);
            }
            logger.info("===================== Test for {} finished =====================\n", serverName);
        }
    }
}
