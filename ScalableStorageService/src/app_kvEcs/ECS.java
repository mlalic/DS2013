package app_kvEcs;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;

import org.apache.log4j.Logger;

import common.metadata.MetaData;
import common.metadata.ServerNode;

public class ECS {
    private String configFile; 
    private HashSet<ServerNode> nodes;
    private MetaData metaData;
    private static Logger logger = Logger.getRootLogger();
    
    public ECS(String configFile){
        this.configFile = configFile;
    }
    
    /***
     * Parse the config file for this ECS and
     * builds initial metadata.
     * Nodes are line separated. 
     * Attributes are space separated
     * Sample Line : "nodeName ipAdd port"
     * @return true if Successfully Parsed
     * @return false if Unsuccessful 
     */
    public boolean buildInitialMetadata(){
        try {
            BufferedReader reader = new BufferedReader(
                    new FileReader(configFile));
            String line = " ";
            nodes = new HashSet<ServerNode>();
            metaData = new MetaData();
            while((line=reader.readLine())!=null){
                String[] parts = line.split(" ");
                if(parts.length!=3){
                    reader.close();
                    logger.error("Invalid File Format");
                    return false;
                }
                ServerNode node = new ServerNode(parts[0], parts[1],
                        Integer.parseInt(parts[2]));
                metaData.addServer(node);
                nodes.add(node);
            }
            reader.close();
            return true;
        } 
        catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
        catch (IOException e){
            e.printStackTrace();
            return false;
        }
    }
    
    /***
     * Returns the Node List
     * @return nodes
     */
    public HashSet<ServerNode> getNodes(){
        return nodes;
    }
    
    /***
     * Returns the MetaData
     * @return metaData
     */
    public MetaData getMetaData(){
        return metaData;
    }
}
