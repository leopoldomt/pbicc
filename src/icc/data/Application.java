package icc.data;

public class Application {
    public final String NOT_SET = "#NOT SET#";
	public boolean allowTaskReparenting = false;
	public boolean allowBackup = true;
    public String backupAgent = NOT_SET;
    public String banner = NOT_SET;
    public boolean debuggable = false;
    public String description = NOT_SET;
    public boolean enabled = true;
	public boolean hasCode = true;

	//TODO special case
	public boolean hardwareAccelerated;
	
	public String icon = NOT_SET;
    public boolean isGame = false;
	public boolean killAfterRestore = true;
	public boolean largeHeap = false;
	public String label = NOT_SET;
	public String logo = NOT_SET;
	public String manageSpaceActivity = NOT_SET;
	public String name = NOT_SET;
	public String permission = NOT_SET;
    public boolean persistent = false;
    
    //TODO special case
    public String process;
    
    public boolean restoreAnyVersion = false;
    public String requiredAccountType = NOT_SET;
    public String restrictedAccountType = NOT_SET;
    public boolean supportsRtl = false;
    
    public String taskAffinity = NOT_SET;
    
    public boolean testOnly = false;
    public String theme = NOT_SET;
    public String uiOptions = NOT_SET;
    public boolean usesCleartextTraffic = true;
	public boolean vmSafeMode = false;
}
