package com.pancentric.bromham.actions;

import com.pancentric.bromham.BromhamStepDefinitions;
import com.pancentric.utilities.PropertyFetcher;
import io.restassured.response.Response;
import io.restassured.path.xml.element.Node;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import static io.restassured.RestAssured.given;

public class JPBMActions {

    private PropertyFetcher propertyfile = new PropertyFetcher();
    private String propfile;

    // checks a workflow of a certain type is created for a policy
    public String verifyWorkflowForPolicyByType(String policyId, String workflowType, int timeout, int interval) throws Exception {
        long end = System.currentTimeMillis() + (timeout * 1000);
        int j;
        String workflowId = "";
        String foundWorkflow = "";
        // wait until timeout for workflow to appear
        workflow_search:
        while (System.currentTimeMillis() < end && (foundWorkflow.contains(workflowType)==false)) {
            Response instances = getWorkflowsForPolicy(policyId);
            List<Node> workflowNodes = instances.xmlPath().get("**.findAll { it.name() == 'process-instance-log' }");
            j = workflowNodes.size();
            if (j > 0) {
                // loop the workflows
                inner_loop:
                for(int i=0; i<j; i++) {
                    workflowId = workflowNodes.get(i).get("process-instance-id").toString();
                    foundWorkflow = workflowNodes.get(i).children().get("process-id").toString();
                    if (foundWorkflow.contains(workflowType)) {
                        break inner_loop;
                    }
                }
            }
            Thread.sleep(interval * 1000);
        }
        if (foundWorkflow.contains(workflowType)==false) {
            throw new Exception("Worfklow for policy "+policyId+" not found within " + timeout + " seconds");
        }
        else {
            System.out.println("Found workflow instance of: " + foundWorkflow);
        }
        return workflowId;
    }

    // checks a workflow of a certain type is created for a policy
    public List<String> getAllWorkflowsForPolicy(String policyId) throws Exception {

        // wait until timeout for workflow to appear
        List<String> workflows = new ArrayList<>();
        Response instances = getWorkflowsForPolicy(policyId);
        List<Node> workflowNodes = instances.xmlPath().get("**.findAll { it.name() == 'process-instance-log' }");
        int j = workflowNodes.size();
            if (j > 0) {
                // loop the workflows
                for(int i=0; i<j; i++) {
                    workflows.add(workflowNodes.get(i).children().get("process-id").toString() + "("+ workflowNodes.get(i).children().get("process-instance-id") + ")");
                }
            }
        return workflows;
    }

    // to check workflows for a policy
    public void verifyStartWorkflowForPolicy(String policyId, String memberName, int timeout, int inverval) throws Exception {

        String workflowId = verifyWorkflowForPolicyByType(policyId,"Policy_Start_Date_Workflow",timeout,inverval);
        Response actual = getJpbmActual(workflowId);
        HashMap<String,String> memberValuesMap = new HashMap<>();
        List<Node> attributeNodes = actual.xmlPath().get("**.findAll { it.name() == 'variable-instance-log' }");
        int j = attributeNodes.size();
        if (j > 0) {
            // loop the workflows
            for(int i=0; i<j; i++) {
                memberValuesMap.put(attributeNodes.get(i).children().get("variable-id").toString(),attributeNodes.get(i).children().get("value").toString());
            }
        }
        assert(memberValuesMap.containsValue(memberName)) : memberName + " not found. Found " + memberValuesMap.values();
        assert(memberValuesMap.containsValue(policyId)) : policyId + " not found. Found " + memberValuesMap.values();
    }

    private Response getJpbmInstances() throws Exception {
        return restCall("/jbpm-console/rest/history/instances");
    }

    private Response getJpbmActual(String id) throws Exception {
        return restCall("/jbpm-console/rest/history/instance/"+id+"/variable");
    }

    private Response getWorkflowsForPolicy(String id) throws Exception {
        return restCall("/jbpm-console/rest/history/variable/policyId/value/"+id+"/instances");
    }

    private Response getWorkflowsOfType(String workflowName) throws Exception {
        return restCall("/jbpm-console/rest/history/process/bfs.base." + workflowName);
    }

    // for making a REST JSON request
    private Response restCall(String requestUrl) throws Exception {
        propfile = BromhamStepDefinitions.projectName + "/properties/"+ BromhamStepDefinitions.environment;
        String username = propertyfile.getPropValues(propfile,"jbpmUsername");
        String password = propertyfile.getPropValues(propfile, "jbpmPassword");
        String url = propertyfile.getPropValues(propfile,"ouServiceUrl");
        return given().auth().preemptive().basic(username, password).when().get(url+requestUrl);
    }
}