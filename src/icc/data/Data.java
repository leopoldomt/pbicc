package icc.data;

import java.util.ArrayList;

public class Data
  {
  
	private ArrayList<String> schemes = new ArrayList<String>();
    private ArrayList<String> hosts = new ArrayList<String>();
    private ArrayList<String> ports = new ArrayList<String>();
    private ArrayList<String> paths = new ArrayList<String>();
    private ArrayList<String> pathPatterns = new ArrayList<String>();
    private ArrayList<String> pathPrefixs = new ArrayList<String>();
    private ArrayList<String> mimeTypes = new ArrayList<String>();
   
    public boolean hasGenericMimeTypePrefix = false;
    
    public ArrayList<String> getSchemes() {
		return schemes;
	}

	public ArrayList<String> getHosts() {
		return hosts;
	}

	public ArrayList<String> getPorts() {
		return ports;
	}

	public ArrayList<String> getPaths() {
		return paths;
	}

	public ArrayList<String> getPathPatterns() {
		return pathPatterns;
	}

	public ArrayList<String> getPathPrefixs() {
		return pathPrefixs;
	}

	public ArrayList<String> getMimeTypes() {
		return mimeTypes;
	}
    
    private String toRowString(ArrayList<String> strings) {
    	String rowString = strings.toString();
    	System.out.println(rowString);
    	return rowString;
    }
    
    public void addScheme(String scheme){
    	if(!schemes.contains(scheme)){
    		schemes.add(scheme);
    	}
    }
    
    public void addHost(String host){
    	if(!hosts.contains(host)){
    		hosts.add(host);
    	}
    }
    
    public void addPort(String port){
    	if(!ports.contains(port)){
    		ports.add(port);
    	}
    }
    
    public void addPath(String path){
    	if(!paths.contains(path)){
    		paths.add(path);
    	}
    }
    
    public void addPathPattern(String pathPattern){
    	if(!pathPatterns.contains(pathPattern)){
    		pathPatterns.add(pathPattern);
    	}
    }
    
    public void addPathPrefix(String pathPrefix){
    	if(!pathPrefixs.contains(pathPrefix)){
    		pathPrefixs.add(pathPrefix);
    	}
    }
    
    public void addMimeType(String mimeType){
    	if(!mimeTypes.contains(mimeType)){
    		mimeTypes.add(mimeType);
    		if(mimeType.startsWith("*/")) hasGenericMimeTypePrefix = true;
    	}
    }
    
    public ArrayList<String> getPrefixesOfMimeTypeWithGenericSufix(){
    	ArrayList<String> prefixes = new ArrayList<String>();
    	
    	String mimeType;
    	String posfix;
    	int slash; 
    	for(int i=0; i < mimeTypes.size(); i++) {
    		mimeType = mimeTypes.get(i);
    		 slash = mimeType.indexOf("/");
    		posfix = mimeType.substring(slash);
    		if(posfix.equals("*")){
    			prefixes.add(mimeType.substring(0, slash));
    		}
    	}
    	
    	return prefixes;
    }
    

	public ArrayList<String> getMimeTypeSufixes() {
		ArrayList<String> sufixes = new ArrayList<String>();
		
		String mimeType;
		int slash;
		for(int i=0; i < mimeTypes.size(); i++) {
			mimeType = mimeTypes.get(i);
			slash = mimeType.indexOf("/");
    		sufixes.add(mimeType.substring(slash));
		}
		return sufixes;
	}
    
    public String toString()
    {
      StringBuilder builder = new StringBuilder();
      builder.append(String.format("%s: %s\n", "schemes", toRowString(schemes)));
      builder.append(String.format("%s: %s\n", "host", toRowString(hosts)));
      builder.append(String.format("%s: %s\n", "port", toRowString(ports)));
      builder.append(String.format("%s: %s\n", "path", toRowString(paths)));
      builder.append(String.format("%s: %s\n", "pathPattern", toRowString(pathPatterns)));
      builder.append(String.format("%s: %s\n", "pathPrefix", toRowString(pathPrefixs)));
      builder.append(String.format("%s: %s\n", "mimeType", toRowString(mimeTypes)));
      
      return builder.toString();
    }

  }