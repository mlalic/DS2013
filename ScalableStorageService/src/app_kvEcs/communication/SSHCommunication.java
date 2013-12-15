package app_kvEcs.communication;

import java.io.IOException;

public class SSHCommunication {
    public static boolean SSHDeploy(String addresses, String ports, String names){
        try {
            Runtime run = Runtime.getRuntime();
            run.exec("fab buildList:"+addresses+" buildPorts:"+ports+" buildNames:"
                    +names+" deploy");
            return true;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
    }

}
