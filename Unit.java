/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scoreboard;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author lovelinanand
 */
public class Unit {
    private static final Logger LOGGER = Logger.getGlobal();
    private final String type;
    private final String[] supportedIns;
    public final String name;
    private int requiredCycle;
    private List<Instructions> processingInst;
    private int cacheMissPenalty;
    public Unit(String name, String Type, int latency, String[] supportedIns){
        this.type = Type;
        this.name = name;
        this.supportedIns = supportedIns;
        this.cacheMissPenalty = 0;
        this.setRequiredCycle(latency);
       LOGGER.info("Creating functional unit "+ name + " of type "+ Type +" with latency "+ latency);
    }
    public void setRequiredCycle(int reqCycle){
        this.requiredCycle = reqCycle;
        if(this.isPipelined()){
            this.processingInst = new ArrayList<Instructions>(reqCycle);
            for(int i = 0; i < reqCycle; i ++){
                this.processingInst.add(null);
            }
        }else{
            this.processingInst = new ArrayList<Instructions>(1);
            this.processingInst.add(null);
        }
    }
    public boolean isPipelined(){
        if(this.type == "pipelined" || this.type == "pipelinedVariable"){
            return true;
        }else{
            return false;
        }
    }
    public void setCacheMissPenalty(int penalty){
        this.cacheMissPenalty = penalty;
    }
    public boolean doesSupportOpt(String operation){
        boolean doesSupport = false;
        for(String supported : this.supportedIns){
            if(supported.equals(operation)){
                doesSupport = true;
                break;
            }
        }
        return doesSupport;
    }
    
    public boolean isFree(){
        boolean isfree = false;
        if(this.processingInst.get(0) == null){
            isfree = true;
        }
        return isfree;
    }

    public void issue(Instructions inst){
        if(this.isFree()){
                inst.setIssued(this.requiredCycle, this.cacheMissPenalty);
                this.addInstruction(inst);
        }else{
            LOGGER.warning(inst + " Cannot issue to busy functional unit");
            System.exit(0);
        }
    }
    public String getStatusString(){
        if(this.isFree()){
            return "Available";
        }else{
            return "Busy";
        }
    }
    public void addInstruction(Instructions instr){
       if(this.isFree()){
           this.processingInst.set(0, instr);
       }else{
           LOGGER.warning("Function unit is full, cannot add instruction"+instr);
           System.exit(0);
       }
    }

    public String getProcessingInstructions(){
        String targetString = "";
        for(Instructions inst : this.processingInst){
            String proc;
            if(inst != null){
                proc = inst.toString();
            }else{
                proc = "-------";
            }
            if(targetString.length() < 1){
                targetString = proc;
            }else{
                targetString += "   " + proc;
            }
        }
        return targetString;
    }
    
    private void advanceClockPipeline(RegisterList rTable){
        for(int i =this.requiredCycle - 1 ; i >= 0; i--){
            Instructions target = this.processingInst.get(i);
            if(target != null){
                target.decrementCycle();
                if(i != this.requiredCycle - 1){
                    if(this.processingInst.get(i+1) == null){
                        this.processingInst.set(i+1, target);
                    }else{
                        LOGGER.warning("Cannot shift instruction to next pipeline"+target);
                        System.exit(0);
                    }
                }else{
                    target.setCompleted();
                    rTable.setFree(target);
                }
                this.processingInst.set(i, null);
            }
        }
    }
    public void advanceClockUnPipeline(RegisterList rTable){
        Instructions target = this.processingInst.get(0);
        if(target != null){
            int cycleLeft = target.decrementCycle();
            if(cycleLeft == 0){
                target.setCompleted();
                rTable.setFree(target);
                this.processingInst.set(0, null);
            }
        }
    }
    
    public void advanceClockPipelineVariable(RegisterList rTable){
        for(int i =this.requiredCycle - 1 ; i >= 0; i--){
            Instructions target = this.processingInst.get(i);
            if(target != null){
                if(i == this.requiredCycle - 1){
                    if(target.isCacheMiss()){
                        if(target.decrementCycle() == 0){
                            this.processingInst.set(i, null);
                            target.setCompleted();
                            rTable.setFree(target);
                        }
                    }else{
                        target.decrementCycle();
                        this.processingInst.set(i, null);
                        target.setCompleted();
                        rTable.setFree(target);
                    }
                }else{
                    if(this.processingInst.get(i + 1) == null){
                        target.decrementCycle();
                        this.processingInst.set(i+1, target);
                        this.processingInst.set(i, null);
                    }else{
                        LOGGER.fine(target + " instruction stalled");
                    }
                }
            }
        }
    }
    
    public void advanceClock(RegisterList rTable){
        switch(this.type){
            case "pipelined":
                this.advanceClockPipeline(rTable);
                break;
            case "pipelinedVariable":
                this.advanceClockPipelineVariable(rTable);
                break;
            default:
                this.advanceClockUnPipeline(rTable);
                break;
        }
    }
}
