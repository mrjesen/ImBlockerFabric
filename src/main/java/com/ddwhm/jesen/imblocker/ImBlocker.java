package com.ddwhm.jesen.imblocker;


import net.fabricmc.api.ModInitializer;


public class ImBlocker implements ModInitializer {

	@Override
	public void onInitialize() {
		String os = System.getProperty("os.name");
		if(os.toLowerCase().startsWith("win")){
			IMManager.flag = 200;
		}else{
			IMManager.flag = 500;
		}
	}
}
