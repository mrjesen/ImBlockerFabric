package com.ddwhm.jesen.imblocker;


import net.fabricmc.api.ModInitializer;
import net.minecraft.SharedConstants;


public class ImBlocker implements ModInitializer {
	@Override
	public void onInitialize() {
		System.out.println("1");
		String os = System.getProperty("os.name");
		if(os.toLowerCase().startsWith("win")){
			IMManager.flag = 200;
		}else{
			IMManager.flag = 500;
		}
	}

}
