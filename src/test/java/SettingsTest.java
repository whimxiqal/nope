import com.minecraftonline.nope.control.Setting;
import com.minecraftonline.nope.control.Settings;
import org.junit.Test;

import static org.junit.jupiter.api.Assertions.fail;

public class SettingsTest {
	/**
	 * Check that the settings are unique, no registration for the same id.
	 */
	@Test
	public void checkUnique() {
		try {
			Settings.load();
			// BEEP BOOP BEEP
		} catch (Settings.SettingNotUniqueException e) {
			fail(e.getMessage());
		}
	}
}
