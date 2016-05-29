	package tg.parse;
	
	public class DataURI {
	
		String type;
		String dataString;
	
		public DataURI(String dataString) {
			this.dataString = dataString;
		}
		
		public void setType(String type) {
			this.type = type;
		}
		
		public String getType() {
			return type;
		}
	
		private int findSchemeSeparator() {
			return dataString.indexOf(':');
		}
	
		private int findFragmentSeparator() {
			return dataString.indexOf('#', findSchemeSeparator());
		}
	
		public boolean isHierarchical() {
			int ssi = findSchemeSeparator();
	
			if (ssi == -1) {
				// All relative URIs are hierarchical.
				return true;
			}
	
			if (dataString.length() == ssi + 1) {
				// No ssp.
				return false;
			}
	
			// If the ssp starts with a '/', this is hierarchical.
			return dataString.charAt(ssi + 1) == '/';
		}	
	
		public boolean isOpaque() {
			return !isHierarchical();
		}
	
		public boolean isRelative() {
			// Note: We return true if the index is 0
			return findSchemeSeparator() == -1;
		}
	
		public String getScheme() {
			return parseScheme();
		}
	
		private String parseScheme() {
			int ssi = findSchemeSeparator();
			return ssi == -1 ? null : dataString.substring(0, ssi);
		}
	
		public String getHost() {
			return parseHost();
		}
	
		private String parseHost() {
			String authority = getAuthority();
			if (authority == null) {
				return null;
			}
	
			// Parse out user info and then port.
			int userInfoSeparator = authority.indexOf('@');
			int portSeparator = authority.indexOf(':', userInfoSeparator);
	
			return portSeparator == -1
					? authority.substring(userInfoSeparator + 1)
							: authority.substring(userInfoSeparator + 1, portSeparator);
	
		}
	
		public int getPort() {
			return parsePort();
		}
	
		private int parsePort() {
			String authority = getAuthority();
			if (authority == null) {
				return -1;
			}
	
			// Make sure we look for the port separtor *after* the user info
			// separator. We have URLs with a ':' in the user info.
			int userInfoSeparator = authority.indexOf('@');
			int portSeparator = authority.indexOf(':', userInfoSeparator);
	
			if (portSeparator == -1) {
				return -1;
			}
	
			String portString = authority.substring(portSeparator + 1);
			try {
				return Integer.parseInt(portString);
			} catch (NumberFormatException e) {
				return -1;
			}
		}
	
		public String getSsp() {
			return parseSsp(); 
		}
	
		private String parseSsp() {
			int ssi = findSchemeSeparator();
			int fsi = findFragmentSeparator();
	
			// Return everything between ssi and fsi.
			return fsi == -1
					? dataString.substring(ssi + 1)
							: dataString.substring(ssi + 1, fsi);
		}
	
	
		public String getAuthority() {
			return parseAuthority(this.dataString, findSchemeSeparator());
		}
	
		private String parseAuthority(String uriString, int ssi) {
			int length = uriString.length();
	
			// If "//" follows the scheme separator, we have an authority.
			if (length > ssi + 2
					&& uriString.charAt(ssi + 1) == '/'
					&& uriString.charAt(ssi + 2) == '/') {
				// We have an authority.
	
				// Look for the start of the path, query, or fragment, or the
				// end of the string.
				int end = ssi + 3;
				LOOP: while (end < length) {
					switch (uriString.charAt(end)) {
					case '/': // Start of path
					case '?': // Start of query
					case '#': // Start of fragment
						break LOOP;
					}
					end++;
				}
	
				return uriString.substring(ssi + 3, end);
			} else {
				return null;
			}
		}
	
		public String getPath() {
			return parsePath();
		}
	
		private String parsePath() {
			String uriString = this.dataString;
			int ssi = findSchemeSeparator();
	
			// If the URI is absolute.
			if (ssi > -1) {
				// Is there anything after the ':'?
				boolean schemeOnly = ssi + 1 == uriString.length();
				if (schemeOnly) {
					// Opaque URI.
					return null;
				}
	
				// A '/' after the ':' means this is hierarchical.
				if (uriString.charAt(ssi + 1) != '/') {
					// Opaque URI.
					return null;
				}
			} else {
				// All relative URIs are hierarchical.
			}
	
			return parsePath(uriString, ssi);
		}
		private String parsePath(String uriString, int ssi) {
			int length = uriString.length();
	
			// Find start of path.
			int pathStart;
			if (length > ssi + 2
					&& uriString.charAt(ssi + 1) == '/'
					&& uriString.charAt(ssi + 2) == '/') {
				// Skip over authority to path.
				pathStart = ssi + 3;
				LOOP: while (pathStart < length) {
					switch (uriString.charAt(pathStart)) {
					case '?': // Start of query
					case '#': // Start of fragment
						return ""; // Empty path.
					case '/': // Start of path!
						break LOOP;
					}
					pathStart++;
				}
			} else {
				// Path starts immediately after scheme separator.
				pathStart = ssi + 1;
			}
	
			// Find end of path.
			int pathEnd = pathStart;
			LOOP: while (pathEnd < length) {
				switch (uriString.charAt(pathEnd)) {
				case '?': // Start of query
				case '#': // Start of fragment
					break LOOP;
				}
				pathEnd++;
			}
	
			return uriString.substring(pathStart, pathEnd);
		}
	
		private String parseQuery() {
			// It doesn't make sense to cache this index. We only ever
			// calculate it once.
			int qsi = dataString.indexOf('?', findSchemeSeparator());
			if (qsi == -1) {
				return null;
			}
	
			int fsi = findFragmentSeparator();
	
			if (fsi == -1) {
				return dataString.substring(qsi + 1);
			}
	
			if (fsi < qsi) {
				// Invalid.
				return null;
			}
	
			return dataString.substring(qsi + 1, fsi);
		}
	
		public String getQuery() {
			return parseQuery();
		}
	
		private String parseFragment() {
			int fsi = findFragmentSeparator();
			return fsi == -1 ? null : dataString.substring(fsi + 1);
		}
	
	
		public String getFragment() {
			return parseFragment();
		}
	
		public String toString() {
			return dataString;
		}
	
	}
