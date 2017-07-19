package com.applariat.plugin.eclipse.handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ArtifactLocationData {
	String owner;
	List<String> repositoryList;
	HashMap<String, List<String>> branchList;  //<key=repositoryName, value=list of available branches>
	public ArtifactLocationData() {
		super();
		// TODO Auto-generated constructor stub
	}
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
	public List<String> getRepositoryList() {
		return repositoryList;
	}
	public void setRepositoryList(List<String> repositoryList) {
		this.repositoryList = repositoryList;
	}
	public void addRepository(String repository) {
		this.repositoryList.add(repository);
	}
	public HashMap<String, List<String>> getBranchList() {
		return branchList;
	}
	public void setBranchList(HashMap<String, List<String>> branchList) {
		this.branchList = branchList;
	}
	public void addBranch(String repo, String branch) {
		List<String> bList = this.branchList.get(repo);
		if (bList==null) {
			bList = new ArrayList<String>();
			this.branchList.put(repo, bList);
		}
		bList.add(branch);
	}
	public List<String> getBranchList(String repo) {
		return this.branchList.get(repo);
	}
}
