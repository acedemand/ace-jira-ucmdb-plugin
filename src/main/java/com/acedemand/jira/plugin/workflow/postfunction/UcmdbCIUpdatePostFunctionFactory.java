package com.acedemand.jira.plugin.workflow.postfunction;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.plugin.workflow.AbstractWorkflowPluginFactory;
import com.atlassian.jira.plugin.workflow.WorkflowPluginFunctionFactory;
import com.atlassian.jira.workflow.JiraWorkflow;
import com.atlassian.jira.workflow.WorkflowManager;
import com.opensymphony.workflow.loader.*;
import webwork.action.ActionContext;

import java.util.*;

/**
 * This is the factory class responsible for dealing with the UI for the post-function.
 * This is typically where you put default values into the velocity context and where you store user input.
 */

public class UcmdbCIUpdatePostFunctionFactory extends AbstractWorkflowPluginFactory implements WorkflowPluginFunctionFactory
{
    /*package internal*/ static final String FIELD_NAME = "field";
    private static final String FIELDS = "fields";
    private static final String NOT_DEFINED = "Not Defined";

    private WorkflowManager workflowManager;

    private final CustomFieldManager customFieldManager;

    public UcmdbCIUpdatePostFunctionFactory() {
        this.customFieldManager = ComponentAccessor.getCustomFieldManager();
    }

    @Override
    protected void getVelocityParamsForInput(Map<String, Object> velocityParams) {
        velocityParams.put(FIELDS, getCFFields());

    }

    @Override
    protected void getVelocityParamsForEdit(Map<String, Object> velocityParams, AbstractDescriptor descriptor) {
        getVelocityParamsForInput(velocityParams);
        getVelocityParamsForView(velocityParams, descriptor);
    }

    @Override
    protected void getVelocityParamsForView(Map<String, Object> velocityParams, AbstractDescriptor descriptor) {
        if (!(descriptor instanceof FunctionDescriptor)) {
            throw new IllegalArgumentException("Descriptor must be a FunctionDescriptor.");
        }
        velocityParams.put(FIELD_NAME, getFieldName(descriptor,FIELD_NAME));
    }


    public Map<String,?> getDescriptorParams(Map<String, Object> formParams) {
        Map<String,String> descriptorParamMap = new HashMap<>();
        if (formParams != null && formParams.containsKey(FIELD_NAME)) {
            descriptorParamMap.put(FIELD_NAME, extractSingleParam(formParams, FIELD_NAME));
        }

        return descriptorParamMap;
    }

    private String getFieldName(AbstractDescriptor descriptor,String fieldName) {
        if (!(descriptor instanceof FunctionDescriptor)) {
            throw new IllegalArgumentException("Descriptor must be a FunctionDescriptor.");
        }

        FunctionDescriptor functionDescriptor = (FunctionDescriptor) descriptor;

        String field = (String) functionDescriptor.getArgs().get(fieldName);
        if (field != null && field.trim().length() > 0) {
            return field;
        }
        else {
            return NOT_DEFINED;
        }
    }

    private Collection<CustomField> getCFFields() {
        return customFieldManager.getCustomFieldObjects();
    }

}