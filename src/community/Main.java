//:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-
//...C   O   M   M   U   N   I   T   Y      D   E   T   E   C   T   I   O   N...
//:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-
//@Hamidreza Alvari, Alireza Hajibagheri, Gita Sukthankar
//University of Central Florida
//:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-
package community;
import java.util.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
public class Main {
//:::::::::::::::::::::::::::Global Variables:::::::::::::::::::::::::::::::::::
    static int m,n,iterations;
    static agent[] agents;
    static Vector communities,benchmark, allNodes;
    static Vector communitiesNoOverlap = new Vector();
//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    public static void main(String[] args) throws Exception{
        int SNAPSHOTS = 100;
        allNodes = new Vector();
        // Input File
        Vector edges = ReadEdges("resources/as/node ("+1+").txt");
        //int PreviousNumNodes = n;
        agents = new agent[n]; //Vertices
        communities = new Vector(1); //Final communities
        Initialize(0,edges);


        FileWriter f1;
        BufferedWriter out1;
        f1 = new FileWriter("resources/modularities.txt");
        out1 = new BufferedWriter(f1);
        
        for(int i=2;i<SNAPSHOTS; i++){
            System.out.println("\nThe underlying graph: a graph with "+n+" nodes and "+m+" edges.\n");
            /*for(int j=0;j<n;j++){
                System.out.print(((Integer)allNodes.elementAt(j) + 1)+"==> ");
                for(int k=0;k<agents[j].nexts.length;k++)
                    System.out.print(((Integer)allNodes.elementAt(agents[j].nexts[k]) + 1)+",");
                System.out.println();
            }*/
            Similarity();
            Game();
            System.out.println("Modularity of SNAPSHOT#"+(i-1)+": "+Graph_Modularity_calculate(communities,"Current"));
            out1.append(Graph_Modularity_calculate(communities,"Current")+",");

            Print_Communities("Result","resources/out-SNAPSHOT#"+(i-1)+".txt");
            edges = ReadEdges("resources/as/node ("+i+").txt");

            Initialize(0,edges);
            communities = new Vector();
            System.out.println("============================================================");
        }
        
        out1.append("\n");
        out1.flush();
        out1.close();
        f1.close();
    }
//:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-
//-------------|^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^|-------------
//:::::::::::::|F     U     N     C     T     I     O      N     S|:::::::::::::
//-------------|__________________________________________________|-------------
//:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-
    static void updateStatistics(int last){
        agent[] tempAgents = new agent[last];
        for(int i=0;i<last;i++)
            tempAgents[i] = agents[i];
        agents = new agent[n];
        for(int i=0;i<last;i++)
            agents[i] = tempAgents[i];

    }
//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    static void Initialize(int last,Vector edges){
        
        /////////////
        agents = new agent[n];
        ////////////
        
        for (int i=last;i<n;i++){
            Vector L = new Vector(1);
            L.addElement(-(i+1)); //At first each agent has its own label
            agents[i] = new agent(i+1,L);
            
        }

        for(int i=0;i<n;i++){
            agents[i].tempNexts.clear();
            agents[i].deg = 0;
            agents[i].In_deg = 0;
        }
        
        
        int[] temp_edges;
        int ind1,ind2;
        for(int i=0; i<edges.size(); i++){
            temp_edges = (int[])edges.elementAt(i);
            ind1 = found(allNodes,temp_edges[0]);
            ind2 = found(allNodes, temp_edges[1]);
            if(ind1 != -1 && ind2 != -1){
                agents[ind1].tempNexts.addElement(ind2);
                agents[ind1].deg++;
                agents[ind2].In_deg++;
            }
        }
        
        int[] nexts;
        for(int i=0;i<n;i++){
            nexts = new int[agents[i].tempNexts.size()];
           for(int j=0;j<agents[i].tempNexts.size();j++)
               nexts[j] = (Integer)agents[i].tempNexts.elementAt(j);
            agents[i].nexts = nexts;
        }

    }
//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    static void Similarity(){
        sim1();
        //sim2();
    }
//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
   static void sim1(){
          for(int i=0;i<n;i++){
            double[] similarity = new double[n];
            for(int j=0;j<n;j++){
                if(i != j){
                    double sim = Sim_calculate(agents[i] , agents[j]);
                    if(sim == 0){
                        //if(A[i][j] == 1)
                            similarity[j] = agents[i].deg * agents[j].deg * 1.0 / (4 * m);
                        //else
                            similarity[j] = -agents[i].deg * agents[j].deg * 1.0 / (4 * m);
                    }
                    if(sim > 0){
                        //if(A[i][j] == 1)
                        if ( find_index(agents[i].nexts, j) != -1)
                            similarity[j] = sim * (1 - agents[i].deg * agents[j].deg * 1.0 / (2 * m));
                        else
                            similarity[j] = sim / (n);
                    }
                }
            }
            agents[i].similarities = similarity;
        }

   }
//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
//    static void sim2(){
//        double[] mu = new double[n];
//          double[] sigma = new double[n];
//          for(int i=0;i<n;i++){
//              for(int j=0;j<n;j++)
//                mu[i] += A[i][j];
//            mu[i] /= n;
//              for(int j=0;j<n;j++)
//                sigma[i] += Math.pow(A[i][j] - mu[i],2);
//            sigma[i] /= n;
//            sigma[i] = Math.sqrt(sigma[i]);
//          }
//          for(int i=0;i<n;i++){
//              double[] similarity = new double[n];
//              for(int j=0;j<n;j++){
//                  for(int k=0;k<n;k++)
//                     similarity[j] += (A[i][k] - mu[i]) * (A[j][k] - mu[j]);
//                  similarity[j] /= (n * sigma[i] * sigma[j]);
//              }
//              agents[i].similarities = similarity;
//          }
//    }
//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    static int Sim_calculate(agent a1,agent a2){
        int[] a = a1.nexts;
        int[] b = a2.nexts;
        int similar=0;
        for(int i=0;i<a.length;i++)
            for(int j=0;j<b.length;j++)
                if(a[i] == b[j] && (a[i] != a2.num - 1) )
                    similar++;
        return similar;
    }
