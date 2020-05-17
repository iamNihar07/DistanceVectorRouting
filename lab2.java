//Nihar Gupte ID 1001556441

import java.io.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.util.concurrent.TimeUnit;

public class lab2 
{

    static int lines=0; //stores number of lines in network topology file
    static int numOfVertices=0; //stores number of nodes / routers
    static final int MAX_VALUE = 999; //setting max value as 999 which behaves as infinity implying that if distance between A and B is 999, they are not connected
    static JFrame frame,frame1,ResultsFrame; //GUI aspects
    static FileWriter fw,fw1; //Writing results and vertexes
    public static void main(String[] args) 
    {
        lab2 ob=new lab2();
        ob.start();

        do
        {
            try
            {
                TimeUnit.SECONDS.sleep(15);
            }
            catch(Exception e)
            {
                System.out.println("Error: "+e);
            }
            String text = JOptionPane.showInputDialog("Do you want to continue? Type Y or N"); //asking user if he has another network topology file that he wants to run
            if(text.equalsIgnoreCase("Y")) //if yes
            {
                ob.start(); //restart the procedure
            }
            else
            {                
                break;
            }
        }while(true);

        
        try
        {
            TimeUnit.SECONDS.sleep(120); //close all the GUI windows after 2 minutes, enough time to read the data displayed on GUIs and exit the program
            System.exit(0);
        }
        catch(Exception e)
        {
            System.out.println("Error: "+e);
        }
    }

    //starter function
    void start()
    {
    
        String filename=openFile();  //gets the filename from GUI using JFileChooser
        File inFile = new File(filename); //opening the file for reading
        showFileOnGUI(filename); //displaying the network topology on the GUI
        

        BufferedReader br = null;
        try 
        {
            String sCurrentLine;
            Scanner in = new Scanner(inFile);
            br = new BufferedReader(new FileReader(inFile));
            while ((sCurrentLine = br.readLine()) != null)
            {
                lines++; //counting number of lines in the file for extraction of values
                //System.out.println(sCurrentLine);
            }

            int arr_size=lines*3;
            int arr[]=new int[arr_size]; //creating an array to store values from the topology txt file
            int k=0;

            br = new BufferedReader(new FileReader(inFile));
            while ((sCurrentLine = br.readLine()) != null) //filling the array according to data from topology file
            {
                arr[k]=in.nextInt();
                arr[k+1]=in.nextInt();
                arr[k+2]=in.nextInt();
                k+=3;                
            }

            k=0;
            /*for(int i=0;i<lines;i++)
            {
                System.out.println(arr[k]+" "+arr[k+1]+" "+arr[k+2]);
                k+=3;
            }*/

            
            numOfVertices= getNumberOfVertices(arr); //get the number of vertices from the given topology txt file
            if(numOfVertices>6) //check whether the given condition of 6 nodes is followed
            {
                System.out.println("Maximum of 6 nodes only allowed.");
            }
            //System.out.println(numOfVertices);


            int adjacencyMatrix[][]=new int[numOfVertices+1][numOfVertices+1]; //adjacency matrix defined for rows and columns 1 to number of vertices
            initializeAdjM(adjacencyMatrix); //initializing the matrix
            //ob.printAdjM(adjacencyMatrix);

            setAdjM(adjacencyMatrix,arr); //creating the adjacency matrix based on data from topology txt file which is now stored in arr
            //printAdjM(adjacencyMatrix);

            printInitialState(adjacencyMatrix); //printing the initial state of the adjacency matrix to text file, node by node
            showFileOnGUI2("initial.txt",frame1,"Intial States"); //displaying the initial state on gui

            createNodesFile(numOfVertices); //creating n text files for n nodes          


            try
            {                    
                fw=new FileWriter("Results.txt",false);    //opening the results file to append the final states                
            }
            catch(Exception e)
            {
                System.out.println("Error: "+e);
            }          

            int choice=0;
            while(true)
            {
                String text2 = JOptionPane.showInputDialog("Type 1 for Single Step Mode and 2 for Non Stop Mode"); //GUI asking for type of mode
                if(text2.equalsIgnoreCase("1"))
                {
                    choice=1;
                    break;
                }
                else if(text2.equalsIgnoreCase("2"))
                {
                    choice=2;
                    break;
                }
                else
                {
                    //
                }
            }            


            if(choice==1)
            {
                for(int i=1;i<=numOfVertices;i++)
                {
                    
                    try
                    {
                        fw1=new FileWriter("Vertex"+i+".txt",false); //open every vertex file
                        BellmanFordEvaluationSingleStep(i,adjacencyMatrix); //step by step, write distance vector table to each file
                        fw1.close();
                        showFileOnGUI2("Vertex"+i+".txt",frame1,"Distance Vector Table for Node "+i); //display the distance vector tables for n vertex files on n GUIs
                        
                    }
                    catch(Exception e)
                    {
                        System.out.println("Exception: "+e);
                    }
                }  
            }

            

            if(choice==2)
            {
                long startTime = System.nanoTime(); //start measuring system execution                 
                
                for(int i=1;i<=numOfVertices;i++)
                {
                    BellmanFordEvaluation(i,adjacencyMatrix);  //run the algorithm, without writing distance vector table to each file at every step
                    // get only the final state distance vector table and write it to Results.txt
                }   

                long stopTime = System.nanoTime(); //stop measuring system execution
                double time=(stopTime - startTime)*0.000000001; //covnersion of nano to secocnds
                JOptionPane.showMessageDialog(null, "Total execution time: "+time+" seconds"); //shows a dialog box with execution time
                //System.out.println("Total execution time: "+time+" seconds");
            }   

            fw.close();          
            
            
            String current = new java.io.File( "." ).getCanonicalPath();
            current=current+"\\Results.txt";
            
            //String filecontents=getStringfromFile(current);
            showFileOnGUI2(current,ResultsFrame,"Results"); //finally, display the results from Results.txt onto a GUI displaying the final distance vector tables
            
        } 
        catch (IOException e)
        {
            e.printStackTrace();
        } 
        finally 
        {
            try 
            {
                if (br != null)
                {  
                    br.close();                    
                }
            } 
            catch (IOException ex) 
            {
                ex.printStackTrace();
            }
        }
    }

