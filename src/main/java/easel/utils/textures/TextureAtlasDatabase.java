package easel.utils.textures;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public enum TextureAtlasDatabase
   implements ITextureAtlasDatabaseEnum
{
   SMALL_HEADERED_TOOL_TIP("easel/textures/headeredToolTips/SmallHeadered.atlas"),
   LARGE_HEADERED_TOOL_TIP("easel/textures/headeredToolTips/LargeHeadered.atlas"),

   STYLED_CONTAINER("easel/textures/container/StyledContainerMessy.atlas"),
   NO_HEADERED_TOOL_TIP("easel/textures/headeredToolTips/NoHeadered.atlas");

   private final String internalPath;

   private TextureAtlas atlas;

   TextureAtlasDatabase(String internalPath) {
     this.internalPath = internalPath;
   }

   public void load() {
     this.atlas = new TextureAtlas(this.internalPath);
   }

   public TextureAtlas getTextureAtlas() {
     return this.atlas;
   }
}
