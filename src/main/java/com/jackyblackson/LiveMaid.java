package com.jackyblackson;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LiveMaid implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger("LIVE MAID");

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info(
    			"""
       			\n
						 __        ________  __   __   ______             ___ __ __   ________    ________  ______     \s
						/_/\\      /_______/\\/_/\\ /_/\\ /_____/\\           /__//_//_/\\ /_______/\\  /_______/\\/_____/\\    \s
						\\:\\ \\     \\__.::._\\/\\:\\ \\\\ \\ \\\\::::_\\/_   _______\\::\\| \\| \\ \\\\::: _  \\ \\ \\__.::._\\/\\:::_ \\ \\   \s
						 \\:\\ \\       \\::\\ \\  \\:\\ \\\\ \\ \\\\:\\/___/\\ /______/\\\\:.      \\ \\\\::(_)  \\ \\   \\::\\ \\  \\:\\ \\ \\ \\  \s
						  \\:\\ \\____  _\\::\\ \\__\\:\\_/.:\\ \\\\::___\\/_\\__::::\\/ \\:.\\-/\\  \\ \\\\:: __  \\ \\  _\\::\\ \\__\\:\\ \\ \\ \\ \s
						   \\:\\/___/\\/__\\::\\__/\\\\ ..::/ / \\:\\____/\\          \\. \\  \\  \\ \\\\:.\\ \\  \\ \\/__\\::\\__/\\\\:\\/.:| |\s
						    \\_____\\/\\________\\/ \\___/_(   \\_____\\/           \\__\\/ \\__\\/ \\__\\/\\__\\/\\________\\/ \\____/_/\s
				""");
	}
}