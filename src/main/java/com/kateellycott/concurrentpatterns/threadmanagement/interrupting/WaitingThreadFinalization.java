package com.kateellycott.concurrentpatterns.threadmanagement.interrupting;

import java.util.Date;
import java.util.concurrent.TimeUnit;

class DataSourceLoader implements Runnable {
    @Override
    public void run() {
        System.out.println("Beginning data sources loading: " + new Date());

        try {
            TimeUnit.SECONDS.sleep(4);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Data sources loading has finished: " + new Date());
    }
}

class NetworkConnectionsLoader implements Runnable {
    @Override
    public void run() {
        System.out.println("Beginning network connections loading: " + new Date());

        try {
            TimeUnit.SECONDS.sleep(6);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Network connections loading has finished: " + new Date());
    }
}

public class WaitingThreadFinalization {
    public static void main(String[] args) {
        DataSourceLoader dataSourceLoader = new DataSourceLoader();
        NetworkConnectionsLoader networkConnectionsLoader = new NetworkConnectionsLoader();

        Thread loadData = new Thread(dataSourceLoader, "DataSourceThread");
        Thread loadNetwork = new Thread(networkConnectionsLoader, "NetworkConnectionLoader");

        loadData.start();
        loadNetwork.start();

        try {
            loadData.join();
            loadNetwork.join();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Main: The configuration has been loaded: " + new Date());
    }
}
