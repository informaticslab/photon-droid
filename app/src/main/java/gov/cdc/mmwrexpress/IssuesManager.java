package gov.cdc.mmwrexpress;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jtq6 on 10/29/14.
 */
public class IssuesManager {

    Map<String,Issue> issues;
    Issue[] sortedIssues;
    Keyword[] keywords;
    boolean hasIssues;

    public IssuesManager() {

        this.issues = new HashMap<String, Issue>();

    }

}
