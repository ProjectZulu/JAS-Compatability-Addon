package jas.compatability;

import jas.api.CompatibilityRegistrationEvent;
import jas.common.JASLog;
import jas.compatability.tf.TFLoadInfo;

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class ModuleLoader {
	private final List<LoadInfo> modulesInfo = new ArrayList<LoadInfo>();

	public ModuleLoader() {
		modulesInfo.add(new TFLoadInfo());
	}

	@SubscribeEvent
	public void CompatibilityRegistration(CompatibilityRegistrationEvent event) {
		for (LoadInfo moduleInfo : modulesInfo) {
			try {
				if (shouldLoadModule(moduleInfo)) {
					for (Object object : moduleInfo.getObjectsToRegister()) {
						event.loader.registerObject(object);
					}
				}
			} catch (Exception e) {
				JASLog.log().severe(
						"Failed to load JAS compatability module %s due to %s",
						moduleInfo.loaderID(), e.getMessage());
				e.printStackTrace();
			}
		}
	}

	private boolean shouldLoadModule(LoadInfo moduleInfo) {
		for (String modID : moduleInfo.getRequiredModIDs()) {
			if (!Loader.isModLoaded(modID)) {
				return false;
			}
		}
		return true;
	}
}