package app_kvEcs.communication;

import java.io.IOException;

public class SSHCommunication {
    public static boolean SSHDeploy(String addresses, String ports, String names){
        try {
            String buildString = "";
            String[] namesList = names.split(",");
            String[] portsList = ports.split(",");
            String[] addressList = addresses.split(",");
            for(int i=0; i < namesList.length; i++){
                buildString += namesList[i];
                buildString += "/";
                buildString += addressList[i];
                buildString += "/";
                buildString += portsList[i];
                buildString += ",";
            }
            buildString = buildString.substring(0,buildString.length()-1);
            Runtime run = Runtime.getRuntime();
            run.exec("fab buildList:" + buildString + " deploy");
            return true;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
    }

}
