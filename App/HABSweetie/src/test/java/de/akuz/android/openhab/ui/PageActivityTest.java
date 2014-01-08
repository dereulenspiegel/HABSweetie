package de.akuz.android.openhab.ui;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = "AndroidManifest.xml", qualifiers = "de-xhdpi", reportSdk = 10)
public class PageActivityTest {

	@Test
	@Ignore
	public void testPageActivityStart() throws Exception {
		// Ignored until problems with ABS and robolectric are solved
		PageActivity activity = Robolectric.buildActivity(PageActivity.class)
				.create().get();
		Assert.assertNotNull(activity.getObjectGraph());
		Assert.assertNotNull(activity);
	}

}
