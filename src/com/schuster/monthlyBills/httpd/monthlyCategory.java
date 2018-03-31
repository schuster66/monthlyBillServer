package com.schuster.monthlyBills.httpd;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class monthlyCategory {
	
	public static ArrayList<monthlyCategory> categoriesInOrder = new ArrayList<monthlyCategory>();
	public static ConcurrentHashMap<String,monthlyCategory> categories = new ConcurrentHashMap<String, monthlyCategory>();
	
	private final String key;
	private final String displayName;
	private final int position;
	private final String startDate;
	private final String endDate;
	private final boolean firstHalf;
	private final boolean inTotal;
	
	private monthlyCategory(String key, String displayName, int position, String startDate, String endDate, boolean firstHalf, boolean inTotal) {
		this.key = key;
		this.displayName = displayName;
		this.position = position;
		this.startDate = startDate;
		this.endDate = endDate;
		this.firstHalf = firstHalf;
		this.inTotal = inTotal;
		
		this.putInArrayList();
		categories.put(key, this);
		
	}
	
	public String key() {
		return key;
	}

	public String displayName() {
		return displayName;
	}

	public int position() {
		return position;
	}

	public String startDate() {
		return startDate;
	}

	public String endDate() {
		return endDate;
	}
	
	public boolean firstHalf() {
		return this.firstHalf;
	}
	
	public boolean inTotal() {
		return this.inTotal;
	}

	private void putInArrayList() {
		int i = 0;
		if (categoriesInOrder.size() == 0 ) {
			categoriesInOrder.add(this);
		} else {
			while (i < categoriesInOrder.size()) {
				//System.out.println("I = " + i + " - Position = " + position + " - key = " + key + " array size = " + templatesInOrder.size());
				if (this.position < categoriesInOrder.get(i).position) {
					categoriesInOrder.add(i, this);
					break;
				}
				if (i >= (categoriesInOrder.size() - 1)) {
					categoriesInOrder.add(this);
					break;
				}
				i++;
			}
		}
	}
	
	public static synchronized void  buildNewCategory(String key, String displayName, int position, String startDate, 
														String endDate, boolean firstHalf, boolean inTotal) {
		if (categories.containsKey(key)) {
			// don't build duplicate The only way to update is to restart application
		} else {
			monthlyCategory mp = new monthlyCategory(key, displayName, position, startDate, endDate, firstHalf, inTotal);
		}
		
	}
	
	public static ArrayList<monthlyCategory> getCategoriesInOrder() {
		return categoriesInOrder;
		
	}

}
