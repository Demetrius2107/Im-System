package com.lip.reciver.process;

/**
 * @author: Elon
 * @title: ProcessFactory
 * @projectName: IM-System
 * @description:
 * @date: 2025/3/5 1:39
 */
public class ProcessFactory {

    private static BaseProcess defaultProcess;

    static {
        defaultProcess = new BaseProcess() {
            @Override
            public void processBefore() {

            }

            @Override
            public void processAfter() {

            }
        };
    }

    public static BaseProcess getMessageProcess(Integer command){
        return defaultProcess;
    }

}
