/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package community;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import javax.sound.midi.SysexMessage;

/**
 *
 * @author Lucif3r
 */
public class analyzeData {
    
    public void analyzeEdge(){
        
        try{
            
            String edgeAdded = "";
            String edgeDeleted = "";
            
            for(int i=1; i <= 8 ; i++){
                
                if(i%10==0){
                    System.err.println("i : " + i);
                }
                
                String line;

                FileReader f = new FileReader("resources/oregon/data/node ("+i+").txt");
                BufferedReader in = new BufferedReader(f);

                line = in.readLine();
                line = in.readLine();
                line = in.readLine();
                line = in.readLine();
                line = in.readLine();
                int num1 = 0;
                while(line != null){
                    num1++;
                    line = in.readLine();
                }                
                int[][] first = new int[num1][2];
                f = new FileReader("resources/oregon/data/node ("+i+").txt");
                in = new BufferedReader(f);
                line = in.readLine();
                line = in.readLine();
                line = in.readLine();
                line = in.readLine();
                line = in.readLine();
                int index = 0;
                while(line != null){                    
                    String[] items = line.split("	");
                    first[index][0] = Integer.parseInt(items[0]);
                    first[index][1] = Integer.parseInt(items[1]);
                    index++;
                    line = in.readLine();
                }
                //System.out.println("salam");
                
                f = new FileReader("resources/oregon/data/node ("+(i+1)+").txt");
                in = new BufferedReader(f);
                
                line = in.readLine();
                line = in.readLine();
                line = in.readLine();
                line = in.readLine();
                line = in.readLine();
                int num2 = 0;
                while(line != null){
                    num2++;
                    line = in.readLine();
                }
                int[][] second = new int[num2][2];
                f = new FileReader("resources/oregon/data/node ("+(i+1)+").txt");
                in = new BufferedReader(f);
                line = in.readLine();
                line = in.readLine();
                line = in.readLine();
                line = in.readLine();
                line = in.readLine();
                index = 0;
                while(line != null){
                    String[] items = line.split("	");
                    second[index][0] = Integer.parseInt(items[0]);
                    second[index][1] = Integer.parseInt(items[1]);
                    index++;
                    line = in.readLine();
                }
                
                //Deeleted edges
                int diff = 0;
                for(int j = 0; j < num1; j++){
                    int found = 0;
                    for(int k = 0 ; k < num2 ; k++){
                        if(first[j][0]==second[k][0] && first[j][1]==second[k][1]){
                            found=1;
                        }
                    }
                    if(found==0){
                        diff++;
                    }
                }
                //System.err.println("dif : " + diff);
                edgeDeleted += (diff + ",");
                //System.err.println(edgeDeleted);
                
                //Added edges
                int add = 0;
                for(int j = 0; j < num2; j++){
                    int found = 0;
                    for(int k = 0 ; k < num1 ; k++){
                        if(second[j][0]==first[k][0] && second[j][1]==first[k][1]){
                            found=1;
                        }
                    }
                    if(found==0){
                        add++;
                    }
                }
                //System.err.println("add : " + add);
                edgeAdded += (add + ",");
            }
            FileWriter f1;
            BufferedWriter out1;
            f1 = new FileWriter("resources/oregon/edges.txt");
            out1 = new BufferedWriter(f1);
            
            System.err.println("deleted : " + edgeDeleted);
            out1.append(edgeDeleted + "\n");
            System.err.println("added : " + edgeAdded);
            out1.append(edgeAdded + "\n");
            
            out1.close();
            
        }catch(Exception e){
            System.out.println(e.toString());
        }
        
    }
    
    public void analyzeNode(){
        
        try{
            
            String nodeChanges = "";
            
            for(int i=1; i <= 8 ; i++){
                
                if(i%10==0){
                    System.err.println("i : " + i);
                }
                
                String line;

                FileReader f = new FileReader("resources/oregon/data/node ("+i+").txt");
                BufferedReader in = new BufferedReader(f);

                line = in.readLine();
                line = in.readLine();
                line = in.readLine();
                line = in.readLine();
                line = in.readLine();
                ArrayList first = new ArrayList();
                while(line != null){
                    
                    String[] items = line.split("	");
                    int a = Integer.parseInt(items[0]);
                    int b = Integer.parseInt(items[1]);
                    if(!first.contains(a)){
                        first.add(a);
                    }
                    if(!first.contains(b)){
                        first.add(b);
                    }
                    line = in.readLine();
                }
            
                f = new FileReader("resources/oregon/data/node ("+(i+1)+").txt");
                in = new BufferedReader(f);
                
                line = in.readLine();
                line = in.readLine();
                line = in.readLine();
                line = in.readLine();
                line = in.readLine();
                ArrayList second = new ArrayList();
                while(line != null){
                    
                    String[] items = line.split("	");
                    int a = Integer.parseInt(items[0]);
                    int b = Integer.parseInt(items[1]);
                    if(!second.contains(a)){
                        second.add(a);
                    }
                    if(!second.contains(b)){
                        second.add(b);
                    }
                    line = in.readLine();
                }
                
                int count = 0;
                for(int j=0; j < first.size() ; j++){
                    int found = 0;
                    for(int k=0; k < second.size() ; k++){
                        int a = (Integer)first.get(j);
                        int b = (Integer)second.get(k);
                        if(a==b){
                            found=1;
                        }
                    }
                    if(found==0){
                        count++;
                    }
                }
                
                for(int j=0; j < second.size() ; j++){
                    int found = 0;
                    for(int k=0; k < first.size() ; k++){
                        int a = (Integer)second.get(j);
                        int b = (Integer)first.get(k);
                        if(a==b){
                            found=1;
                        }
                    }
                    if(found==0){
                        count++;
                    }
                }
                nodeChanges += (count + ",");
                //System.err.println("count : " + count);
            
            }
            FileWriter f1;
            BufferedWriter out1;
            f1 = new FileWriter("resources/oregon/nodes.txt");
            out1 = new BufferedWriter(f1);
            out1.append(nodeChanges + "\n");
            out1.close();
            
        }catch(Exception e){
            System.err.println(e.toString());
        }
    }
    
