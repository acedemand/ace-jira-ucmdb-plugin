package com.acedemand.jira.plugin.service.security;

import com.hp.ucmdb.api.Credentials;

/**
 * Created by Pamir on 1/27/2017.
 */
public interface VaultService {
    com.acedemand.jira.plugin.api.Credentials findCredentials();
}
