/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scoreboard;

import java.util.LinkedList;
/**
 *
 * @author lovelinanand
 */
public class InstructionQueue {
    public LinkedList<Instructions> queue;;
    private InstructionsList iTable;
    public InstructionQueue() {
        this.queue = new LinkedList<Instructions>();
    }
    public boolean canBeDispatched(Instructions inst){
        return true;
    }
    public void dispatch(Instructions inst){
        this.queue.add(inst);
        inst.setDispatched();
    }
    public boolean isAntiDependant(Instructions inst){
        boolean isAntiDependant = false;
        for(Instructions nextIns: this.queue){
            if(inst.lineCount <= nextIns.lineCount){
                continue;
            }
            String Destination = inst.getDestination();
            if(Destination.equals(nextIns.getSource1())  || Destination.equals(nextIns.getSource2())){
                isAntiDependant = true;
            }
        }
        return isAntiDependant;
    }
    public boolean removeIns(Instructions nextIns){
        return this.queue.remove(nextIns);
    }
}
