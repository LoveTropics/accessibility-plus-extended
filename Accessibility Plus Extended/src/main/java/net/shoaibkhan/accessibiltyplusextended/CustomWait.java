package net.shoaibkhan.accessibiltyplusextended;

import java.util.Map;

public class CustomWait extends Thread {
	private boolean running = false;

	public void run() {
		while (running) {
			try {
				if (!AccessibilityPlusExt.mainThreadMap.isEmpty()) {
					Map.Entry<String, Integer> toRemove = null;
					for (Map.Entry<String, Integer> entry : AccessibilityPlusExt.mainThreadMap.entrySet()) {
						entry.setValue(entry.getValue() - 1);
						if (entry.getValue() <= 10) 
							toRemove = entry;
					}

					if(toRemove != null)
						AccessibilityPlusExt.mainThreadMap.remove(toRemove.getKey());
				}
				Thread.sleep(1);
			} catch (Exception e) {
			}
		}
	}

	public void stopThread() {
		running = false;
		interrupt();
	}

	public void startThread() {
		running = true;
		this.start();
	}
}
