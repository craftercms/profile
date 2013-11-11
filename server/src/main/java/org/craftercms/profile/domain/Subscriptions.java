package org.craftercms.profile.domain;

import java.util.ArrayList;

public class Subscriptions {
	
	private static final String FREQUENCY = "instant";
	private static final String ACTION = "email";
	private static final String FORMAT = "single";
	private static final boolean AUTOWATCH_DEFAULT = false;
	
	private String frequency;
	private String action;
	private String format;
	private boolean autoWath;
	private ArrayList<Target> subscription;
	
	public Subscriptions() {
		this.subscription =  new ArrayList<Target>();
		this.frequency = FREQUENCY;
		this.format = FORMAT;
		this.action = ACTION;
		autoWath = AUTOWATCH_DEFAULT;
	}
	
	
	public String getFrequency() {
		return frequency;
	}
	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getFormat() {
		return format;
	}
	public void setFormat(String format) {
		this.format = format;
	}
	public ArrayList<Target> getSubscription() {
		return subscription;
	}
	public void setSubscription(ArrayList<Target> subscription) {
		this.subscription = subscription;
	}
	
	public void addSubscription(Target target) {
		this.subscription.add(target);
	}
	
	public void removeSubscription(Target target) {
		this.subscription.remove(target);
	}
	
	public Target getByTargetId(String targetId) {
		Target result = null;
		if (this.subscription == null || this.subscription.size() == 0 || targetId==null) {
			return result;
		}
		for(Target t: this.subscription) {
			if (t.getTargetId()!=null && targetId.equals(t.getTargetId())) {
				result = t;
				break;
			}
		}
		return result;
	}

	public boolean isAutoWath() {
		return autoWath;
	}

	public void setAutoWath(boolean autoWath) {
		this.autoWath = autoWath;
	}

}
