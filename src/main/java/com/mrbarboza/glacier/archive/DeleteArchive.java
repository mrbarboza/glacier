package com.mrbarboza.glacier.archive;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.glacier.GlacierClient;
import software.amazon.awssdk.services.glacier.model.DeleteArchiveRequest;
import software.amazon.awssdk.services.glacier.model.GlacierException;

public class DeleteArchive {

    public static void main(String[] args) {

        final String USAGE = "\n" +
            "DeleteArchive - deletes an Amazon Glacier archive\n\n" +
            "Usage: DeleteArchive <vaultName> <accountId> <archiveId>\n\n" +
            "Where:\n" +
            "  vaultName - the name of the vault that contains the archive to delete\n\n" +
            "  accountId - the account ID\n\n" +
            "  archiveId - the archive ID\n\n";

        if (args.length < 3) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String vaultName = args[0];
        String accountId = args[1];
        String archiveId = args[2];

        // Create a GlacierClient Object
        GlacierClient glacierClient = GlacierClient.builder()
            .region(Region.US_EAST_1)
            .build();

        deleteGlacierArchive(glacierClient, vaultName, accountId, archiveId);
    }

    public static void deleteGlacierArchive(GlacierClient glacierClient, String vaultName, String accountId, String archiveId) {

        try {
            DeleteArchiveRequest archiveRequest = DeleteArchiveRequest.builder()
                .vaultName(vaultName)
                .accountId(accountId)
                .archiveId(archiveId)
                .build();

            glacierClient.deleteArchive(archiveRequest);
            System.out.println("The archive was deleted");
        } catch (GlacierException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
}
