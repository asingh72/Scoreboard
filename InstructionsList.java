/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scoreboard;

import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.logging.Logger;

/**
 *
 * @author lovelinanand
 */
public class InstructionsList {
    private static final Logger LOGGER = Logger.getGlobal();
    private LinkedHashMap<Integer, Instructions> InstructionTable;
    public int instructionCount;
    public final String orderType;
    public InstructionsList(String orderType){
        this.InstructionTable = new LinkedHashMap<Integer, Instructions>();
        this.instructionCount = 0;
        this.orderType = orderType;
    }
    public Instructions add(String inst, Integer count){
        Instructions temp = new Instructions(inst, count);
        this.InstructionTable.put(count, temp);
        this.instructionCount ++;
        return temp;
    }
    public Instructions get(Integer lineNo){
        return this.InstructionTable.get(lineNo);
    }
    
    public Set<Integer> keyList(){
        return this.InstructionTable.keySet();
    }
    
    public Instructions next(){
        Instructions nextIns = null;
        for(Integer key : this.InstructionTable.keySet()){
            Instructions temp = this.InstructionTable.get(key);
            if(temp.canBeIssued()){
                nextIns = temp;
                break;
            }
        }
        return nextIns;
    }
    
    public Instructions nextDispatch(){
        Instructions nextIns = null;
        for(Integer key : this.InstructionTable.keySet()){
            Instructions temp = this.InstructionTable.get(key);
            if(temp.canBeDispatched()){
                nextIns = temp;
                break;
            }
        }
        return nextIns;
    }
    
    public boolean isCompleted(){
        boolean isCompleted = true;
        for(Integer key: this.InstructionTable.keySet()){
            Instructions temp = this.InstructionTable.get(key);
            if(!temp.isCompleted()){
               isCompleted = false;
               break;
            }
        }
        return isCompleted;
    }
    public void dump(){
        String leftAlignFormat = "| %-15s | %-10s |%n";
        System.out.println("Instruction List Status");
        System.out.format("+-----------------+------------+%n");   
        System.out.format("| Instruction     | Status     |%n");
        System.out.format("+-----------------+------------+%n");
        for(Integer key : this.InstructionTable.keySet()){
            Instructions temp = this.InstructionTable.get(key);
            System.out.format(leftAlignFormat, temp, temp.getStatusString());
        }
        System.out.format("+-----------------+------------+%n");
    }
    public int getCacheMiss(){
        int cacheMiss = 0;
         for(Integer key: this.InstructionTable.keySet()){
            Instructions temp = this.InstructionTable.get(key);
            if("CACHEMISS".equals(temp.getOperation())){
                cacheMiss = Integer.parseInt(temp.getSource1());
            }
        }
        return cacheMiss;
    }
    
    public int getIssueWidth(){
        int issueWidth = 1;
        for(Integer key: this.InstructionTable.keySet()){
            Instructions temp = this.InstructionTable.get(key);
            if("ISSUEWIDTH".equals(temp.getOperation())){
                issueWidth = Integer.parseInt(temp.getSource1());
            }
        }
        return issueWidth;
    }
    
    public Integer[] getDump(){
        Integer[] dumpCycles = { -1, -1};
        for(Integer key: this.InstructionTable.keySet()){
            Instructions temp = this.InstructionTable.get(key);
            if("DUMP".equals(temp.getOperation())){
                dumpCycles[0] = Integer.parseInt(temp.getSource1());
                dumpCycles[1] = Integer.parseInt(temp.getSource2());
            }
        }
        return dumpCycles;
    }
}
