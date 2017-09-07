package com.acedemand.jira.plugin.servlet;

import com.acedemand.jira.plugin.api.Contants;
import com.acedemand.jira.plugin.service.UCMDBService;
import com.acedemand.jira.plugin.service.security.InCodeUsernamePasswordService;
import com.acedemand.jira.plugin.service.security.VaultService;
import com.atlassian.jira.component.ComponentAccessor;
import com.hp.ucmdb.api.impact.AffectedCI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;

public class ImpactAnalysisServlet extends HttpServlet{
    private static final Logger log = LoggerFactory.getLogger(ImpactAnalysisServlet.class);

    private static final VaultService VAULT_SERVICE = new InCodeUsernamePasswordService();
    private static final UCMDBService UCMDB_SERVICE = new UCMDBService(Contants.UCMDB_HOST);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {

        resp.setContentType("text/html");
        resp.getWriter().write("<html><body>Hello World</body></html>");
        String issueKey = req.getParameter("issuekey");
        String ciName =
                ComponentAccessor.getCustomFieldManager().getCustomFieldObjectByName(Contants.CF_CI_NAME).getValueFromIssue(ComponentAccessor.getIssueManager().getIssueByKeyIgnoreCase(issueKey));

        Collection<AffectedCI> affectedCICollection = UCMDB_SERVICE.findAffectedCIList(ciName,VAULT_SERVICE);
        for (AffectedCI affectedCI : affectedCICollection){
            resp.getWriter().write(String.format("</p> %s",affectedCI.getProperty("name").getValue().toString()));

        }
    }

}