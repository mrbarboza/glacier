package com.mrbarboza.glacier.vault;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.glacier.GlacierClient;
import software.amazon.awssdk.services.glacier.model.DeleteVaultRequest;
import software.amazon.awssdk.services.glacier.model.DeleteVaultResponse;
import software.amazon.awssdk.services.glacier.model.GlacierException;

public class DeleteVault {

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