    //function to get the number of vertices in the network topology, stored in int arr[]
    int getNumberOfVertices(int arr[])
    {
        int max=0;
        int k=0;
        for(int i=0;i<lines;i++)
        {
            if(arr[k]>max)
            {
                max=arr[k];
            }
            if(arr[k+1]>max)
            {
                max=arr[k+1];
            }
            k+=3;
        }

        return max;
    }

    //function to initialize the adjacency matrix to 0 for rows and columns corresponding to 0th index
    // to initialize all rows and columns of the type (i,i) to 0
    // and to initialize all other rows and coumns to infinity (MAX_VALUE 999) in our case
    void initializeAdjM(int adjacencyMatrix[][])
    {
        for(int i=0;i<numOfVertices+1;i++)
        {
           adjacencyMatrix[0][i]=0;
        }

        for(int i=1;i<=numOfVertices;i++)
        {
            for(int j=1; j<=numOfVertices; j++)
            {
                if(i==j)
                {
                    adjacencyMatrix[i][j]=0;
                    continue;
                }
                adjacencyMatrix[i][j]= MAX_VALUE;
            }
        }
    }

    //function to print Adjacency matrix used for debugging purposes
    void printAdjM(int adjacencyMatrix[][])
    {
        
        System.out.println();
        for(int i=0;i<numOfVertices+1;i++)
        {
            for(int j=0;j<numOfVertices+1;j++)
            {
                System.out.print(adjacencyMatrix[i][j]+" ");
            }
            System.out.println();
        }
    }

