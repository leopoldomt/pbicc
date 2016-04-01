package icc.data;

public class IntentStats {
	static class Stat {
		public String name;
		public int value;

		public Stat(String name) {
			this.name = name;
			this.value = 0;
		}
	}

	public Stat intentCount = new Stat("Intents");
	public Stat explicitIntents = new Stat("Intents (Explicit)");
	public Stat implicitIntents = new Stat("Intents (Implicit)");
	public Stat iccLinks = new Stat("ICC Links");
	public Stat explicitICCLinks = new Stat("ICC links (using explicit intents)");
	public Stat implicitICCLinks = new Stat("ICC links (using implicit intents)");
	public Stat startActivityCount = new Stat("'startActivity' calls");
	public Stat startServiceCount = new Stat("'startService' calls");
	public Stat bindServiceCount = new Stat("'bindService' calls");
	public Stat sendBroadcastCount = new Stat("'sendBroadcast' calls");
	public Stat providerCount = new Stat("ContentProvider-related calls");

	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(String.format("%s: %d\n", intentCount.name, intentCount.value));
		builder.append(String.format("%s: %d\n", explicitIntents.name, explicitIntents.value));
		builder.append(String.format("%s: %d\n", implicitIntents.name, implicitIntents.value));
		builder.append(String.format("%s: %d\n", iccLinks.name, iccLinks.value));
		builder.append(String.format("%s: %d\n", explicitICCLinks.name, explicitICCLinks.value));
		builder.append(String.format("%s: %d\n", implicitICCLinks.name, implicitICCLinks.value));
		builder.append(String.format("%s: %d\n", startActivityCount.name, startActivityCount.value));
		builder.append(String.format("%s: %d\n", startServiceCount.name, startServiceCount.value));
		builder.append(String.format("%s: %d\n", sendBroadcastCount.name, sendBroadcastCount.value));
		builder.append(String.format("%s: %d\n", providerCount.name, providerCount.value));
		return builder.toString();
	}

	public void addStartActivity() {
		this.startActivityCount.value += 1;
	}

	public void addStartService() {
		this.startServiceCount.value += 1;
	}

	public void addBindService() {
		this.bindServiceCount.value += 1;
	}

	public void addSendBroadcast() {
		this.sendBroadcastCount.value += 1;
	}

	public void addProvider() {
		this.providerCount.value += 1;
	}

	public void add(IntentStats otherStats) {
		this.intentCount.value += otherStats.intentCount.value;
		this.explicitIntents.value += otherStats.explicitIntents.value;
		this.implicitIntents.value += otherStats.implicitIntents.value;
		this.iccLinks.value += otherStats.iccLinks.value;
		this.explicitICCLinks.value += otherStats.explicitICCLinks.value;
		this.implicitICCLinks.value += otherStats.implicitICCLinks.value;
		this.startActivityCount.value += otherStats.startActivityCount.value;
		this.startServiceCount.value += otherStats.startServiceCount.value;
		this.sendBroadcastCount.value += otherStats.sendBroadcastCount.value;
		this.providerCount.value += otherStats.providerCount.value;
	}

	public String getExtendedAnalysis() {
		StringBuilder builder = new StringBuilder();
		builder.append(String.format("%.2f%% of the intents were explicit\n", (explicitIntents.value * 100.0) / intentCount.value));
		builder.append(String.format("%.2f%% of the links were caused by explicit intents\n", (explicitICCLinks.value * 100.0) / iccLinks.value));
		return builder.toString();
	}
}
