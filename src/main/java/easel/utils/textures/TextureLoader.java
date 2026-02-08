package easel.utils.textures;

public class TextureLoader
{
   public static <T extends Enum<T> & ITextureDatabaseEnum> void loadTextures(T[] src) {
     for (T x : src) {
       ((ITextureDatabaseEnum)x).load();
     }
   }

   public static <T extends Enum<T> & ITextureAtlasDatabaseEnum> void loadTextureAtlases(T[] src) {
     for (T x : src)
       ((ITextureAtlasDatabaseEnum)x).load();
   }
}
