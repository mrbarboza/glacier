package com.mrbarboza.glacier.archive;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.glacier.GlacierClient;
import software.amazon.awssdk.services.glacier.model.GlacierException;
import software.amazon.awssdk.services.glacier.model.InitiateJobRequest;
import software.amazon.awssdk.services.glacier.model.InitiateJobResponse;
import software.amazon.awssdk.services.glacier.model.JobParameters;

public class DownloadArchive {

    public static void main(String[] args) {

        final String USAGE = "\n" +
            "DownloadArchive - start a job to retrieve vault inventory\n\n" +
            "Usage: DownloadArchive <vaultName> <accountId>\n\n" +
            "Where:\n" +
            "  vaultName - the name of the vault\n\n" +
            "  accountId - the account ID\n\n";

        if (args.length < 2) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String vaultName = args[0];
        String accountId = args[1];

        // Create a GlacierClient Object
        GlacierClient glacierClient = GlacierClient.builder()
            .region(Region.US_EAST_1)
            .build();

        createJob(glacierClient, vaultName, accountId);
    }

    public static void createJob(GlacierClient glacierClient, String vaultName, String accountId) {

        try {

            JobParameters jobParameters = JobParameters.builder()
                .type("inventory-retrieve")
                .build();

            InitiateJobRequest initiateJobRequest = InitiateJobRequest.builder()
                .jobParameters(jobParameters)
                .accountId(accountId)
                .vaultName(vaultName)
                .build();

            InitiateJobResponse initiateJobResponse = glacierClient.initiateJob(initiateJobRequest);

            System.out.println("The job ID is: " + initiateJobResponse.jobId());
            System.out.println("The relative URI path of the job is: " + initiateJobResponse.location());
        } catch (GlacierException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }

        System.out.println("Done");
    }
}
