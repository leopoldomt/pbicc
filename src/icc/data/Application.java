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
	public boolean hardwareAccelerated;
	public boolean hardwareAcceleratedWasSetted;	
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
	
	
	public void setDefaulthardwareAccelerated(int minSdkVersion, int targetSdkVersion) {
		if(minSdkVersion>=14 || targetSdkVersion>=14){
			hardwareAccelerated = true;
		} else {
			hardwareAccelerated = false;
		}
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
        builder.append(String.format("AllowTaskReparenting: %s\n", allowTaskReparenting));
        builder.append(String.format("AllowBackup: %s\n", allowBackup));
        builder.append(String.format("BackupAgent: %s\n", backupAgent));
        builder.append(String.format("Banner: %s\n", banner));
        builder.append(String.format("Debuggable: %s\n", debuggable));
        builder.append(String.format("Description: %s\n", description));
        builder.append(String.format("Enabled: %s\n", enabled));
        builder.append(String.format("HasCode: %s\n", hasCode));
        builder.append(String.format("HardwareAccelerated: %s\n", hardwareAccelerated));
        builder.append(String.format("Icon: %s\n", icon));
        builder.append(String.format("IsGame: %s\n", isGame));
        builder.append(String.format("KillAfterRestore: %s\n", killAfterRestore));
        builder.append(String.format("LargeHeap: %s\n", largeHeap));
        builder.append(String.format("Label: %s\n", label));
        builder.append(String.format("Logo: %s\n", logo));
        builder.append(String.format("ManageSpaceActivity: %s\n", manageSpaceActivity));
        builder.append(String.format("Name: %s\n", name));
        builder.append(String.format("Permission: %s\n", permission));
        builder.append(String.format("Persistent: %s\n", persistent));
        builder.append(String.format("Process: %s\n", process));
        builder.append(String.format("RestoreAnyVersion: %s\n", restoreAnyVersion));
        builder.append(String.format("RequiredAccountType: %s\n", requiredAccountType));
        builder.append(String.format("RestrictedAccountType: %s\n", restrictedAccountType));
        builder.append(String.format("SupportsRtl: %s\n", supportsRtl));
        builder.append(String.format("TaskAffinity: %s\n", taskAffinity));
        builder.append(String.format("TestOnly: %s\n", testOnly));
        builder.append(String.format("Theme: %s\n", theme));
        builder.append(String.format("UiOptions: %s\n", uiOptions));
        builder.append(String.format("UsesCleartextTraffic: %s\n", usesCleartextTraffic));
        builder.append(String.format("VmSafeMode: %s\n", vmSafeMode));
		
		return builder.toString();
	}
}
