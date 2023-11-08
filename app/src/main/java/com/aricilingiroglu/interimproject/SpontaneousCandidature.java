package com.aricilingiroglu.interimproject;

import java.util.List;

public class SpontaneousCandidature extends Candidature{
    private List<String> targetJobs;
    private List<String> targetEmployers;
    private String desiredPeriod;


    public void setTargetJobs(List<String> targetJobs) {
        this.targetJobs = targetJobs;
    }

    public void setTargetEmployers(List<String> targetEmployers) {
        this.targetEmployers = targetEmployers;
    }

    public void setDesiredPeriod(String desiredPeriod) {
        this.desiredPeriod = desiredPeriod;
    }
}
