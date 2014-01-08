package de.akuz.android.openhab.ui;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class PageActivityTest {

	@Test
	public void testPageActivityStart() throws Exception {
		PageActivity activity = Robolectric.buildActivity(PageActivity.class)
				.create().get();
		Assert.assertNotNull(activity.getObjectGraph());
		Assert.assertNotNull(activity);
	}

}