    public void renameFiles(){
        
        try{
        
            for(int i=1; i<=1 ; i++){
                
                String line;

                FileReader f = new FileReader("resources/Ratings-timed.txt");
                BufferedReader in = new BufferedReader(f);
                
                FileWriter f1;
                BufferedWriter out1;
                f1 = new FileWriter("resources/Ratings-timed2.txt");
                out1 = new BufferedWriter(f1);
                           
                line = in.readLine();
                int j = 0;
                while(line != null && j < 100){
                    out1.append(line + "\n");
                    line = in.readLine();
                    j++;
                }
                
                out1.close();
            }
        
        }catch(Exception e){
            System.err.println(e.toString());
        }
        
    }
    
    public void convertArxiv(){
        
        try{

            
            FileWriter f1 = new FileWriter("resources/arxiv/arxiv.txt");
            BufferedWriter out1 = new BufferedWriter(f1);
                    
            String line;

            FileReader f = new FileReader("resources/arxiv/hep-th-slacdates");
            BufferedReader in = new BufferedReader(f);

            int counter = 1;
            line = in.readLine();
            while(line != null){
                
                if(counter%100==0){
                    System.out.println("counter : " + counter);
                }
                                
                String[] items = line.split(" ");
                String node = items[0];
                String date = items[1];
                
                FileReader edges = new FileReader("resources/arxiv/hep-th-citations");
                BufferedReader iter = new BufferedReader(edges);
                
                String edgeLine = iter.readLine();
                while(edgeLine != null){
                    
                    String[] nodes = edgeLine.split(" ");
                    String node1 = nodes[0];
                    String node2 = nodes[1];
                    if(node1.equalsIgnoreCase(node)){
                        
                        out1.append(Integer.parseInt(node1) + " " + Integer.parseInt(node2) + " " + date + "\n");
                                                
                    }
                                        
                    edgeLine = iter.readLine();
                }
                iter.close();
                edges.close();
                line = in.readLine();
                counter++;
            }
                     
            out1.close();
            
        }catch(Exception e){
            System.out.println(e.toString());
        }
        
        
    }
    
    public void editAsFiles(){
        
        try{
            
            String line;

            for(int i=1; i <= 733 ; i++){
                
                FileWriter f1 = new FileWriter("resources/as/as-new/node ("+ i + ").txt");
                BufferedWriter out1 = new BufferedWriter(f1);

                FileReader f = new FileReader("resources/as/node ("+ i + ").txt");
                BufferedReader in = new BufferedReader(f);

                line = in.readLine();
                line = in.readLine();
                line = in.readLine();
                line = in.readLine();
                line = in.readLine();
                while(line != null){
                    //String newline = line.replaceAll("\t", " ");
                    out1.append(line + "\n");
                    line = in.readLine();
                }
                in.close();
                out1.flush();
                out1.close();
            }
        
        }catch(Exception e){
            System.out.println(e.toString());
        }
    }
    
    public void countNumCommunities(){
        
        try{
            
            String line;

            FileWriter f1 = new FileWriter("resources/oregon/iLCD/numcom.txt");
            BufferedWriter out1 = new BufferedWriter(f1);
            
            for(int i=1; i <= 9 ; i++){
               
                FileReader f = new FileReader("resources/oregon/iLCD/out"+ i);
                BufferedReader in = new BufferedReader(f);
                line = in.readLine();
                int count = 0;
                while(line != null){
                    count++;
                    line = in.readLine();
                }
                out1.append(count + ",");
                in.close();
                
            }
            out1.append("\n");
            out1.flush();
            out1.close();
            
        }catch(Exception e){
            System.out.println(e.toString());
        }
               
    }
}
