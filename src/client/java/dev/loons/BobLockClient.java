package dev.loons;

import net.fabricmc.api.ClientModInitializer;

public class BobLockClient implements ClientModInitializer {
	public static boolean isRenderingHand = false;

	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as
		// rendering.
		org.slf4j.LoggerFactory.getLogger("bob-lock").info("Hello from BobLockClient!");
	}
}