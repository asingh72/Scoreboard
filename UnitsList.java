/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scoreboard;

import java.util.Hashtable;
import java.util.logging.Logger;

/**
 *
 * @author lovelinanand
 */
public class UnitsList {
    private static final Logger LOGGER = Logger.getGlobal();
    private Hashtable<String, Unit> unitsTable;
    private final int cacheMiss;
    public UnitsList(int cacheMiss){
        this.cacheMiss = cacheMiss;
        this.unitsTable = new Hashtable<String, Unit>();
        this.createAlU();
        this.createMUL();
        this.createDiv();
        this.createLD();
    }
    public void addUnit(String unitName, Unit unit){
        this.unitsTable.put(unitName, unit);
    }
    private void createAlU(){
        Unit ALU = new Unit("ALU", "pipelined", 1,new String[] {"ADD", "SUB"});
        this.addUnit("ALU", ALU);
        
    }
    private void createDiv(){
        Unit DIV = new Unit("DIV", "unpipelined", 8,new String[] {"DIV"});
        this.addUnit("DIV", DIV);
    }
    private void createMUL(){
        Unit MUL = new Unit("MUL", "pipelined", 4,new String[] {"MUL"});
        this.addUnit("MUL", MUL);
    }
    private void createLD(){
        Unit LD = new Unit("LOAD", "pipelinedVariable", 2,new String[] {"LDH","LDM","ST"});
        LD.setCacheMissPenalty(this.cacheMiss);
        this.addUnit("LOAD", LD);
    }
    public Unit getUnitByOperation(String operation){
        Unit targetUnit = null;
        for(String key : this.unitsTable.keySet()){
            Unit tmpUnit = this.unitsTable.get(key);
            if(tmpUnit.doesSupportOpt(operation)){
                targetUnit = tmpUnit;
                break;
            }
        }
        return targetUnit;
    }
    public Unit getUnitByInstruction(Instructions inst){
        Unit targetUnit;
        String operation = inst.getOperation();
        targetUnit = this.getUnitByOperation(operation);
        return targetUnit;
    }
    public boolean canIssue(Instructions inst){
        boolean canIssue = false;
        Unit targetUnit = this.getUnitByInstruction(inst);
        if(targetUnit == null){
            System.out.println("Cannot find suitable functional unit for operation "+ inst);
            System.exit(1);
        }
        if(targetUnit.isFree()){
            canIssue = true;
        }
        return canIssue;
    }
    public void issue(Instructions inst){
        if(this.canIssue(inst)){
            Unit targetUnit = this.getUnitByInstruction(inst);
            if(targetUnit == null){
                LOGGER.warning(inst + " cannot find target instruction");
                System.exit(0);
            }
            targetUnit.issue(inst);
        }else{
            LOGGER.warning(inst + " Issuing not issuable instruction");
        }
    }
    public void advanceClock(RegisterList rTable){
        for(String key : this.unitsTable.keySet()){
            Unit targetUnit = this.unitsTable.get(key);
            targetUnit.advanceClock(rTable);
        }
    }
    
    public void dump(){
        String leftAlignFormat = "| %-7s | %-9s | %-61s |%n";
        System.out.println("Function Unit Status");
        System.out.format("+---------+-----------+---------------------------------------------------------------+%n");   
        System.out.format("| FU Name | Status    |                  Instruction(s)                               |%n");
        System.out.format("+---------+-----------+---------------------------------------------------------------+%n");
        for(String key : this.unitsTable.keySet()){
            Unit targetUnit = this.unitsTable.get(key);
            System.out.format(leftAlignFormat, targetUnit.name, targetUnit.getStatusString(), targetUnit.getProcessingInstructions());
        }
        System.out.format("+---------+-----------+---------------------------------------------------------------+%n");
    }
}
