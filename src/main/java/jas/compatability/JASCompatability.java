package jas.compatability;

import jas.api.CompatibilityRegistrationEvent;
import jas.common.JASLog;
import jas.compatability.tf.TFLoadInfo;

import java.util.ArrayList;
import java.util.List;

import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = CompatibilityProps.MODID, name = CompatibilityProps.MODNAME, useMetadata = true)
public class JASCompatability {

	@Instance(CompatibilityProps.MODID)
	public static JASCompatability modInstance;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(new ModuleLoader());
	}
}
