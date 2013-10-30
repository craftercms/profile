package org.craftercms.profile.impl.domain;

import java.util.ArrayList;

import org.craftercms.profile.impl.domain.Target;

import java.util.Arrays;

public class Subscriptions {
	
//	private static final String FREQUENCY = "instant";
//	private static final String ACTION = "email";
//	private static final String FORMAT = "single";
	
	private String frequency;
	private String action;
	private String format;
	private ArrayList<Target> subscription;
	
	public Subscriptions() {
		this.subscription =  new ArrayList<Target>();
//		this.frequency = FREQUENCY;
//		this.format = FORMAT;
//		this.action = ACTION;
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

	
//	public void setSubscription(Target[] subscription) {
//		if (subscription !=null) {
//			this.subscription = (ArrayList<Target>) Arrays.asList(subscription);
//		}
//		
//	}

}