//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    static int Game(){
        System.out.println("Starting...");
        System.out.println("------------");
        int current_agent = 0;
        iterations = 0;
        do{
            iterations++;
            if(iterations % n == 0)
                System.out.println("Iteration "+iterations+"... "+communities.size()+" communities are detected.");
            current_agent = fair_random_agent_selection();
            //double t1 = System.nanoTime() * Math.pow(10, -9);
            //System.out.println("current: "+(current_agent + 1));
            //System.out.println("current: "+ ((Integer)allNodes.elementAt(current_agent)+1) );
            //System.out.println("Old Q: "+agents[current_agent].utility);
            Personal_Decision(current_agent);
            //double t2 = System.nanoTime() * Math.pow(10, -9);
            //agents[current_agent].active_time += t2 - t1;
            //System.out.println("This agent was active for "+agents[current_agent].active_time+" seconds");
            //System.out.println("Time visited so far: "+agents[current_agent].time_visited);
            //System.out.println("Improve: "+agents[current_agent].improve);
//            System.out.println("===============================================");
//            System.out.println("Personal Decision");
//            /*for(int i=0;i<communities.size();i++){
//                int[] temp = (int[])communities.elementAt(i);
//                System.out.print((i+1)+": ");
//                for(int j=0;j<temp.length;j++)
//                    System.out.print( ((Integer)allNodes.elementAt(temp[j]-1) + 1)+" ");
//            System.out.println();
//            }*/
//            System.out.println("===============================================");
//            //Joint_Decision();
//            System.out.println("===============================================");
//            System.out.println("Joint Decision");
//            for(int i=0;i<communities.size();i++){
//                int[] temp = (int[])communities.elementAt(i);
//                System.out.print((i+1)+": ");
//                for(int j=0;j<temp.length;j++)
//                    System.out.print(((Integer)allNodes.elementAt(temp[j]-1) + 1)+" ");
//            System.out.println();
//            }
//            System.out.println("===============================================");
        }while(iterations != 5 * n);
        //while(!converge() && iterations != 40 * n );
        return iterations;
    }
//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    static void Remove_Overlap(){
        agent a_current = null;
        int mode = 0;
        
        int delta=0;
        double Q=0.0;
        
        
        /*System.out.println("Before Overlap");
        for(int i=0;i<communities.size();i++){
            int[] temp = (int[])communities.elementAt(i);
            System.out.print((i+1)+": ");
            for(int j=0;j<temp.length;j++)
                System.out.print(((Integer)allNodes.elementAt(temp[j]-1) + 1)+" ");
            System.out.println();
        }*/
        
        for(int i=0;i<agents.length;i++){
            //System.out.print(agents[i].num + " ");
            
            double[] thisLabelUtils = new double[agents[i].L.size()];
            if(agents[i].L.size()>1){   
                for(int j=0;j<agents[i].L.size();j++){
                    //System.out.print(agents[i].L.elementAt(j));
                    for(int k=0;k<agents.length;k++){
                        if(k!=i && agents[k].L.contains(agents[i].L.elementAt(j))){
                            Q += agents[i].similarities[k];
                            thisLabelUtils[j] += Q;
                        }
                    }
                    Q=0.0;
                    thisLabelUtils[j] /= 2*m;
                }
                /*for(int k =0 ; k < thisLabelUtils.length ; k++){
                    System.out.println( ((Integer)allNodes.elementAt(i)+1) + " Q " + agents[i].L.elementAt(k) + " : " + thisLabelUtils[k]);
                }*/
                int keepIndex = maxIndex(thisLabelUtils);
                int keepLabel = (Integer)agents[i].L.elementAt(keepIndex);
                //System.out.println("maxind : " + keepIndex + " label is : " + keepLabel);
                agents[i].L.removeAllElements();
                agents[i].L.addElement(keepLabel);

                agents[i].mainLabel = keepLabel;
            }else{
                agents[i].mainLabel = Integer.parseInt(agents[i].L.elementAt(0).toString());
            }
        }
        
        
        
        
        int maxNumCom = -1;
        for(int i=0;i<agents.length;i++){
            if(agents[i].mainLabel > maxNumCom){
                maxNumCom = agents[i].mainLabel;
            }
        }
        if (maxNumCom >= 1){
            communitiesNoOverlap = new Vector(maxNumCom);
            //System.out.println("Max         " + maxNumCom);
            for(int i=0;i<maxNumCom;i++){
                communitiesNoOverlap.add(new Vector());
            }
            for(int i=0;i<agents.length;i++){

                if(agents[i].mainLabel>=0){
                    int ind = agents[i].mainLabel-1 ;
                    //System.out.println("indexxxxxxxxxxxxxxxxx : " + inde);
                   ((Vector)communitiesNoOverlap.elementAt(ind)).addElement(agents[i].num);
                }
            }

            /*System.out.println("After Overlap");
            for(int i=0;i<communitiesNoOverlap.size();i++){
                Vector temp = (Vector)communitiesNoOverlap.elementAt(i);
                System.out.print((i+1)+": ");
                for(int j=0;j<temp.size();j++)
                    //System.out.print(temp.elementAt(j)+" ");
                    System.out.print(((Integer)allNodes.elementAt((Integer)temp.elementAt(j)-1) + 1)+" ");
            System.out.println();
            }*/
        }
    }
