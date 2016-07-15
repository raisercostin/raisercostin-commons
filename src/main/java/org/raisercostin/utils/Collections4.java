package org.raisercostin.utils;

import java.util.List;

import com.google.common.base.Preconditions;

public class Collections4 {
	public static <T> T unique(List<T> all) {
		Preconditions.checkArgument(all.size()==1,"The collection should have one and only one element but has "+all.size());
		return all.get(0);
	}

	public static <T> void empty(List<T> all, String message) {
		Preconditions.checkArgument(all.size()==0,message+" The collection should be empty but has "+all.size());
		//return all.get(0);
	}
}
