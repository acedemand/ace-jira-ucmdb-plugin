package com.acedemand.jira.plugin.service;

import com.acedemand.jira.plugin.api.Credentials;
import com.acedemand.jira.plugin.service.security.VaultService;
import com.hp.ucmdb.api.UcmdbService;
import com.hp.ucmdb.api.impact.*;
import com.hp.ucmdb.api.topology.*;
import com.hp.ucmdb.api.types.CI;
import com.hp.ucmdb.api.types.TopologyCI;
import com.hp.ucmdb.api.types.UcmdbId;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Pamir on 1/27/2017.
 */
public class UCMDBService {

    private String host;

    public UCMDBService(String host) {
        this.host = host;
    }

    public Collection<TopologyCI> findChangeItemsByName(UcmdbService ucmdbService, TopologyModificationData topologyModificationData, String name) {
        TopologyQueryService queryService = ucmdbService.getTopologyQueryService();

        TopologyQueryFactory topologyQueryFactory = queryService.getFactory();

        QueryDefinition queryDefinition = topologyQueryFactory.createQueryDefinition("Query Every CI Item");

        //property uzerinde sorgu yapacağın method
        //queryProperty cekmek istedigin kolonların isimleri

        queryDefinition.addNode("ciList").ofType("configuration_item").property("name", Operator.EQUALS_CASE_INSENSITIVE, name).queryProperty("jira_issue");
        Topology topology = queryService.executeQuery(queryDefinition);
        return topology.getCIsByName("ciList");
    }

    public void update(VaultService vaultService, String name, String issueKey) throws MalformedURLException {
        Credentials credentials = vaultService.findCredentials();
        UcmdbService ucmdbService = UCMDBConnectionFactory.createConnection(host, credentials.getUsername(), credentials.getPassword());
        TopologyUpdateService topologyUpdateService = ucmdbService.getTopologyUpdateService();
        TopologyUpdateFactory topologyUpdateFactory = topologyUpdateService.getFactory();

        TopologyModificationData topologyModificationData = topologyUpdateFactory.createTopologyModificationData();
        Collection<TopologyCI> ciCollection = findChangeItemsByName(ucmdbService, topologyModificationData, name);
        for (TopologyCI tci : ciCollection) {
            TopologyModificationData topologyModificationData1 = topologyUpdateFactory.createTopologyModificationData();
            TopologyCI topologyCI1 = tci;
            CI ci = topologyModificationData.addCI(topologyCI1.getId(), topologyCI1.getType());
            ci.setPropertyValue("jira_issue", issueKey);
            topologyUpdateService.create(topologyModificationData, CreateMode.UPDATE_EXISTING);
        }
    }

    public Collection<AffectedCI> findAffectedCIList(String ciName,VaultService vaultService) throws MalformedURLException {
        Credentials credentials = vaultService.findCredentials();
        UcmdbService ucmdbService = UCMDBConnectionFactory.createConnection(host, credentials.getUsername(), credentials.getPassword());
        TopologyUpdateService topologyUpdateService = ucmdbService.getTopologyUpdateService();
        TopologyUpdateFactory topologyUpdateFactory = topologyUpdateService.getFactory();
        TopologyModificationData topologyModificationData = topologyUpdateFactory.createTopologyModificationData();
        ImpactAnalysisService impactAnalysisService = ucmdbService.getImpactAnalysisService();
        ImpactAnalysisFactory impactFactory = impactAnalysisService.getFactory();
        ImpactAnalysisDefinition definition = impactFactory.createImpactAnalysisDefinition();
        definition.useAllRules();
        //definition.useRulesBundle();

        Collection<AffectedCI> topologyCICollection = new ArrayList<>();
        Collection<TopologyCI> minorTopologyCIList = findChangeItemsByName(ucmdbService,topologyModificationData,ciName);
        for(TopologyCI topologyCI : minorTopologyCIList) {
            definition.addTriggerCI(topologyCI.getId()).ofType("node").withSeverity(impactFactory.getSeverityByName("Critical"));
            //definition.addTriggerCI(ci2).ofType("nt").withSeverity(impactFactory.getSeverityByName("Critical"));
            // Execute impact analysis
            ImpactAnalysisResult impactResult = impactAnalysisService.analyze(definition);
            // Get Affected CIs
            topologyCICollection.addAll(impactResult.getAffectedCIs().getAllCIs());
        }
        return topologyCICollection;
    }
}