//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    static void Print_Results(){
        if(iterations % n != 0)
           System.out.println("Iteration "+iterations+"... "+communities.size()+" communities are detected.");
        System.out.println("-------------------------------------------------Final Statuses---------------------------------------------------------------------------------------------");
        System.out.println("Num     Labels        Visited        Join         Leave        Switch          Invited         Rejected        Evicted       No_Op            Final Q");
        System.out.println("------------------------------------------------------------------------------------------------------------------------------------------------------------");
        for(int i=0;i<agents.length;i++){
            System.out.print(( (Integer)allNodes.elementAt( agents[i].num - 1) + 1) +":      {");
            for(int j=0;j<agents[i].L.size();j++){
                System.out.print(agents[i].L.elementAt(j));
                if(j != agents[i].L.size() - 1)
                    System.out.print(",");
            }
            System.out.print("}               ");
            System.out.println(agents[i].time_visited+"             "+agents[i].joined+"            "+agents[i].left+"            "+agents[i].switched+"                "+agents[i].invited+"                "+agents[i].rejected+"              "+agents[i].evicted+"            "+agents[i].noOp+"          "+agents[i].utility);
        }
        int Joins = 0,Leaves = 0,Switches = 0,noOps = 0,Invitations = 0,Rejects = 0,Evictions = 0,overlapped = 0,max_membership=0;
            for(int i=0;i<n;i++){
                Joins += agents[i].joined;
                Leaves += agents[i].left;
                Switches += agents[i].switched;
                noOps += agents[i].noOp;
                Invitations += agents[i].invited;
                Rejects += agents[i].rejected;
                Evictions += agents[i].evicted;
                if( agents[i].L.size() > 1)
                    overlapped++;
                if(agents[i].L.size() > agents[max_membership].L.size())
                    max_membership = i;
            }
        int s_min = 0,s_max = 0;
        if( !communities.isEmpty() ){
        s_min = ((int[])communities.elementAt(0)).length;
        s_max = ((int[])communities.elementAt(0)).length; //min & max size of all commnities
        for(int i=1;i<communities.size();i++){
            int tmp = ((int[])communities.elementAt(i)).length;
            if(s_min > tmp)
                s_min = tmp;
            if(s_max < tmp)
                s_max = tmp;
        }
        }
        int Total_Ops = Joins + Leaves + Switches + Invitations + Rejects + Evictions;
        System.out.println("------------------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out.println("\n***After "+iterations+" iterations on this graph with "+n+" nodes and "+m+" edges("+(2*m)+").\n");
        System.out.println("All "+Total_Ops+" operations are as below:");
        System.out.println("Joins: "+Joins);
        System.out.println("Leaves: "+Leaves);
        System.out.println("Switches: "+Switches);
        System.out.println("Invitations: "+Invitations);
        System.out.println("Rejects: "+Rejects);
        System.out.println("Evictions: "+Evictions);
        System.out.println("-----------------------");
        System.out.println("No operations: "+noOps);
        System.out.println("---------------------------------------------------------");
        System.out.println("Calculated fraction of overlapped nodes: "+overlapped*1.0/n+" --> "+overlapped);
        System.out.println("Maximum degree of membership: "+agents[max_membership].L.size());
        System.out.println("Minimum size of communities: "+s_min);
        System.out.println("Maximum size of communities: "+s_max);
        System.out.println("---------------------------------------------------------");
        //System.out.println("Modularity of benchmark: "+Graph_Modularity_calculate(benchmark,"Original"));
        System.out.println("Modularity of final division of graph: "+Graph_Modularity_calculate(communities,"Current"));
        System.out.println("---------------------------------------------------------");
    }
//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    static int maxIndex(double[] v){
        int ind = -1;
        double max = -1;
        for(int i=0;i<v.length;i++){
            if(v[i]>max){
                ind = i;
                max = v[i];
            }
        }
        return ind;
    }
//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    static Vector ReadEdges(String path)throws Exception{
        FileReader f = new FileReader(path);
        BufferedReader in = new BufferedReader(f);
        String s = in.readLine();
        Vector edges = new Vector(1);
        int[] temp_edges = new int[2];
        String temp="";
        int count;
        
        s = in.readLine();
        s = in.readLine();
        s = in.readLine();
        s = in.readLine();
        while(s != null){
            count = 0;
            for(int i=0;i<s.length();i++){
                if(s.charAt(i)!='	' && s.charAt(i)!=',')
                   temp += s.charAt(i);
                if(!temp.isEmpty())
                if(s.charAt(i) == '	' || s.charAt(i) == ',' || i==s.length()-1){
                    temp_edges[count] = Integer.parseInt(temp) - 1;
                    count++;
                    temp = "";
                }
            }

            edges.addElement(temp_edges);

            if(found(allNodes,temp_edges[0]) == -1)
                allNodes.addElement(temp_edges[0]);

            if(found(allNodes,temp_edges[1]) == -1)
                allNodes.addElement(temp_edges[1]);

            temp_edges = new int[2];
            s = in.readLine();
        }
        f.close();
        n = allNodes.size();
        m = edges.size();

        return edges;
    }
    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    


//Read 'benchmark labels file' from input argument 'path' and save it.
//Based on this file 'benchmark file' is constructed.
    static Vector Read_benchmark_labels(String path)throws Exception{
        Vector benchmark_labels = new Vector(1);
        Vector benchmark_temp = new Vector(1);
        try{
            FileReader f = new FileReader(path);
            BufferedReader in = new BufferedReader(f);
            String s = in.readLine();
            String temp;
            Vector line;
            while( s!= null ){
                temp = "";
                line = new Vector(1);
                for(int i=0;i<s.length();i++){
                    if(s.charAt(i)!=' ')
                       temp += s.charAt(i);
                    if(!temp.isEmpty())
                    if(s.charAt(i) == ' ' || i==s.length()-1){
                        line.addElement(Integer.parseInt(temp));
                        temp = "";
                    }
                }
                benchmark_labels.addElement(line);
                s = in.readLine();
            }
            f.close();
        }
        catch(FileNotFoundException err){
            System.out.println("No such file or directory!Try another file!Thanks...! *Hamidreza Alvari*");
            System.exit(1);
        }
        for(int i=0;i<benchmark_labels.size();i++){
            Vector temp = (Vector)benchmark_labels.elementAt(i);
            for(int j=0;j<temp.size();j++){
                if(benchmark_temp.size() <= (Integer)temp.elementAt(j) - 1){
                    benchmark_temp.setSize( (Integer)temp.elementAt(j)-1 );
                    Vector temp2 = new Vector(1);
                    temp2.addElement(i+1);
                    benchmark_temp.insertElementAt(temp2,(Integer)temp.elementAt(j) - 1);
                }
                else{
                    Vector temp2;
                    if( benchmark_temp.elementAt((Integer)temp.elementAt(j) - 1) != null ){
                        temp2 = (Vector)benchmark_temp.remove( (Integer)temp.elementAt(j) - 1 );
                        temp2.addElement(i+1);
                    }
                    else{
                        temp2 = new Vector(1);
                        temp2.addElement(i+1);
                        benchmark_temp.removeElementAt( (Integer)temp.elementAt(j) - 1 );
                    }
                    benchmark_temp.insertElementAt(temp2,(Integer)temp.elementAt(j) - 1);
                }
            }
        }
        return benchmark_temp;
    }
//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
//Read 'benchmark file' from input argument 'path' and save it.
//Each line of 'benchmark file' must refer to a certain community.
    static Vector Read_benchmark_communities(String path)throws Exception{
        Vector benchmark_temp = new Vector(1);
        try{
            FileReader f = new FileReader(path);
            BufferedReader in = new BufferedReader(f);
            String s = in.readLine();
            String temp;
            Vector line;
            while( s!= null ){
                temp = "";
                line = new Vector(1);
                for(int i=0;i<s.length();i++){
                    if(s.charAt(i)!=' ')
                       temp += s.charAt(i);
                    if(!temp.isEmpty())
                    if(s.charAt(i) == ' ' || i==s.length()-1){
                        line.addElement(Integer.parseInt(temp));
                        temp = "";
                    }
                }
                benchmark_temp.addElement(line);
                s = in.readLine();
            }
            f.close();
        }
        catch(FileNotFoundException err){
            System.out.println("No such file or directory!Try another file!Thanks...! *Hamidreza Alvari*");
            System.exit(1);
        }
        return benchmark_temp;
    }
