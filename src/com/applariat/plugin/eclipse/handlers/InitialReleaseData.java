package com.applariat.plugin.eclipse.handlers;

// Written by Mazda Marvasti: AppLariat Corp.

public class InitialReleaseData {

	ReleaseData release;
	ArtifactData artifact;
	DeployLocationData deployLoc;
	private String newArtifactLocation;
	private String repositoryOwner;
	private String repositoryName;
    private String repositoryBranch;
    
    
	public InitialReleaseData() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ReleaseData getRelease() {
		return release;
	}

	public void setRelease(ReleaseData release) {
		this.release = release;
	}

	public ArtifactData getArtifact() {
		return artifact;
	}

	public void setArtifact(ArtifactData artifact) {
		this.artifact = artifact;
	}

	public String getNewArtifactLocation() {
		return newArtifactLocation;
	}

	public void setNewArtifactLocation(String newArtifactLocation) {
		this.newArtifactLocation = newArtifactLocation;
	}

	public String getRepositoryOwner() {
		return repositoryOwner;
	}

	public void setRepositoryOwner(String repositoryOwner) {
		this.repositoryOwner = repositoryOwner;
	}

	public String getRepositoryName() {
		return repositoryName;
	}

	public void setRepositoryName(String repositoryName) {
		this.repositoryName = repositoryName;
	}

	public String getRepositoryBranch() {
		return repositoryBranch;
	}

	public void setRepositoryBranch(String repositoryBranch) {
		this.repositoryBranch = repositoryBranch;
	}

	public DeployLocationData getDeployLoc() {
		return deployLoc;
	}

	public void setDeployLoc(DeployLocationData deployLoc) {
		this.deployLoc = deployLoc;
	}
	
}
