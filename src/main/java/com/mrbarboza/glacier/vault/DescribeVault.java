package com.mrbarboza.glacier.vault;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.glacier.GlacierClient;
import software.amazon.awssdk.services.glacier.model.DescribeVaultRequest;
import software.amazon.awssdk.services.glacier.model.DescribeVaultResponse;
import software.amazon.awssdk.services.glacier.model.GlacierException;

public class DescribeVault {

    public static void main(String[] args) {

        final String USAGE = "\n" +
            "DescribeVault - describes an Amazon Glacier vault\n\n" +
            "Usage: DescribeVault<vaultName>\n\n" +
            "Where:\n"+
            "  vaultName - the name of the vault\n\n";

        if (args.length < 1) {

            System.out.println(USAGE);
            System.exit(1);
        }

        String vaultName = args[0];

        // Create a GlacierClient Object
        GlacierClient glacierClient = GlacierClient.builder()
            .region(Region.US_EAST_1)
            .build();

        describeGlacierVault(glacierClient, vaultName);
    }

    public static void describeGlacierVault(GlacierClient glacierClient, String vaultName) {

        try {

            DescribeVaultRequest vaultRequest = DescribeVaultRequest.builder()
                .vaultName(vaultName)
                .build();

            DescribeVaultResponse vaultResponse = glacierClient.describeVault(vaultRequest);

            System.out.println("Describing the vault: " + vaultName);
            System.out.println(
                "CreationDate: " + vaultResponse.creationDate() +
                    "\nLastInventoryDate: " + vaultResponse.lastInventoryDate() +
                    "\nNumberOfArchives: " + vaultResponse.numberOfArchives() +
                    "\nSizeInBytes: " + vaultResponse.sizeInBytes() +
                    "\nvaultARN: " + vaultResponse.vaultARN() +
                    "\nvaultName: " + vaultResponse.vaultName()
            );
        } catch (GlacierException e) {

            System.out.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
}