//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::: 
    static int fair_random_agent_selection(){
        int min = 0;
        int selected = 0;
        for(int i=1;i<n;i++)
            if(agents[i].time_visited < agents[min].time_visited)
                min = i;
        Random rand = new Random();
        while(true){
            selected = rand.nextInt(n);
            if(agents[selected].time_visited <= agents[min].time_visited)
                break;
        }
        return selected;
    }
//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    static int fair_random_community_selection(){
        int[] sum = new int[communities.size()];
        for(int i=0;i<communities.size();i++){
            int[] temp = (int[])communities.elementAt(i);
            for(int j=0;j<temp.length;j++)
                sum[i] += agents[temp[j]-1].time_visited;
        }
        int min = 0;
        int selected = 0;
        for(int i=1;i<communities.size();i++)
            if(sum[i] < sum[min])
                min = i;
        Random rand = new Random();
        while(true){
            selected = rand.nextInt(communities.size());
            if(sum[selected] <= sum[min])
                break;
        }
        return selected;
    }
//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
//Choose best operation among join,leave & switch operations.
    static void Personal_Decision(int current){
        agent a_current;
        int[] nexts = agents[current].nexts;
//********************************Join Operation********************************
        Vector Us_Join = new Vector(1); // utilities for current agent by joining next possible communities
        Vector indx1 = new Vector(1);
        Vector indx2 = new Vector(1);
        for(int j=0;j<nexts.length;j++){
            for(int k=0;k<agents[nexts[j]].L.size();k++){
                if(found(agents[current].L,agents[nexts[j]].L.elementAt(k)) == -1){
                    a_current = new agent(agents[current].num,agents[current].L);
                    a_current.similarities = agents[current].similarities;
                    a_current.nexts = agents[current].nexts;
                    a_current.deg = agents[current].deg;
                    a_current.time_visited = agents[current].time_visited;
                    a_current.active_time = agents[current].active_time;
                    a_current.L.addElement(agents[nexts[j]].L.elementAt(k));
                    Us_Join.addElement(utility_calculate(a_current,0));
                    
                    //System.out.println("Join: "+agents[nexts[j]].num+":"+a_current.L.lastElement()+"--> "+Us_Join.lastElement());
                    //System.out.println("Join: "+( (Integer)allNodes.elementAt(agents[nexts[j]].num - 1) + 1)+":"+a_current.L.lastElement()+"--> "+Us_Join.lastElement());
                    
                    indx1.addElement(j);
                    indx2.addElement(agents[nexts[j]].L.elementAt(k));
                }
            }
        }
        int ind_Join = max(Us_Join);
//*****************************Leave Operation**********************************
        Vector Us_Leave = new Vector(1); // utilities for current agent by leaving next possible communities
        Vector indx3 = new Vector(1);
        for(int i=0;i<agents[current].L.size();i++){
            a_current = new agent(agents[current].num,agents[current].L);
            a_current.similarities = agents[current].similarities;
            a_current.nexts = agents[current].nexts;
            a_current.deg = agents[current].deg;
            if((Integer)a_current.L.elementAt(i) > 0){
                //System.out.println("Last label-----> "+a_current.L.elementAt(i));
                indx3.addElement(a_current.L.elementAt(i));
                a_current.L.removeElementAt(i);
                if(!a_current.L.isEmpty())
                    Us_Leave.addElement(utility_calculate(a_current,0));
                else
                    Us_Leave.addElement(0.0);
                //System.out.println("Leave: "+indx3.lastElement()+"--> "+Us_Leave.lastElement());
            }
        }
        int ind_Leave = max(Us_Leave);
//*****************************Switch Operation*********************************
        Vector Us_Switch = new Vector(1); // utilities for current agent by switching between communities
        Vector indx4 = new Vector(1);
        Vector indx5 = new Vector(1);
        a_current = new agent(agents[current].num,agents[current].L);
        a_current.nexts = agents[current].nexts;
        a_current.deg = agents[current].deg;
        a_current.similarities = agents[current].similarities;
        if(!a_current.L.isEmpty() && !indx3.isEmpty() && !communities.isEmpty()){
            a_current.L.removeElementAt(find_index2(a_current.L,(Integer)indx3.elementAt(ind_Leave)));
            for(int j=0;j<nexts.length;j++){
                for(int k=0;k<agents[nexts[j]].L.size();k++){
                    if(found(agents[current].L,agents[nexts[j]].L.elementAt(k)) == -1 &&
                       (Integer)agents[nexts[j]].L.elementAt(k) != (Integer)indx3.elementAt(ind_Leave) &&
                       (Integer)agents[nexts[j]].L.elementAt(k) <= communities.size() &&
                       (Integer)agents[nexts[j]].L.elementAt(k) > 0)
                    {
                        Vector L = new Vector(1);
                        for(int i=0;i<agents[current].L.size();i++)
                          if((Integer)agents[current].L.elementAt(i) != (Integer)indx3.elementAt(ind_Leave))
                            L.addElement(agents[current].L.elementAt(i));
                        a_current = new agent(agents[current].num,L);
                        a_current.similarities = agents[current].similarities;
                        a_current.nexts = agents[current].nexts;
                        a_current.deg = agents[current].deg;
                        a_current.L.addElement(agents[nexts[j]].L.elementAt(k));
                        Us_Switch.addElement(utility_calculate(a_current,0));
                        //System.out.println("Switch: "+indx3.elementAt(ind_Leave)+"-->("+agents[nexts[j]].num+":"+a_current.L.lastElement()+")--> "+Us_Switch.lastElement());
                        indx4.addElement(j);
                        indx5.addElement(agents[nexts[j]].L.elementAt(k));
                    }
                }
            }
        }
        int ind_Switch = max(Us_Switch);
//******************************************************************************
//Now check whether it is better to join or leave or switch for current agent
        double oldUtility = agents[current].utility;
        double newUtility = 0;
        if( !Us_Join.isEmpty() && Us_Leave.isEmpty())
            newUtility = Join(current,indx1,indx2,Us_Join,ind_Join);
        else if( Us_Join.isEmpty() && !Us_Leave.isEmpty())
            newUtility = Leave(current,indx3,Us_Leave,ind_Leave);
        else if(!Us_Join.isEmpty() && !Us_Leave.isEmpty()){
            int best_operation = 2; //Suppose that join operation is the best among all operations
            if(!Us_Switch.isEmpty()){
                if( (Double)Us_Switch.elementAt(ind_Switch) > (Double)Us_Join.elementAt(ind_Join)  &&
                    (Double)Us_Switch.elementAt(ind_Switch) > (Double)Us_Leave.elementAt(ind_Leave) ){
                        best_operation = 1; //Switch
                }
                else{
                    if((Double)Us_Leave.elementAt(ind_Leave) > (Double)Us_Join.elementAt(ind_Join))
                        best_operation = 3; //Leave
                }
            }
            if(best_operation == 1)
                newUtility = Switch(current,indx4,indx5,indx3,Us_Switch,Us_Leave,ind_Leave,ind_Switch);
            if(best_operation == 2)
                newUtility = Join(current,indx1,indx2,Us_Join,ind_Join);
            if(best_operation == 3)
                newUtility = Leave(current,indx3,Us_Leave,ind_Leave);
        }
        agents[current].improve = newUtility - oldUtility;
    }
