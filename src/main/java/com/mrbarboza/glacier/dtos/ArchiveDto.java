package com.mrbarboza.glacier.dtos;

import java.io.File;
import java.nio.file.Path;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

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

	@NotEmpty(message = "Path não pode ser vazio.")
	@Length(min = 3, max = 200, message = "Path deve conter entre 3 e 200 caracteres.")
	public Path getPath() {
		return path;
	}

	public void setPath(Path path) {
		this.path = path;
	}

	@NotEmpty(message = "VaultName não pode ser vazio.")
	@Length(min = 3, max = 200, message = "VaultName deve conter entre 3 e 200 caracteres.")
	public String getVaultName() {
		return vaultName;
	}

	public void setVaultName(String valtName) {
		this.vaultName = valtName;
	}

	@NotEmpty(message = "ArchiveFile não pode ser vazio.")
	@Length(min = 3, max = 200, message = "ArchiveFile deve conter entre 3 e 200 caracteres.")
	public File getArchiveFile() {
		return archiveFile;
	}

	public void setArchiveFile(File archiveFile) {
		this.archiveFile = archiveFile;
	}

	@NotEmpty(message = "AccountId não pode ser vazio.")
	@Length(min = 3, max = 200, message = "ArchiveFile deve conter entre 3 e 200 caracteres.")
	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	@NotEmpty(message = "AccountId não pode ser vazio.")
	@Length(min = 3, max = 200, message = "ArchiveFile deve conter entre 3 e 200 caracteres.")	
	public String getArchiveId() {
		return archiveId;
	}

	public void setArchiveId(String archiveId) {
		this.archiveId = archiveId;
	}

}
