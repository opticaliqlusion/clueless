package edu.jhu.epioneers.clueless.communication;

/***
 * Request class for adding a log to the server
 */
public class AddLogRequest {
    /***
     * Content of the log to add
     */
    private String logContent;

    public String getLogContent() {
        return logContent;
    }

    public void setLogContent(String logContent) {
        this.logContent = logContent;
    }
}
