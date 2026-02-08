package easel;

import easel.utils.EaselFonts;
import easel.utils.textures.TextureAtlasDatabase;
import easel.utils.textures.TextureDatabase;
import easel.utils.textures.TextureLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Easel
{
   public static final Logger logger = LogManager.getLogger(Easel.class);

   public static void initialize() {
     new Easel();
   }

   public void receivePostInitialize() {
     TextureLoader.loadTextures(TextureDatabase.values());
     logger.info("TextureManager: loaded " + (TextureDatabase.values()).length + " textures.");
     TextureLoader.loadTextureAtlases(TextureAtlasDatabase.values());
     logger.info("TextureManager: loaded " + (TextureAtlasDatabase.values()).length + " texture atlases.");
     EaselFonts.loadFonts();
   }
}
