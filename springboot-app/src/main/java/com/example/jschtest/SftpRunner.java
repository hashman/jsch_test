package com.example.jschtest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class SftpRunner implements CommandLineRunner {

    private final SftpService sftpService;

    // RHEL 7 properties
    @Value("${sftp.rhel7.host}")
    private String rhel7Host;
    @Value("${sftp.rhel7.port}")
    private int rhel7Port;
    @Value("${sftp.rhel7.user}")
    private String rhel7User;
    @Value("${sftp.rhel7.password}")
    private String rhel7Password;

    // RHEL 8 properties
    @Value("${sftp.rhel8.host}")
    private String rhel8Host;
    @Value("${sftp.rhel8.port}")
    private int rhel8Port;
    @Value("${sftp.rhel8.user}")
    private String rhel8User;
    @Value("${sftp.rhel8.password}")
    private String rhel8Password;

    public SftpRunner(SftpService sftpService) {
        this.sftpService = sftpService;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Starting SFTP connection tests...");

        // Test connection to RHEL 7 server
        sftpService.testSftpConnection(rhel7Host, rhel7Port, rhel7User, rhel7Password, "RHEL7");

        // Test connection to RHEL 8 server
        sftpService.testSftpConnection(rhel8Host, rhel8Port, rhel8User, rhel8Password, "RHEL8");

        System.out.println("SFTP connection tests finished. Check logs for details.");
    }
}
