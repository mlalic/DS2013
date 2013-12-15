package app_kvEcs.commands;

import java.io.PrintStream;
import java.util.ArrayList;

import app_kvEcs.ECS;
import app_kvEcs.communication.kvECSCommInterface;


public class Context {
    private ECS ecs = null;
    private PrintStream outputStream = null;
    private ArrayList<kvECSCommInterface> connections = null;
   
    public ArrayList<kvECSCommInterface> getConnections() {
        return connections;
    }

    public void setConnections(ArrayList<kvECSCommInterface> connections) {
        this.connections = connections;
    }

    public ECS getECS() {
        return ecs;
    }

    public void setECS(ECS ecs) {
        this.ecs = ecs;
    }

    public PrintStream getOutputStream() {
        return outputStream;
    }

    public void setOutputStream(PrintStream outputStream) {
        this.outputStream = outputStream;
    }

}
