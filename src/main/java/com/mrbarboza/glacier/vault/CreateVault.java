package com.mrbarboza.glacier.vault;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.glacier.GlacierClient;
import software.amazon.awssdk.services.glacier.model.CreateVaultRequest;
import software.amazon.awssdk.services.glacier.model.CreateVaultResponse;
import software.amazon.awssdk.services.glacier.model.GlacierException;

public class CreateVault {

    public static void main(String[] args) {

        final String USAGE = "\n" +
            "CreateVault - create an Amazon Glacier vault\n\n" +
            "Usage: CreateVault <vaultName>\n\n" +
            "Where:\n" +
            "  vaultName - the name of the vault to create\n\n";

        if (args.length < 1) {
            System.out.println(USAGE);
            System.exit(1);
        }
        String vaultName = args[0];

        // Create a GlacierClient Object
        GlacierClient glacierClient = GlacierClient.builder()
            .region(Region.US_EAST_1)
            .build();

        createGlacierVault(glacierClient, vaultName);
    }

    public static void createGlacierVault(GlacierClient glacierClient, String vaultName) {

        try {
            CreateVaultRequest vaultRequest = CreateVaultRequest.builder()
                .vaultName(vaultName)
                .build();

            CreateVaultResponse vaultResponse = glacierClient.createVault(vaultRequest);
            System.out.println("The URI of the new vault is " + vaultResponse.location());
        } catch (GlacierException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
}
