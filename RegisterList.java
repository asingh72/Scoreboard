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
public class RegisterList {
        private static final Logger LOGGER = Logger.getGlobal();
        private Hashtable<String, Integer> RegisterTable;  
        public RegisterList(){
            this.RegisterTable = new Hashtable<String, Integer>();
        }
        public void create(String RegisterName){
            try{
                Integer.parseInt(RegisterName);
            }catch(NumberFormatException E){
                LOGGER.info("Creating Register => "+RegisterName);
                this.RegisterTable.put(RegisterName, 0);
            }
        }
        public boolean isBusy(String registerName){
            boolean isBusy = false;
            if(registerName != null){
                if(this.RegisterTable.get(registerName) != null && this.RegisterTable.get(registerName) != 0){
                    isBusy = true;
                }
            }
            return isBusy;
        }
        public boolean isDependant(Instructions ins){
            boolean isDependant = false;
            String source1 = ins.getSource1();
            String source2 = ins.getSource2();
            String destination = ins.getDestination();
            if(this.isBusy(source1) || this.isBusy(source2) || this.isBusy(destination)){
                isDependant = true;
            }
            return isDependant;
        }
        public void setBusy(Instructions ins){
            String destination = ins.getDestination();
            if(this.isBusy((destination))){
               LOGGER.warning("Destination already busy" + ins);
               System.exit(0);
            }else{
                this.RegisterTable.put(destination, 1);
            }
        }
        
        public void setFree(Instructions ins){
            String destination = ins.getDestination();
            if(this.isBusy((destination))){
                this.RegisterTable.put(destination, 0);
            }else{
                LOGGER.warning("Cannot free a register which is already free" + ins);
                System.exit(0);
            }
        }
        
        public void dump(){
            System.out.println("Register Status");
            int length = this.RegisterTable.size() + 1;
            String dash = "+";
            String format = "|";
            for(int i = 0 ; i < length ; i ++){
                dash += "---------+";
                format += "%-8s |";
            }
            format +="%n";
            String[] FirstRow = new String[length];;
            String[] SecondRow = new String[length];;
            FirstRow[0] = "Status";
            SecondRow[0] = "Register";
            int start = 1;
            for( String register : this.RegisterTable.keySet()){
                FirstRow[start] = this.RegisterTable.get(register).toString();
                SecondRow[start] = register;
                start ++;
            }
            System.out.println(dash);
            System.out.format(format, FirstRow);
            System.out.println(dash);
            System.out.format(format, SecondRow);
            System.out.println(dash);
        }
}
