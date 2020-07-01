package com.mrbarboza.glacier.dtos;

import java.io.File;
import java.nio.file.Path;

public class ArchiveDto {
	
	public ArchiveDto() {}
	
	private Path path;
	private String vaultName;
	private File archiveFile;
	private String accountId;
	private String archiveId;
	private String jobId;
	private String location;

	public String getJobId() {
		return jobId;
	}

	public void setJobId(String jobId) {
		this.jobId = jobId;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public Path getPath() {
		return path;
	}

	public void setPath(Path path) {
		this.path = path;
	}

	public String getVaultName() {
		return vaultName;
	}

	public void setVaultName(String valtName) {
		this.vaultName = valtName;
	}

	public File getArchiveFile() {
		return archiveFile;
	}

	public void setArchiveFile(File archiveFile) {
		this.archiveFile = archiveFile;
	}

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public String getArchiveId() {
		return archiveId;
	}

	public void setArchiveId(String archiveId) {
		this.archiveId = archiveId;
	}

}