//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    static void Joint_Decision(){
        int current_community = 0;
        if( !communities.isEmpty() ){
                current_community = fair_random_community_selection();
                Invitation(current_community);
                current_community = fair_random_community_selection();
                Eviction(current_community);
            }
    }
//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    static void Invitation(int current){
        Vector nodes = new Vector(1);
        int[] temp = (int[])communities.elementAt(current);
        double last_gain = 0;
        for(int i=0;i<temp.length;i++){
            nodes.addElement(temp[i]);
            last_gain += agents[temp[i]-1].utility;
        }
        double new_gain;
        Vector gains = new Vector(1);
        Vector indx1 = new Vector(1);
        Vector indx2 = new Vector(1);
        for(int i=0;i<nodes.size();i++){
            for(int j=0;j<agents[(Integer)nodes.elementAt(i)-1].nexts.length;j++){
                if( found(nodes,agents[agents[(Integer)nodes.elementAt(i)-1].nexts[j]].num) == -1){
                    nodes.addElement( agents[agents[(Integer)nodes.elementAt(i)-1].nexts[j]].num );
                    agents[agents[(Integer)nodes.elementAt(i)-1].nexts[j]].L.addElement( current + 1 );
                    new_gain = 0;
                    for(int k=0;k<nodes.size();k++)
                        new_gain += utility_calculate( agents[(Integer)nodes.elementAt(k)-1],0);
                    gains.addElement(new_gain);
                    indx1.addElement((Integer)nodes.elementAt(i)-1);
                    indx2.addElement(j);
                    nodes.removeElementAt(nodes.size()-1);
                    agents[agents[(Integer)nodes.elementAt(i)-1].nexts[j]].L.removeElementAt(agents[agents[(Integer)nodes.elementAt(i)-1].nexts[j]].L.size() - 1);
                }
            }
        }
        int ind_Invited = max(gains);
        if(!gains.isEmpty())
        if((Double)gains.elementAt(ind_Invited) > last_gain){
            //System.out.println("Invitation in community: "+(current + 1));
            agents[agents[(Integer)indx1.elementAt(ind_Invited)].nexts[(Integer)indx2.elementAt(ind_Invited)]].invited++;
            if(!Reject_Invite( agents[agents[(Integer)indx1.elementAt(ind_Invited)].nexts[(Integer)indx2.elementAt(ind_Invited)]].num , current )){
                //System.out.println("Accept invitation");
                //System.out.println("Agent "+agents[agents[(Integer)indx1.elementAt(ind_Invited)].nexts[(Integer)indx2.elementAt(ind_Invited)]].num+" accepted the invitation");
                nodes.addElement( agents[agents[(Integer)indx1.elementAt(ind_Invited)].nexts[(Integer)indx2.elementAt(ind_Invited)]].num );
                if((Integer)agents[agents[(Integer)indx1.elementAt(ind_Invited)].nexts[(Integer)indx2.elementAt(ind_Invited)]].L.elementAt(0) == -(agents[agents[(Integer)indx1.elementAt(ind_Invited)].nexts[(Integer)indx2.elementAt(ind_Invited)]].num))
                    agents[agents[(Integer)indx1.elementAt(ind_Invited)].nexts[(Integer)indx2.elementAt(ind_Invited)]].L.removeElementAt(0);
                agents[agents[(Integer)indx1.elementAt(ind_Invited)].nexts[(Integer)indx2.elementAt(ind_Invited)]].L.addElement( current + 1 );
                agents[agents[(Integer)indx1.elementAt(ind_Invited)].nexts[(Integer)indx2.elementAt(ind_Invited)]].joined++;
                temp = new int[nodes.size()];
                for(int i=0;i<nodes.size();i++){
                    temp[i] = (Integer)nodes.elementAt(i);
                    double util = utility_calculate(agents[temp[i] - 1],0);
                    agents[temp[i] - 1].improve = util - agents[temp[i] - 1].utility;
                    agents[temp[i] - 1].utility = util;
                }
                communities.removeElementAt(current);
                communities.insertElementAt(sort(temp),current);
            }
            else{
               //System.out.println("Reject invitation in community: "+(current + 1));
               //System.out.println("Agent "+agents[agents[(Integer)indx1.elementAt(ind_Invited)].nexts[(Integer)indx2.elementAt(ind_Invited)]].num +" rejected the invitation");
               agents[agents[(Integer)indx1.elementAt(ind_Invited)].nexts[(Integer)indx2.elementAt(ind_Invited)]].rejected++;
            }
        }
    }
//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    static boolean Reject_Invite(int current_node,int current_community){
        double before_util = agents[current_node-1].utility;
        agents[current_node-1].L.addElement(current_community + 1);
        double new_util = utility_calculate(agents[current_node-1],0);
        agents[current_node-1].L.removeElementAt(agents[current_node-1].L.size()-1);
        if(new_util < before_util)
            return true;
        return false;
    }
