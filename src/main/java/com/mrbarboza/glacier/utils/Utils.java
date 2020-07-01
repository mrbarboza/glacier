package com.mrbarboza.glacier.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Utils {
	
	static final int ONE_MB = 1024 * 1024;
	
	public static String computeSHA256(File archiveFile) {

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
