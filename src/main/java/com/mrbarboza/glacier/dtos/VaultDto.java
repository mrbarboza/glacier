package com.mrbarboza.glacier.dtos;


public class VaultDto {
	
	public VaultDto() {}
	
	private String name;
	private String location;
	
	private String creationDate;
	private String lastInventoryDate;
	private Long numberOfArchives;
	private Long sizeInBytes;
	private String vaultARN;
	private String vaultName;

	public String getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(String creationDate) {
		this.creationDate = creationDate;
	}

	public String getLastInventoryDate() {
		return lastInventoryDate;
	}

	public void setLastInventoryDate(String lastInventoryDate) {
		this.lastInventoryDate = lastInventoryDate;
	}

	public Long getNumberOfArchives() {
		return numberOfArchives;
	}

	public void setNumberOfArchives(Long numberOfArchives) {
		this.numberOfArchives = numberOfArchives;
	}

	public Long getSizeInBytes() {
		return sizeInBytes;
	}

	public void setSizeInBytes(Long sizeInBytes) {
		this.sizeInBytes = sizeInBytes;
	}

	public String getVaultARN() {
		return vaultARN;
	}

	public void setVaultARN(String vaultARN) {
		this.vaultARN = vaultARN;
	}

	public String getVaultName() {
		return vaultName;
	}

	public void setVaultName(String vaultName) {
		this.vaultName = vaultName;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
