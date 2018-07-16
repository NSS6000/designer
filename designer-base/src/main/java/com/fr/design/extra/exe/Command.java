package com.fr.design.extra.exe;

import com.fr.design.extra.Process;

/**
 * Created by richie on 16/3/19.
 */
public interface Command {

    String getExecuteMessage();

    void run(Process<String> process);
}