    //function to develop the actual adjacency matrix based on network topology info stored in int arr[]
    void setAdjM(int adjacencyMatrix[][], int arr[])
    {
        for(int i=1;i<=numOfVertices;i++)
        {
            for(int j=1; j<=numOfVertices; j++)
            {
                int k=0;
                for(int l=0;l<lines;l++)
                {
                    if(arr[k]==i && arr[k+1]==j) //if the the nodes are connected, then write their distances in the adjacency matrix
                    {
                        adjacencyMatrix[i][j]=arr[k+2];
                        adjacencyMatrix[j][i]=arr[k+2];
                    }
                    k+=3;
                }
            }
        }
    }

    
    //function to get least cost path from given source to all other vertices and write data to vertex text file at each step displaying change in distance vector table
    //and write final results to results.txt
    //https://www.sanfoundry.com/java-program-implement-bellmanford-algorithm/
    public void BellmanFordEvaluationSingleStep(int source, int adjacencymatrix[][])
    {

        int distances[]=new int[numOfVertices+1]; //the array to store least cost path for all vertices
    	
        for (int node = 1; node <= numOfVertices; node++)
        {
            distances[node] = MAX_VALUE; //setting default to our max value, i.e infinity
        }
        
        

        //getting information from neighbors' and building the network topology graph
        distances[source] = 0;
        for (int node = 1; node <= numOfVertices - 1; node++)
        {
            for (int sourcenode = 1; sourcenode <= numOfVertices; sourcenode++)
            {
                for (int destinationnode = 1; destinationnode <= numOfVertices; destinationnode++)
                {
                    if (adjacencymatrix[sourcenode][destinationnode] != MAX_VALUE)
                    {
                        if (distances[destinationnode] > distances[sourcenode] 
                                + adjacencymatrix[sourcenode][destinationnode])
                            distances[destinationnode] = distances[sourcenode]
                                + adjacencymatrix[sourcenode][destinationnode];
                    }
                }
                
                
                printDistances(distances);
                
            }
            //printDistances(distances);
        }
 
        //calculate the cost from source to all other vertices
        for (int sourcenode = 1; sourcenode <= numOfVertices; sourcenode++)
        {
            for (int destinationnode = 1; destinationnode <= numOfVertices; destinationnode++)
            {
                if (adjacencymatrix[sourcenode][destinationnode] != MAX_VALUE)
                {
                    if (distances[destinationnode] > distances[sourcenode]
                           + adjacencymatrix[sourcenode][destinationnode])
                        System.out.println("The Graph contains negative egde cycle");
                }
            }
        }
 
        //writing results to  txt file from distances array
        try
        {
            for (int vertex = 1; vertex <= numOfVertices; vertex++)
            {
                String temp="distance of source  " + source + " to "
                        + vertex + " is " + distances[vertex]+"\n";
                fw.write(temp);
            }
            fw.write("\n");
        }
        catch(Exception e)
        {
            System.out.println("Error: "+e);
        }
    }

    //function to get least distance from given source to all other vertices and write final results to results.txt 
    //https://www.sanfoundry.com/java-program-implement-bellmanford-algorithm/
    public void BellmanFordEvaluation(int source, int adjacencymatrix[][])
    {

        int distances[]=new int[numOfVertices+1]; //the array to store least cost path for all vertices
    	
        for (int node = 1; node <= numOfVertices; node++)
        {
            distances[node] = MAX_VALUE; //setting default to our max value, i.e infinity
        }
        
        //getting information from neighbors' and building the network topology graph
        distances[source] = 0;
        for (int node = 1; node <= numOfVertices - 1; node++)
        {
            for (int sourcenode = 1; sourcenode <= numOfVertices; sourcenode++)
            {
                for (int destinationnode = 1; destinationnode <= numOfVertices; destinationnode++)
                {
                    if (adjacencymatrix[sourcenode][destinationnode] != MAX_VALUE)
                    {
                        if (distances[destinationnode] > distances[sourcenode] 
                                + adjacencymatrix[sourcenode][destinationnode])
                            distances[destinationnode] = distances[sourcenode]
                                + adjacencymatrix[sourcenode][destinationnode];
                    }
                }
                //printDistances(distances);
            }
        }
 
        //calculate the cost from source to all other vertices
        for (int sourcenode = 1; sourcenode <= numOfVertices; sourcenode++)
        {
            for (int destinationnode = 1; destinationnode <= numOfVertices; destinationnode++)
            {
                if (adjacencymatrix[sourcenode][destinationnode] != MAX_VALUE)
                {
                    if (distances[destinationnode] > distances[sourcenode]
                           + adjacencymatrix[sourcenode][destinationnode])
                        System.out.println("The Graph contains negative egde cycle");
                }
            }
        }
 
        //writing results to  txt file from distances array        
        try
        {
            for (int vertex = 1; vertex <= numOfVertices; vertex++)
            {
                String temp="distance of source  " + source + " to "
                        + vertex + " is " + distances[vertex]+"\n";
                fw.write(temp);
            }
            fw.write("\n");
        }
        catch(Exception e)
        {
            System.out.println("Error: "+e);
        }
    }

