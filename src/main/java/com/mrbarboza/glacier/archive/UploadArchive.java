package com.mrbarboza.glacier.archive;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.glacier.GlacierClient;
import software.amazon.awssdk.services.glacier.model.GlacierException;
import software.amazon.awssdk.services.glacier.model.UploadArchiveRequest;
import software.amazon.awssdk.services.glacier.model.UploadArchiveResponse;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class UploadArchive {

    static final int ONE_MB = 1024 * 1024;

    public static void main(String[] args) {

        final String USAGE = "\n" +
            "UploadArchive - uploads an archive to Amazon Glacier vault\n\n" +
            "Usage: UploadArchive <strPath> <vaultName> \n\n" +
            "Where:\n" +
            "  strPath - the path to the archive to upload (i.e., C:\\AWS\\test.pdf)\n" +
            "  vaultName - the name of the vault\n\n";

        if (args.length < 2) {

            System.out.println(USAGE);
            System.exit(1);
        }

        String strPath = args[0];
        String vaultName = args[1];

        File archiveFile = new File(strPath);
        Path path = Paths.get(strPath);

        // Create a GlacierClient Object
        GlacierClient glacierClient = GlacierClient.builder()
            .region(Region.US_EAST_1)
            .build();

        String archiveId = uploadContent(glacierClient, path, vaultName, archiveFile);
        System.out.println("The ID of the archived item is " + archiveId);
    }

    public static String uploadContent(GlacierClient glacierClient, Path path, String vaultName, File archiveFile) {

        // Get an SHA-256 tree hash value
        String checkValue = computeSHA256(archiveFile);

        try {

            UploadArchiveRequest archiveRequest = UploadArchiveRequest.builder()
                .vaultName(vaultName)
                .checksum(checkValue)
                .build();

            UploadArchiveResponse archiveResponse = glacierClient.uploadArchive(archiveRequest, path);
            return archiveResponse.archiveId();
        } catch (GlacierException e) {

            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return "";
    }

    private static String computeSHA256(File archiveFile) {

        try {
            byte[] treeHash = computeSHA256TreeHash(archiveFile);
            System.out.printf("SHA-256 tree hash =%s\n", toHex(treeHash));
            return toHex(treeHash);
        } catch (IOException ioe) {
            System.err.format("Exception when reading from file %s: %s", archiveFile,
                ioe.getMessage());
            System.exit(-1);
        } catch (NoSuchAlgorithmException nsae) {
            System.err.format("Cannot locate MessageDigest algorithm for SHA-256: %s",
                nsae.getMessage());
            System.exit(-1);
        }

        return "";
    }

    private static byte[] computeSHA256TreeHash(File archiveFile) throws IOException, NoSuchAlgorithmException {

        byte[][] chunkSHA256Hashes = getChunkSHA256Hashes(archiveFile);
        return computeSHA256TreeHash(chunkSHA256Hashes);
    }

    private static byte[] computeSHA256TreeHash(byte[][] chunkSHA256Hashes) throws NoSuchAlgorithmException {

        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");

        byte[][] prevLvlHashes = chunkSHA256Hashes;

        while (prevLvlHashes.length > 1) {

            int len = prevLvlHashes.length / 2;
            if (prevLvlHashes.length % 2 != 0) {
                len++;
            }

            byte[][] currLvlHashes = new byte[len][];

            int j = 0;
            for (int i = 0; i < prevLvlHashes.length; i = i + 2, j++) {

                if (prevLvlHashes.length - i > 1) {

                    messageDigest.reset();
                    messageDigest.update(prevLvlHashes[i]);
                    messageDigest.update(prevLvlHashes[i + 1]);
                    currLvlHashes[j] = messageDigest.digest();
                } else {

                    currLvlHashes[j] = prevLvlHashes[i];
                }
            }

            prevLvlHashes = currLvlHashes;
        }

        return  prevLvlHashes[0];
    }

    private static byte[][] getChunkSHA256Hashes(File archiveFile) throws IOException, NoSuchAlgorithmException {

        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");

        long numChunks = archiveFile.length() / ONE_MB;

        if (archiveFile.length() % ONE_MB > 0) {
            numChunks++;
        }

        if (numChunks == 0) {
            return new byte[][] { messageDigest.digest() };
        }

        byte[][] chunkSHA256Hashes = new byte[(int) numChunks][];
        FileInputStream fileInputStream = null;

        try {
            fileInputStream = new FileInputStream(archiveFile);
            byte[] buffer = new byte[ONE_MB];

            int bytesRead;
            int idx = 0;

            while ((bytesRead = fileInputStream.read(buffer, 0, ONE_MB)) > 0) {

                messageDigest.reset();
                messageDigest.update(buffer, 0, bytesRead);
                chunkSHA256Hashes[idx++] = messageDigest.digest();
            }

            return chunkSHA256Hashes;
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException ioe) {
                    System.err.printf("Exception while closing %s.\n%s.", archiveFile.getName(), ioe.getMessage());
                }
            }
        }
    }

    private static String toHex(byte[] data) {

        StringBuilder stringBuilder = new StringBuilder(data.length * 2);

        for (byte item : data) {

            String hex = Integer.toHexString(item & 0xFF);

            if (hex.length() == 1) {
                stringBuilder.append("0");
            }
            stringBuilder.append(hex);
        }
        return stringBuilder.toString().toLowerCase();
    }
}