//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    static void Eviction(int current){
        Vector nodes = new Vector(1);
        int[] temp = (int[])communities.elementAt(current);
        double last_gain = 0;
        for(int i=0;i<temp.length;i++){
            nodes.addElement(temp[i]);
            last_gain += agents[temp[i]-1].utility;
        }
        double new_gain;
        Vector gains = new Vector(1);
        for(int i=0;i<nodes.size();i++){
            int evicted = (Integer)nodes.remove(i);
            agents[evicted - 1].L.removeElementAt( find_index2(agents[evicted - 1].L,current + 1) );
            new_gain = 0;
            for(int j=0;j<nodes.size();j++)
                new_gain += utility_calculate( agents[(Integer)nodes.elementAt(j)-1],0);
            gains.addElement(new_gain);
            agents[evicted - 1].L.addElement(current + 1);
            nodes.insertElementAt(evicted, i);
        }
        int ind_Evicted = max(gains);
        if(!gains.isEmpty())
        if((Double)gains.elementAt(ind_Evicted) > last_gain){
            //System.out.println("Eviction in community "+(current+1));
            if(nodes.size() >= 3){
                int evicted = (Integer)nodes.remove(ind_Evicted);
                agents[evicted - 1].L.removeElementAt( find_index2(agents[evicted - 1].L,current + 1) );
                agents[evicted - 1].evicted++;
                agents[evicted - 1].left++;
                if(agents[evicted - 1].L.isEmpty()){
                    agents[evicted - 1].L.addElement( -(evicted) );
                    agents[evicted - 1].utility = 0;
                }
                else{
                    double util = utility_calculate(agents[evicted - 1],0);
                    agents[evicted - 1].improve = util - agents[evicted - 1].utility;
                    agents[evicted - 1].utility = util;
                }
                temp = new int[nodes.size()];
                for(int i=0;i<nodes.size();i++){
                    temp[i] = (Integer)nodes.elementAt(i);
                    double util = utility_calculate(agents[temp[i]-1],0);
                    agents[temp[i]-1].improve = util - agents[temp[i]-1].utility;
                    agents[temp[i]-1].utility = util;
                }
                communities.removeElementAt(current);
                communities.insertElementAt(temp,current);
            }
            else{
                communities.removeElementAt(current);
                int evicted = (Integer)nodes.elementAt(ind_Evicted);
                agents[evicted - 1].evicted++;
                agents[evicted - 1].left++;
                for(int i=0;i<nodes.size();i++){                    
                    agents[(Integer)nodes.elementAt(i) - 1].L.removeElementAt( find_index2(agents[(Integer)nodes.elementAt(i) - 1].L,current+1) );
                    if(agents[(Integer)nodes.elementAt(i) - 1].L.isEmpty()){
                        agents[(Integer)nodes.elementAt(i) - 1].L.addElement( -((Integer)nodes.elementAt(i)) );
                        agents[(Integer)nodes.elementAt(i) - 1].utility = 0;
                    }
                    else{
                        double util = utility_calculate(agents[(Integer)nodes.elementAt(i) - 1],0);
                        agents[(Integer)nodes.elementAt(i) - 1].improve = util - agents[(Integer)nodes.elementAt(i) - 1].utility;
                        agents[(Integer)nodes.elementAt(i) - 1].utility = util;

                    }
                }
                for(int i=current;i<communities.size();i++){
                    temp = (int[])communities.elementAt(i);
                    for(int j=0;j<temp.length;j++){
                        int ii = find_index2(agents[temp[j]-1].L,i+2);
                        if(ii != -1){
                            int last = (Integer)agents[temp[j]-1].L.remove(ii);
                            last--;
                            agents[temp[j]-1].L.addElement(last);
                        }
                    }
                }
            }
        }
    }
//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    static double Graph_Modularity_calculate(Vector a,String mode){
        double Q = 0;
        if(mode.equals("Current")){
            double delta;
            int LiLj;
            for(int i=0;i<n;i++){
                for(int j=0;j<n;j++){
                    delta = 0;
                    if (i != j){
                        LiLj = intersect(agents[i],agents[j]);
                        if (LiLj > 0)
                            delta = 1;
                        //System.out.println(((Integer)allNodes.elementAt(i)+1) + " " + ((Integer)allNodes.elementAt(j)+1) + " " + LiLj + " deg : " + agents[i].deg + "Aij : " + A[i][j]);
                        double dif;
                        if ( find_index(agents[i].nexts,j) != -1)
                            dif = delta * (1 -  ((agents[i].In_deg * agents[j].deg * 1.0)/m));
                        else
                            dif = delta * (0 -  ((agents[i].In_deg * agents[j].deg * 1.0)/m));
                        //System.out.println("dif : " + dif);
                        Q += dif;
                    }
                }
            }
            return Q/(m);
        }
        else{
            int ind1,ind2;
            for(int i=0;i<a.size();i++){
                Vector temp = (Vector)a.elementAt(i);
                for(int j=0;j<temp.size();j++){
                    for(int k=0;k<temp.size();k++){
                        if (j != k){
                            ind1 = find_index2(allNodes, (Integer)temp.elementAt(j)-1);
                            ind2 = find_index2(allNodes, (Integer)temp.elementAt(k)-1);
                            if ( find_index(agents[ind1].nexts,ind2) != -1)
                                Q += 1 - (agents[ind1].In_deg * agents[ind2].deg)/(m);
                            else
                                Q += 0 - (agents[ind1].In_deg * agents[ind2].deg)/(m);
                        }
                    }
                }
            }
            return Q/(m);
        }
    }
    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    static int intersect(agent a,agent b){
        Vector La = a.L;
        Vector Lb = b.L;
        Vector intersects=new Vector(1);
        int intersection = 0;
        for(int i=0;i<La.size();i++)
            for(int j=0;j<Lb.size();j++)
                if(La.elementAt(i).equals(Lb.elementAt(j)))
                        intersects.addElement(La.elementAt(i));
        int negatives=0,positives=0;
        for(int i=0;i<intersects.size();i++){
            if((Integer)intersects.elementAt(i) < 0)
                negatives++;
            else
                positives++;
        }
        if(positives != 0)
            intersection = positives;
        else
            intersection = negatives;
        return intersection;
    }
//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    static int found(Vector v,Object item){
        for(int i=0;i<v.size();i++)
            if(v.elementAt(i).equals(item))
                return i;
        return -1;
    }
//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    static double utility_calculate(agent a_current,int mode){
        double Q=0;
        int LiLj=0,delta=0;
        int current = a_current.num - 1;
        for(int next=0;next<agents.length;next++){
            if(next != current){
                delta = 0;
                LiLj = intersect(a_current,agents[next]);
                if(LiLj > 0)
                  delta = 1;
                Q += agents[current].similarities[next] * delta;
                //Q += A[current][next] * delta - LiLj * (a_current.deg * agents[next].deg) * 1.0 /(2 * m);
            }
        }        
        Q /= (2*m);
        
        if( mode == 0 && (Integer)a_current.L.elementAt(0) != -(current + 1)){
        //if(mode == 0 && a_current.L.size() > 1)
            Q -= (a_current.L.size()-1) * 1.0/(2*m);
        }
        //Q -= a_current.active_time * 1.0;
        return Q;
    }
//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    static int max(Vector v){
        int ind = 0;
        for(int i=1;i<v.size();i++)
            if((Double)v.elementAt(i)>(Double)v.elementAt(ind))
                ind = i;
        return ind;
    }
