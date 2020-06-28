package com.mrbarboza.glacier.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.mrbarboza.glacier.dtos.ArchiveDto;
import com.mrbarboza.glacier.dtos.VaultDto;
import com.mrbarboza.glacier.response.Response;
import com.mrbarboza.glacier.utils.Utils;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.glacier.GlacierClient;
import software.amazon.awssdk.services.glacier.model.*;

@RestController
@RequestMapping("/api")
public class GlacierController {
	
	// Create a GlacierClient Object
    private GlacierClient glacierClient = GlacierClient.builder()
        .region(Region.US_EAST_1)
        .build();
	
    @PostMapping(value = "/createVault")
	public ResponseEntity<Response<VaultDto>> createVault(@Valid @RequestBody VaultDto vaultDto,
			BindingResult result) {
		
		Response<VaultDto> response = new Response<VaultDto>();
		
		try {
			
			CreateVaultRequest vaultRequest = CreateVaultRequest.builder()
	                .vaultName(vaultDto.getName())
	                .build();

			CreateVaultResponse vaultResponse = glacierClient.createVault(vaultRequest);
			
			vaultDto.setLocation(vaultResponse.location());
	        
	        response.setData(vaultDto);
			
		} catch (GlacierException e) {
			response.getErrors().add(e.awsErrorDetails().errorMessage());
			return ResponseEntity.badRequest().body(response);
		}
		
		return ResponseEntity.ok(response);
		
	}
	
    @PostMapping(value = "/deleteVault")
	public ResponseEntity<Response<VaultDto>> deleteVault(@Valid @RequestBody VaultDto vaultDto,
			BindingResult result) {
		
    	Response<VaultDto> response = new Response<VaultDto>();
		
		try {
			
			DeleteVaultRequest vaultRequest = DeleteVaultRequest.builder()
	                .vaultName(vaultDto.getName())
	                .build();

			DeleteVaultResponse vaultResponse = glacierClient.deleteVault(vaultRequest);
	        
	        response.setData(vaultDto);
			
		} catch (GlacierException e) {
			response.getErrors().add(e.awsErrorDetails().errorMessage());
			return ResponseEntity.badRequest().body(response);
		}
		
		return ResponseEntity.ok(response);
		
	}
	
    @GetMapping(value = "/describeVault")
	public ResponseEntity<Response<VaultDto>> describeVault(@Valid @RequestBody VaultDto vaultDto,
			BindingResult result) {
		
    	Response<VaultDto> response = new Response<VaultDto>();
		
		try {
			
			DescribeVaultRequest vaultRequest = DescribeVaultRequest.builder()
	                .vaultName(vaultDto.getName())
	                .build();
			
			DescribeVaultResponse vaultResponse = glacierClient.describeVault(vaultRequest);
			
			vaultDto.setCreationDate(vaultResponse.creationDate());
			vaultDto.setLastInventoryDate(vaultResponse.lastInventoryDate());
			vaultDto.setNumberOfArchives(vaultResponse.numberOfArchives());
			vaultDto.setSizeInBytes(vaultResponse.sizeInBytes());
			vaultDto.setVaultARN(vaultResponse.vaultARN());
			vaultDto.setVaultName(vaultResponse.vaultName());
	        
	        response.setData(vaultDto);
			
		} catch (GlacierException e) {
			response.getErrors().add(e.awsErrorDetails().errorMessage());
			return ResponseEntity.badRequest().body(response);
		}
		
		return ResponseEntity.ok(response);
		
	}
	
