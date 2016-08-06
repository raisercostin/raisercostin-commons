package org.raisercostin.utils;

import org.junit.Assert;
import org.junit.Test;

public class ObjectUtilsBug1Test {

	@Test
	public void test() {
		Assert.assertEquals("org.raisercostin.utils.ObjectUtilsBug1Test$MessageId\n.   id=<null>",new MessageId().toString());
	}

	public class MessageId {
		public final String id;

		/** Needed for com.fasterxml.jackson */
		protected MessageId() {
			id = null;
		}
		public MessageId(String id) {
			this.id = id;
		}

		@Override
		public String toString() {
			return ObjectUtils.toString(this);
		}
	}
}
