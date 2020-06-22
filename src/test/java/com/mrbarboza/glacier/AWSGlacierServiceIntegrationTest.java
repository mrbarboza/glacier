package com.mrbarboza.glacier;


import com.mrbarboza.glacier.archive.DeleteArchive;
import com.mrbarboza.glacier.archive.DownloadArchive;
import com.mrbarboza.glacier.archive.UploadArchive;
import com.mrbarboza.glacier.vault.CreateVault;
import com.mrbarboza.glacier.vault.DeleteVault;
import com.mrbarboza.glacier.vault.DescribeVault;
import com.mrbarboza.glacier.vault.ListVaults;
import org.junit.jupiter.api.*;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.glacier.GlacierClient;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AWSGlacierServiceIntegrationTest {

    private static GlacierClient glacierClient;
    private static String vaultName = "";
    private static String strPath = "";
    private static String downloadVault = "";
    private static String accountId = "";
    private static String archiveId = "";
    private static String emptyVault = "";

    @BeforeAll
    public static void setUp() throws IOException {

        glacierClient = GlacierClient.builder()
            .region(Region.US_EAST_1)
            .build();

        try (InputStream input = AWSGlacierServiceIntegrationTest.class.getClassLoader().getResourceAsStream("config.properties")) {

            Properties prop = new Properties();

            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }

            prop.load(input);

            vaultName = prop.getProperty("vaultName");
            strPath = prop.getProperty("strPath");
            downloadVault = prop.getProperty("downloadVault");
            accountId = prop.getProperty("accountId");
            emptyVault = prop.getProperty("emptyVault");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    @Order(1)
    public void whenInitializingAWSS3Service_thenNotNull() {
        assertNotNull(glacierClient);
        System.out.println("Test 1 passed");
    }

    @Test
    @Order(2)
    public void createVault() {
        CreateVault.createGlacierVault(glacierClient, vaultName);
    }

    @Test
    @Order(3)
    public void describeVault() {
        DescribeVault.describeGlacierVault(glacierClient, vaultName);
    }

    @Test
    @Order(4)
    public void listVaults() {
        ListVaults.listAllVault(glacierClient);
    }

    @Test
    @Order(5)
    public void uploadArchive() {
        File testFile = new File(strPath);
        Path path = Paths.get(strPath);
        archiveId = UploadArchive.uploadContent(glacierClient, path, vaultName, testFile);
        assertTrue(!archiveId.isEmpty());
    }

    @Test
    @Order(6)
    public void downloadArchive() {
        DownloadArchive.createJob(glacierClient, downloadVault, accountId);
    }

    @Test
    @Order(7)
    public void deleteArchive() {
        DeleteArchive.deleteGlacierArchive(glacierClient, vaultName, accountId, archiveId);
    }

    @Test
    @Order(8)
    public void deleteVault() {
        DeleteVault.deleteGlacierVault(glacierClient, emptyVault);
    }
}