    @GetMapping(value = "/listVault")
	public ResponseEntity<Response<List<VaultDto>>> listVault() {
		
		Response<List<VaultDto>> response = new Response<List<VaultDto>>();
		ListVaultsResponse vaultResponse;
		
		boolean listComplete = false;
        String newMarker = null;
        int totalVaults = 0;
        
        List<DescribeVaultOutput> vaultOutputList = new ArrayList<>();
        List<VaultDto> vaultDtoList = new ArrayList<>();
		
		try {
			
			while (!listComplete) {

                vaultResponse = null;

                if (newMarker != null) {

                    ListVaultsRequest request = ListVaultsRequest.builder()
                        .marker(newMarker)
                        .build();

                    vaultResponse = glacierClient.listVaults(request);
                } else {

                    ListVaultsRequest request = ListVaultsRequest.builder()
                        .build();

                    vaultResponse = glacierClient.listVaults(request);
                }

                vaultOutputList.addAll(vaultResponse.vaultList());

                // check for further results
                newMarker = vaultResponse.marker();

                if (newMarker ==  null) {
                    listComplete = true;
                }
            }
			
        	for (DescribeVaultOutput vault : vaultOutputList) {
        		VaultDto vaultDto = new VaultDto();
        		vaultDto.setName(vault.vaultName());
        		vaultDtoList.add(vaultDto);
			}
        	response.setData(vaultDtoList);
			
		} catch (GlacierException e) {
			response.getErrors().add(e.awsErrorDetails().errorMessage());
			return ResponseEntity.badRequest().body(response);
		}
		
		return ResponseEntity.ok(response);
	}
	
    @PostMapping(value = "/uploadArchive")
	public ResponseEntity<Response<ArchiveDto>> uploadArchive(@Valid @RequestBody ArchiveDto archiveDto,
			BindingResult result){
    	
    	Response<ArchiveDto> response = new Response<ArchiveDto>();
    	
    	// Get an SHA-256 tree hash value
        String checkValue = Utils.computeSHA256(archiveDto.getArchiveFile());
    	
    	try {
    		
    		UploadArchiveRequest archiveRequest = UploadArchiveRequest.builder()
                    .vaultName(archiveDto.getVaultName())
                    .checksum(checkValue)
                    .build();

            UploadArchiveResponse archiveResponse = glacierClient.uploadArchive(archiveRequest, archiveDto.getPath());
            
            archiveDto.setAccountId(archiveResponse.archiveId());
            
            response.setData(archiveDto);
			
    	}catch (GlacierException e) {
			response.getErrors().add(e.awsErrorDetails().errorMessage());
			return ResponseEntity.badRequest().body(response);
		}
		
		return ResponseEntity.ok(response);
		
	}
	
    @PostMapping(value = "/downloadArchive")
	public ResponseEntity<Response<ArchiveDto>> downloadArchive(@Valid @RequestBody ArchiveDto archiveDto,
			BindingResult result) {
    	
    	Response<ArchiveDto> response = new Response<ArchiveDto>();
    	
    	try {

            JobParameters jobParameters = JobParameters.builder()
                .type("inventory-retrieval")
                .build();

            InitiateJobRequest initiateJobRequest = InitiateJobRequest.builder()
                .jobParameters(jobParameters)
                .accountId(archiveDto.getAccountId())
                .vaultName(archiveDto.getVaultName())
                .build();

            InitiateJobResponse initiateJobResponse = glacierClient.initiateJob(initiateJobRequest);
            
            archiveDto.setJobId(initiateJobResponse.jobId());
            archiveDto.setLocation(initiateJobResponse.location());

            response.setData(archiveDto);
        } catch (GlacierException e) {
        	response.getErrors().add(e.awsErrorDetails().errorMessage());
			return ResponseEntity.badRequest().body(response);
        }
    	
    	return ResponseEntity.ok(response);
	}
	
    @PostMapping(value = "/deleteArchive")
	public ResponseEntity<Response<ArchiveDto>> deleteArchive(@Valid @RequestBody ArchiveDto archiveDto,
			BindingResult result) {
    	
    	Response<ArchiveDto> response = new Response<ArchiveDto>();
    	
    	try {
            DeleteArchiveRequest archiveRequest = DeleteArchiveRequest.builder()
                .vaultName(archiveDto.getVaultName())
                .accountId(archiveDto.getAccountId())
                .archiveId(archiveDto.getArchiveId())
                .build();

            DeleteArchiveResponse archiveResponse = glacierClient.deleteArchive(archiveRequest);
            System.out.println("The archive was deleted");
        } catch (GlacierException e) {
        	response.getErrors().add(e.awsErrorDetails().errorMessage());
			return ResponseEntity.badRequest().body(response);
        }
    	
    	return ResponseEntity.ok(response);
	}

}
