package app_kvClient.commands;

import client.KVCommInterface;

import java.io.PrintStream;

public class Context {
    private KVCommInterface session = null;
    private PrintStream outputStream = null;

    public KVCommInterface getSession() {
        return session;
    }

    public void setSession(KVCommInterface session) {
        this.session = session;
    }

    public PrintStream getOutputStream() {
        return outputStream;
    }

    public void setOutputStream(PrintStream outputStream) {
        this.outputStream = outputStream;
    }
}
