package com.superzanti.serversync;

import java.util.Arrays;
import java.util.List;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;

import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

import com.superzanti.lib.RefStrings;

@Mod(modid = RefStrings.MODID, name = RefStrings.NAME, version = RefStrings.VERSION)
public class ServerSyncRegistry {
	protected static Logger logger;
	protected static String SERVER_IP;
	protected static int SERVER_PORT;
	protected static int MINECRAFT_PORT;
	protected static String SECURE_RECURSIVE;
	protected static String SECURE_CHECKSUM;
	protected static String SECURE_UPDATE;
	protected static String SECURE_EXISTS;
	protected static String SECURE_EXIT;
	protected static List<String> IGNORE_LIST;
	protected static int BUTTON_ID;
	
	protected static final String CLIENT_PROXY = "com.superzanti.serversync.ClientProxy";
	protected static final String SERVER_PROXY = "com.superzanti.serversync.CommonProxy";
	@SidedProxy(modId = RefStrings.MODID, clientSide = CLIENT_PROXY, serverSide = SERVER_PROXY)
    protected static CommonProxy proxy;
	
	protected static SyncClient syncclient;
	
	@EventHandler
	public static void PreLoad(FMLPreInitializationEvent PreEvent) {
		
		//MinecraftForge.EVENT_BUS.register(new SyncClient());
		
		// setup the logger for the mod
		logger = PreEvent.getModLog();
		
		// Grab the configuration file and load in the values
		Configuration config = new Configuration(PreEvent.getSuggestedConfigurationFile());
		
		config.load();
		
		SERVER_IP = config.getString("SERVER_IP", "ServerConnection", "127.0.0.1", "The IP address of the server");
		SERVER_PORT = config.getInt("SERVER_PORT", "ServerConnection", 38067, 1, 49151, "The port that your server will be serving on");
		MINECRAFT_PORT = config.getInt("MINECRAFT_PORT", "ServerConnection", 25565, 1, 49151, "The port in which the minecraft server is running, not the serversync port");
		
		SECURE_RECURSIVE = config.getString("SECURE_RECURSIVE", "ServerEncryption", "f8e45531a3ea3d5c1247b004985175a4", "The recursive command security key phrase");
		SECURE_CHECKSUM = config.getString("SECURE_CHECKSUM", "ServerEncryption", "226190d94b21d1b0c7b1a42d855e419d", "The checksum command security key phrase");
		SECURE_UPDATE = config.getString("SECURE_UPDATE", "ServerEncryption", "3ac340832f29c11538fbe2d6f75e8bcc", "The update command security key phrase");
		SECURE_EXISTS = config.getString("SECURE_EXISTS", "ServerEncryption", "e087923eb5dd1310f5f25ddd5ae5b580", "The exists command security key phrase");
		SECURE_EXIT = config.getString("SECURE_EXIT", "ServerEncryption", "f24f62eeb789199b9b2e467df3b1876b", "The exit command security key phrase");
		
		String[] defaultList = {
		        "./mods/CustomMainMenu-MC1.7.10-1.5.jar",
		        "./config/CustomMainMenu/mainmenu.json",
		        "./config/serversync.cfg",
		        "./config/forge.cfg",
		        "./config/forgeChunkLoading.cfg",
		        "./config/splash.properties",
		        "./mods/serversync-2.0.jar"
		};
		String[] ignoreList = config.getStringList("IGNORE_LIST", "IgnoredFiles", defaultList,  "These files are ignored by serversync");
		IGNORE_LIST = Arrays.asList(ignoreList);
		
		
		BUTTON_ID = config.getInt("ButtonID", "GUI", 6001, 0, 2147483647, "The ID of the button that connects to the server and updates");

		// loading the configuration from its file
        config.save();
		
        // Client side
        if(proxy.isClient()){
        	logger.info("I am a client");
        	syncclient = new SyncClient();
        	MinecraftForge.EVENT_BUS.register(syncclient);
        	MinecraftForge.EVENT_BUS.register(new GuiScreenHandler());
        }
	
        //Server side
        if(proxy.isServer()){
        	logger.info("I am a server");
			SyncServer syncServer = new SyncServer();
			Thread syncThread = new Thread(syncServer);
			syncThread.start();
        }
        return;
	}
}