//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    static double Join(int current,Vector indx1,Vector indx2,Vector Us_Join,int ind_Join){
        int[] nexts = agents[current].nexts;
        agents[current].status(4);
        if(agents[current].utility < (Double)Us_Join.elementAt(ind_Join)){
            agents[current].status(1);
            agents[current].noOp--;
            agents[current].time_visited--;
            //System.out.println("Now Join...");
            agents[current].utility = (Double)Us_Join.elementAt(ind_Join);
            //System.out.println("New Q: "+agents[current].utility);
            //New community will be added to current communities
                if((Integer)indx2.elementAt(ind_Join) < 0){
                   int[] temp = {agents[current].num,agents[nexts[(Integer)indx1.elementAt(ind_Join)]].num};
                   communities.addElement(sort(temp));
                   for(int i=0;i<agents[current].L.size();i++)
                       if((Integer)agents[current].L.elementAt(i) < 0)
                           agents[current].L.removeElementAt(i);
                   agents[current].L.addElement(communities.size());
                   //System.out.println("New Label(Join): "+agents[current].L.lastElement());
                   for(int i=0;i<agents[nexts[(Integer)indx1.elementAt(ind_Join)]].L.size();i++)
                       if((Integer)agents[nexts[(Integer)indx1.elementAt(ind_Join)]].L.elementAt(i) < 0)
                           agents[nexts[(Integer)indx1.elementAt(ind_Join)]].L.removeElementAt(i);
                   /*agents[nexts[(Integer)indx1.elementAt(ind_Join)]].L.addElement(communities.size());
                   agents[nexts[(Integer)indx1.elementAt(ind_Join)]].improve = agents[current].utility - agents[nexts[(Integer)indx1.elementAt(ind_Join)]].utility;
                   agents[nexts[(Integer)indx1.elementAt(ind_Join)]].utility = agents[current].utility;*/


                   agents[nexts[(Integer)indx1.elementAt(ind_Join)]].L.addElement(communities.size());
                   double old_U = agents[nexts[(Integer)indx1.elementAt(ind_Join)]].utility;
                   double new_U = utility_calculate(agents[nexts[(Integer)indx1.elementAt(ind_Join)]], 0);
                   agents[nexts[(Integer)indx1.elementAt(ind_Join)]].improve = new_U - old_U;
                   agents[nexts[(Integer)indx1.elementAt(ind_Join)]].utility = new_U;
                }
                //One of current communities is modified
                else{
                   int[] temp1 = (int[])communities.elementAt((Integer)indx2.elementAt(ind_Join)-1);
                   int[] temp = new int[temp1.length+1];
                   for(int i=0;i<temp1.length;i++)
                       temp[i]=temp1[i];
                   temp[temp.length-1]=current+1;
                   for(int i=0;i<agents[current].L.size();i++)
                       if((Integer)agents[current].L.elementAt(i) < 0)
                           agents[current].L.removeElementAt(i);
                   agents[current].L.addElement((Integer)indx2.elementAt(ind_Join));
                   //System.out.println("New Label(Join): "+agents[current].L.lastElement());
                   communities.removeElementAt((Integer)indx2.elementAt(ind_Join)-1);
                   communities.insertElementAt(sort(temp),(Integer)indx2.elementAt(ind_Join)-1);
                   for(int i=0;i<temp.length;i++)
                       if(temp[i] != current + 1){
                           double util = utility_calculate(agents[temp[i]-1],0);
                           agents[temp[i]-1].improve = util - agents[temp[i]-1].utility;
                           agents[temp[i]-1].utility = util;
                       }
                }
            }
        return agents[current].utility;
    }
//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    static double Leave(int current,Vector indx3,Vector Us_Leave,int ind_Leave){
        agents[current].status(4);
        if(agents[current].utility < (Double)Us_Leave.elementAt(ind_Leave)){
            agents[current].status(2);
            agents[current].noOp--;
            agents[current].time_visited--;
            //System.out.println("Now Leave...");
            agents[current].utility = (Double)Us_Leave.elementAt(ind_Leave);
            //System.out.println("New Q(Leave): "+agents[current].utility);
            int[] temp = (int[])communities.remove((Integer)indx3.elementAt(ind_Leave) - 1);
            int ind = find_index(temp,agents[current].num);
            int[] temp2 = new int[temp.length-1];
            int count = 0;
            for(int i=0;i<temp.length;i++)
                if(i != ind){
                    temp2[count] = temp[i];
                    count++;
                }
            if(temp2.length > 1)
                communities.insertElementAt(sort(temp2),(Integer)indx3.elementAt(ind_Leave) - 1);
            else{
                if(agents[temp2[0]-1].L.size() == 1){
                    agents[temp2[0]-1].L.removeAllElements();
                    agents[temp2[0]-1].L.addElement(-temp2[0]);
                    agents[temp2[0]-1].utility = 0;
                }
                else
                    agents[temp2[0]-1].L.removeElementAt(find_index2(agents[temp2[0]-1].L,(Integer)indx3.elementAt(ind_Leave)));
                for(int i=(Integer)indx3.elementAt(ind_Leave)-1;i<communities.size();i++){
                    temp = (int[])communities.elementAt(i);
                    for(int j=0;j<temp.length;j++){
                        int last = (Integer)agents[temp[j]-1].L.remove(find_index2(agents[temp[j]-1].L,i+2));
                        last--;
                        agents[temp[j]-1].L.addElement(last);
                    }
                }
            }
            agents[current].L.removeElementAt(find_index2(agents[current].L,(Integer)indx3.elementAt(ind_Leave)));
            if(agents[current].L.isEmpty()){
                agents[current].L.addElement(-(current+1));
                agents[current].utility = 0;
            }
            //System.out.println("New label(Leave): "+agents[current].L.lastElement());
            for(int i=0;i<temp2.length;i++){
              double util = utility_calculate(agents[temp2[i]-1],0);
              agents[temp2[i]-1].improve = util - agents[temp2[i]-1].utility;
              agents[temp2[i]-1].utility = util;
            }
        }
        return agents[current].utility;
    }
