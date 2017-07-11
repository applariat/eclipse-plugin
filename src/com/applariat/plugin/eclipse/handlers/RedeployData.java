package com.applariat.plugin.eclipse.handlers;

// Written by Mazda Marvasti: AppLariat Corp.

import java.io.Serializable;

public class RedeployData implements Serializable{

	private static final long serialVersionUID = 1L;

	private DeployData deployData;
	private ArtifactData artifactData;
	private String repositoryOwner;
	private String repositoryName;
    private String repositoryBranch;
    
	public RedeployData() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public DeployData getDeployData() {
		return deployData;
	}

	public void setDeployData(DeployData deployData) {
		this.deployData = deployData;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
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
	
	public String getNewBuildHash() {
		long time = (new java.util.Date()).getTime();
		return Long.toHexString(time);
	}
	
	public ArtifactData getArtifactData() {
		return artifactData;
	}

	public void setArtifactData(ArtifactData artifactData) {
		this.artifactData = artifactData;
	}

	public static String getNewLocUrl(String location, String owner, String name, String branch) {
		if (location.equalsIgnoreCase("github")) {
			return "https://github.com/"+owner+"/"+name+"/archive/"+branch+".zip";
		} else if (location.equalsIgnoreCase("bitbucket")) {
			return "https://bitbucket.org/"+owner+"/"+name+"/branch/"+branch+".zip";
		} else { // default to GitHub
			return "https://github.com/"+owner+"/"+name+"/archive/"+branch+".zip";
		}
	}
}
