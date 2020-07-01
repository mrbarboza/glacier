package com.mrbarboza.glacier.vault;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.glacier.GlacierClient;
import software.amazon.awssdk.services.glacier.model.DeleteVaultRequest;
import software.amazon.awssdk.services.glacier.model.DeleteVaultResponse;
import software.amazon.awssdk.services.glacier.model.GlacierException;

public class DeleteVault {

    public static void main(String[] args) {

        final String USAGE = "\n" +
            "DeleteVault - deletes an Amazon Glacier vault\n\n" +
            "Usage: DeleteVault <vaultName>\n\n" +
            "Where:\n" +
            "  vaultName - the name of the vault to delete\n\n";

        if (args.length < 1) {
            System.out.println(USAGE);
            System.exit(1);
        }
        String vaultName = args[0];

        // Create a GlacierClient object
        GlacierClient glacierClient = GlacierClient.builder()
            .region(Region.US_EAST_1)
            .build();

        deleteGlacierVault(glacierClient, vaultName);
    }

    public static void deleteGlacierVault(GlacierClient glacierClient, String vaultName) {

        try {
            DeleteVaultRequest vaultRequest = DeleteVaultRequest.builder()
                .vaultName(vaultName)
                .build();

            DeleteVaultResponse vaultResponse = glacierClient.deleteVault(vaultRequest);
            System.out.println("The vault was deleted!");
        } catch (GlacierException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
}