    //function to open a file in a GUI manner using JFileChooser and return the absolute path of the file selected
    String openFile()
    {
        JButton open=new JButton();
        JFileChooser fc=new JFileChooser();
        fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
        fc.setDialogTitle("Choose a *.txt file");
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        if(fc.showOpenDialog(open)==JFileChooser.APPROVE_OPTION)
        {

        }
        return fc.getSelectedFile().getAbsolutePath();
    }

    //function to display the given filename in GUI with a working close button
    //http://www.java2s.com/Tutorials/Java/Swing_How_to/JFileChooser/Display_the_Contents_of_a_text_file_in_a_JTextArea.htm
    void showFileOnGUI(String filename)
    {
        frame = new JFrame("Network Topology");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JTextArea tarea = new JTextArea(25,40);

        JButton button = new JButton("Close");
        
        
        try
        {
        BufferedReader input = new BufferedReader(new InputStreamReader(
                new FileInputStream(filename)));
        tarea.read(input, "READING FILE :-)");
        }
        catch(Exception e)
        {
            System.out.println("Error: "+e);
        }          

        frame.getContentPane().add(tarea, BorderLayout.CENTER);
        frame.getContentPane().add(button, BorderLayout.PAGE_END);
        frame.pack();
        frame.setVisible(true);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        button.addActionListener(e -> {
        frame.dispose();
      });
        
    }

    //function to create n txt files to store distance vector tables for n vertices
    void createNodesFile(int numOfVertices)
    {
        for(int i=1;i<=numOfVertices;i++)
        {
            try
            {                    
                FileWriter fw=new FileWriter("Vertex"+i+".txt",false);
                fw.write("The distance vector table for node "+i+" is as follows: \n");
                fw.close();
                    
            }
            catch(Exception e)
            {
                System.out.println("Error: "+e);
            }

        }
        
    }

    //function to write distance vector table at every update to the specified nth vertex file
     void printDistances(int arr[])
     {
         try
         {
             
            for(int i=1;i<arr.length;i++)
            {
                fw1.write(arr[i]+" ");
            }
            fw1.write("\n");
            
        }
         catch(Exception e)
         {
             System.out.println("Error: "+e);
         }
     }   
    
    //function to display a file filename on GUI with a new JFrame with main title specified as title
    void showFileOnGUI2(String filename, JFrame frame, String title)
    {
        frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JTextArea tarea = new JTextArea(35, 35);

        //JButton button = new JButton("Close");
        
        
        try
        {
        BufferedReader input = new BufferedReader(new InputStreamReader(
                new FileInputStream(filename)));
        tarea.read(input, "READING FILE :-)");
        }
        catch(Exception e)
        {
            System.out.println("Error: "+e);
        }          

        frame.getContentPane().add(tarea, BorderLayout.CENTER);
        //frame.getContentPane().add(button, BorderLayout.PAGE_END);
        frame.pack();
        frame.setVisible(true);

        
        
    }

    //function to write the initial state distance vector table for every node to the file initial.txt
    void printInitialState(int adjacencyMatrix[][])
    {
        try
        {
            FileWriter fw=new FileWriter("initial.txt",false);
            for(int i=1;i<=numOfVertices;i++)
            {
                fw.write("Initial State for Node "+i+": ");
                for(int j=1;j<=numOfVertices;j++)
                {
                    fw.write(adjacencyMatrix[i][j]+" ");
                }
                fw.write("\n");
            }
            fw.close();
        }
        catch(Exception e)
        {
            System.out.println("Error: "+e);
        }
    }

    /*
     void printDistances(int arr[])
     {
         
            for(int i=0;i<arr.length;i++)
            {
                System.out.print(arr[i]+" ");
            }
            System.out.println();
            
     }*/

/*
    void showFileOnGUI3(String content, JFrame localframe)
    {
        localframe = new JFrame("Results");
        localframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        String displayContent=content;
        JLabel label1 = new JLabel(displayContent);

        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(800,600));
        panel.add(label1);

        localframe.getContentPane().add(panel);
        localframe.pack();
        localframe.setVisible(true);

    }
    
    String getStringfromFile(String filename)
    {
        String filecontent="";
        try 
        {
                FileReader reader = new FileReader(filename);
                BufferedReader bufferedReader = new BufferedReader(reader);
    
                String line;
    
                while ((line = bufferedReader.readLine()) != null)
                {
                    filecontent=filecontent+line+"\n";
                }
                reader.close();
        }
            catch (IOException e) 
        {
                e.printStackTrace();
        }

        return filecontent;
    }
    */   
}