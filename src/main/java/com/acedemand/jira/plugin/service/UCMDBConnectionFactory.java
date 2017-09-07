package com.acedemand.jira.plugin.service;

import com.hp.ucmdb.api.*;
import sun.misc.UCDecoder;

import java.net.MalformedURLException;

/**
 * Created by Pamir on 1/27/2017.
 */
public class UCMDBConnectionFactory {

    private static final String PROTOCOL = "http";
    private static final int PORT = 8080;

    public static UcmdbService createConnection(String address, String username, String password) throws MalformedURLException {
        UcmdbServiceProvider serviceProvider = UcmdbServiceFactory.getServiceProvider(PROTOCOL,address,PORT);
        ClientContext clientContext = serviceProvider.createClientContext("jira-adaptor");

        Credentials credentials = serviceProvider.createCredentials(username,password);
        return serviceProvider.connect(credentials,clientContext);
    }
}
