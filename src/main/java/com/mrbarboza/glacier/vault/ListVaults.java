package com.mrbarboza.glacier.vault;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.glacier.GlacierClient;
import software.amazon.awssdk.services.glacier.model.DescribeVaultOutput;
import software.amazon.awssdk.services.glacier.model.GlacierException;
import software.amazon.awssdk.services.glacier.model.ListVaultsRequest;
import software.amazon.awssdk.services.glacier.model.ListVaultsResponse;

import java.util.List;

public class ListVaults {

    public static void listAllVault(GlacierClient glacierClient) {

        boolean listComplete = false;
        String newMarker = null;
        int totalVaults = 0;
        System.out.println("Your Amazon Glacier vaults:");

        try {

            while (!listComplete) {

                ListVaultsResponse response = null;

                if (newMarker != null) {

                    ListVaultsRequest request = ListVaultsRequest.builder()
                        .marker(newMarker)
                        .build();

                    response = glacierClient.listVaults(request);
                } else {

                    ListVaultsRequest request = ListVaultsRequest.builder()
                        .build();

                    response = glacierClient.listVaults(request);
                }

                List<DescribeVaultOutput> vaultOutputList = response.vaultList();

                for (DescribeVaultOutput vault: vaultOutputList) {

                    totalVaults += 1;
                    System.out.println("* " + vault.vaultName());
                }

                // check for further results
                newMarker = response.marker();

                if (newMarker ==  null) {
                    listComplete = true;
                }
            }

            if (totalVaults == 0) {
                System.out.println("No vaults found.");
            }
        } catch (GlacierException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
}