//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    static double Switch(int current,Vector indx4,Vector indx5,Vector indx3,Vector Us_Switch,Vector Us_Leave,int ind_Leave,int ind_Switch){
        agents[current].status(4);
        if(agents[current].utility < (Double)Us_Switch.elementAt(ind_Switch)){
            agents[current].status(3);
            agents[current].noOp--;
            agents[current].time_visited--;
            //System.out.println("Now Switch...");
            agents[current].utility = (Double)Us_Switch.elementAt(ind_Switch);
            //System.out.println("New Q(Switch): "+agents[current].utility);
            int[] temp = (int[])communities.remove((Integer)indx3.elementAt(ind_Leave) - 1);
            int ind = find_index(temp,agents[current].num);
            int[] temp2 = new int[temp.length-1];
            int count = 0;
            for(int i=0;i<temp.length;i++)
                if(i != ind){
                    temp2[count] = temp[i];
                    count++;
                }
            if(temp2.length > 1)
                communities.insertElementAt(sort(temp2),(Integer)indx3.elementAt(ind_Leave) - 1);
            else {
                if(temp2.length == 1){
                    if(agents[temp2[0]-1].L.size() == 1){
                        agents[temp2[0]-1].L.removeAllElements();
                        agents[temp2[0]-1].L.addElement(-temp2[0]);
                        agents[temp2[0]-1].utility = 0;
                    }
                    else
                        agents[temp2[0]-1].L.removeElementAt(find_index2(agents[temp2[0]-1].L,(Integer)indx3.elementAt(ind_Leave)));
                }
                for(int i=(Integer)indx3.elementAt(ind_Leave)-1;i<communities.size();i++){
                    temp = (int[])communities.elementAt(i);
                    for(int j=0;j<temp.length;j++){
                        int ii = find_index2(agents[temp[j]-1].L,i+2);
                        if(ii != -1){
                            int last = (Integer)agents[temp[j]-1].L.remove(ii);
                            last--;
                            agents[temp[j]-1].L.addElement(last);
                        }
                    }
                }
                if((Integer)indx5.elementAt(ind_Switch) >= (Integer)indx3.elementAt(ind_Leave)){
                    int last = (Integer)indx5.remove(ind_Switch);
                    last--;
                    indx5.insertElementAt(last, ind_Switch);
                }
            }
            agents[current].L.removeElementAt(find_index2(agents[current].L,(Integer)indx3.elementAt(ind_Leave)));            
            for(int i=0;i<temp2.length;i++){
              double util = utility_calculate(agents[temp2[i]-1],0);
              agents[temp2[i]-1].improve = util - agents[temp2[i]-1].utility;
              agents[temp2[i]-1].utility = util;
            }
           //Join
           int[] temp1 = (int[])communities.elementAt((Integer)indx5.elementAt(ind_Switch)-1);
           temp = new int[temp1.length+1];
           for(int i=0;i<temp1.length;i++)
              temp[i]=temp1[i];
           temp[temp.length-1]=current+1;
           agents[current].L.addElement((Integer)indx5.elementAt(ind_Switch));
           //System.out.println("New Label(Switch): "+agents[current].L.lastElement());
           communities.removeElementAt((Integer)indx5.elementAt(ind_Switch)-1);
           communities.insertElementAt(sort(temp),(Integer)indx5.elementAt(ind_Switch)-1);
           for(int i=0;i<temp.length;i++)
             if(temp[i] != current + 1){
                 double util = utility_calculate(agents[temp[i]-1],0);
                 agents[temp[i]-1].improve = util - agents[temp[i]-1].utility;
                 agents[temp[i]-1].utility = util;
             }
        }
        return agents[current].utility;
    }
//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    static int find_index(int[] a,int b){
        for(int i=0;i<a.length;i++)
            if(a[i] == b)
                return i;
        return -1;
    }
//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    static int find_index2(Vector a,int b){
        for(int i=0;i<a.size();i++)
            if((Integer)a.elementAt(i) == b)
                return i;
        return -1;
    }
//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    static int[] sort(int[] a){
        int temp=0;
        for(int i=0;i<a.length-1;i++){
            for(int j=i+1;j<a.length;j++){
                if(a[i]>a[j]){
                    temp = a[i];
                    a[i] = a[j];
                    a[j] = temp;
                }
            }
        }
        return a;
    }
//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    static void Print_Communities_NOverlap(String mode,String path) throws Exception{
        FileWriter f1,f2;
        BufferedWriter out1,out2;
        if(mode.equals("Result")){
            f1 = new FileWriter(path);
            out1 = new BufferedWriter(f1);
            for(int i=0;i<communities.size();i++){
                Vector temp = (Vector)communitiesNoOverlap.elementAt(i);
                for(int j=0;j<temp.size();j++)
                    out1.append( ((Integer)allNodes.elementAt( (Integer)temp.elementAt(j) - 1) + 1) +" ");
                out1.newLine();
            }
            out1.flush();
            out1.close();
            f1.close();
        }
    }
//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    static void Print_Communities(String mode,String path) throws Exception{
        FileWriter f1,f2;
        BufferedWriter out1,out2;
        if(mode.equals("Result")){
            f1 = new FileWriter(path);
            out1 = new BufferedWriter(f1);
            for(int i=0;i<communities.size();i++){
                int[] temp = (int[])communities.elementAt(i);
                for(int j=0;j<temp.length;j++)
                    out1.append( ((Integer)allNodes.elementAt( temp[j] - 1) + 1) +" ");
                out1.newLine();
            }
            out1.flush();
            out1.close();
            f1.close();
        }
        else if(mode.equals("Benchmark")){
            f2 = new FileWriter(path);
            out2 = new BufferedWriter(f2);
            for(int i=0;i<benchmark.size();i++){
                Vector temp = (Vector)benchmark.elementAt(i);
                for(int j=0;j<temp.size();j++)
                    out2.append( (Integer)temp.elementAt(j)+" " );
                out2.newLine();
            }
            out2.flush();
            out2.close();
            f2.close();
        }
    }
}
//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
class agent{
    int num;//Number
    int deg;
    int In_deg;
    int time_visited = 0;
    int joined = 0;
    int left = 0;
    int switched = 0;
    int noOp;
    int invited = 0;
    int rejected = 0;
    int evicted = 0;
    int[] nexts;//Next agents
    Vector tempNexts = new Vector();
    double[] similarities;
    double utility=0; //Utility = Personalized modularity-Loss
    double improve=0; //Differnece between two iterations
    Vector L = new Vector(); //Labels
    int mainLabel=-1;
    double active_time = 0;
    agent(int num,Vector L){
        this.num = num;
        for(int i=0;i<L.size();i++)
            this.L.addElement(L.elementAt(i));
    }
    void status(int status){
        time_visited++;
        switch(status){
            case 1:
                joined++;
                break;
            case 2:
                left++;
                break;
            case 3:
                switched++;
                break;
            case 4:
                noOp++;
                break;
            default:
                break;
        }
    }
}
//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
//End of all classes