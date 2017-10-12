/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scoreboard;

import java.util.logging.Logger;

/**
 *
 * @author lovelinanand
 */
public class Instructions {
    private static final Logger LOGGER = Logger.getGlobal();
    private final String instruction;
    private String operation;
    private String Source1;
    private String Source2;
    private String Destination;
    public int lineCount;
    // 99 cannot be executed. 0 - ready, 1 - dispatched, 2 - issued, 3 - completed
    private int instStatus; 
    private int cyclesLeft;
    private int totalCycleRequired;
    public Instructions(String instruction, int count){
        this.instruction = instruction;
        this.lineCount = count;
        String[] parts = instruction.split("\\s+");
        this.operation = parts[0];
        this.instStatus = 0;
        switch(this.operation){
            case "DUMP":
                this.Source1 = parts[1];
                this.Source2 = parts[2];
                this.instStatus = 99;
                break;
            case "ISSUEWIDTH":
                this.Source1 = parts[1];
                this.instStatus = 99;
                break;
            case "CACHEMISS":
                this.Source1 = parts[1];
                this.instStatus = 99;
                break;
            case "ST":
                this.Source1 = parts[1];
                this.Source2 = parts[3];
                this.Destination = parts[2];
                break;
            default:
                this.Destination = parts[1];
                this.Source1 = parts[2];
                this.Source2 = parts[3];
                break;
        }
        LOGGER.info("Adding Instruction => "+ count +" = "+  instruction);
    }
    public String getStatusString(){
        String status;
        switch(this.instStatus){
            case 0:
                status="Waiting";
                break;
            case 1:
                status="Dispatched";
                break;
            case 2:
                if(this.totalCycleRequired == this.cyclesLeft ){
                    status="Issued";
                }else{
                    status="Executing";
                }
                break;
            case 3:
                status="Completed";
                break;
            default:
                status="-----";
                break;
        }
        return status;
    }
    public String getSource1(){
        if(this.Source1 != null){
            return this.Source1;
        }else{
            return null;
        }
    }
    public String getSource2(){
        if(this.Source2 != null){
            return this.Source2;
        }else{
            return null;
        }
    }
    public String getDestination(){
        if(this.Destination != null){
            return this.Destination;
        }else{
            return null;
        }
    }
    
    public Boolean canBeIssued(){
        if(this.instStatus == 0){
            return true;
        }else{
            return false;
        }
    }
    
    public Boolean canBeDispatched(){
        if(this.instStatus == 0){
            return true;
        }else{
            return false;
        }
    }
    
    public String getOperation(){
        return this.operation;
    }
    
    public Boolean isCacheMiss(){
        if(this.operation.equals("LDM")){
            return true;
        }else{
            return false;
        }
    }
    
    public void setIssued(int cycleLeft, int cacheMiss){
        this.instStatus = 2;
        int actualCyclesLeft;
        if(this.isCacheMiss()){
            actualCyclesLeft = cycleLeft + cacheMiss;
        }else{
            actualCyclesLeft = cycleLeft;
        }
        this.cyclesLeft = actualCyclesLeft;
        this.totalCycleRequired = actualCyclesLeft;
        System.out.println("Issue: " + this);
    }
    public void setDispatched(){
        this.instStatus = 1;
        System.out.println("Dispatch:" + this);
    }
    public int decrementCycle(){
        if(this.cyclesLeft > 0){
            this.cyclesLeft --;
        }else{
            LOGGER.fine("Cannot decrement cycle in instructions" + this.toString());
            System.exit(0);
        }
        return this.cyclesLeft;
    }
    public void setCompleted(){
        if(this.cyclesLeft == 0){
            this.instStatus = 3;
            System.out.println("complete: " + this);
        }else{
            LOGGER.warning(this + "Cannot set completed status , cycles still left " + this.cyclesLeft );
            System.exit(0);
        }
    }
    
    public boolean isCompleted(){
        if(this.instStatus > 2){
            return true;
        }else{
            return false;
        }
    }
    
    @Override
    public String toString(){
        return this.instruction;
    }
}
