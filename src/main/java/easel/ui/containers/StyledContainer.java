package easel.ui.containers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.megacrit.cardcrawl.core.Settings;
import easel.ui.AbstractWidget;
import easel.ui.AnchorPosition;
import easel.ui.InterpolationSpeed;
import easel.ui.graphics.ninepatch.NinePatchWidget;
import easel.ui.layouts.VerticalLayout;
import easel.ui.text.Label;
import easel.utils.EaselFonts;
import easel.utils.colors.EaselColors;
import easel.utils.textures.TextureAtlasDatabase;
import easel.utils.textures.TextureDatabase;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StyledContainer
   extends AbstractWidget<StyledContainer>
{
   private float width;
   private float height;
   private boolean hasHeader = false;
   private boolean hasCustomHeader = false;
   private AnchorPosition headerAnchor = AnchorPosition.CENTER;
   private AnchorPosition contentAnchor = AnchorPosition.CENTER;

   private static final TextureAtlas atlas = TextureAtlasDatabase.STYLED_CONTAINER.getTextureAtlas();

   private NinePatchWidget npFullShadow;

   private NinePatchWidget npFullBase;

   private NinePatchWidget npFullTrim;

   private NinePatchWidget npFullTrimHighlight;
   private NinePatchWidget npHeaderBase;
   private NinePatchWidget npHeaderTrim;
   private VerticalLayout defaultHeader;
   private AbstractWidget customHeader;
   private AbstractWidget content;
   private Color baseColor = EaselColors.TOOLTIP_BASE();
   private Color trimColor = EaselColors.TOOLTIP_TRIM();
   private Color trimHighlightColor = EaselColors.TOOLTIP_TRIM_HIGHLIGHT();
   private Color headerColor = EaselColors.HEADER_BLUE();

   private boolean renderFullShadows = false;

   private static final float SHADOW_OFFSET_X = 7.0F;

   private static final float SHADOW_OFFSET_Y = 7.0F;

   private static final float OUTER_TRIM_SIZE = 4.0F;
   private static final float SHADOW_SIZE = 4.0F;
   private static final Texture SHADOW_TEXTURE = TextureDatabase.BLACK_GRADIENT_VERTICAL.getTexture();

   public StyledContainer(float width, float height) {
     this
       .npFullShadow = (new NinePatchWidget(width, height, (TextureRegion)atlas.findRegion("shadow"))).withColor(Settings.QUARTER_TRANSPARENT_WHITE_COLOR);

     this
       .npFullBase = (new NinePatchWidget(width, height, (TextureRegion)atlas.findRegion("base"))).withColor(this.baseColor);

     this
       .npFullTrim = (new NinePatchWidget(width, height, (TextureRegion)atlas.findRegion("trim"))).withColor(this.trimColor);

     this
       .npFullTrimHighlight = (new NinePatchWidget(width, height, (TextureRegion)atlas.findRegion("trim_highlight"))).withColor(this.trimHighlightColor);

     this.width = width;
     this.height = height;
   }

   private void constructHeaderNP() {
     AbstractWidget headerContents = this.hasCustomHeader ? this.customHeader : (AbstractWidget)this.defaultHeader;

     this
       .npHeaderBase = (new NinePatchWidget(this.width, headerContents.getHeight(), (TextureRegion)atlas.findRegion("header_base"))).withColor(this.headerColor);

     this
       .npHeaderTrim = (new NinePatchWidget(this.width, headerContents.getHeight(), (TextureRegion)atlas.findRegion("header_trim"))).withColor(this.trimColor);
   }

   public StyledContainer withHeader(String title) {
     this.hasHeader = true;
     this.hasCustomHeader = false;

     this

       .defaultHeader = ((VerticalLayout)((VerticalLayout)((VerticalLayout)(new VerticalLayout(this.width, 0.0F)).withMargins(40.0F, 20.0F)).withDefaultChildAnchorPosition(this.headerAnchor)).withChild((AbstractWidget)new Label(title, EaselFonts.SMALLER_TIP_BODY, Settings.CREAM_COLOR))).scaleToWidestChild();

     constructHeaderNP();

     return this;
   }

   public StyledContainer withHeader(String title, String subtitle) {
     this.hasHeader = true;
     this.hasCustomHeader = false;

     this

       .defaultHeader = ((VerticalLayout)((VerticalLayout)((VerticalLayout)((VerticalLayout)(new VerticalLayout(this.width, 0.0F)).withMargins(40.0F, 20.0F)).withDefaultChildAnchorPosition(this.headerAnchor)).withChild((AbstractWidget)new Label(title, EaselFonts.SMALLER_TIP_BODY, Settings.CREAM_COLOR))).withChild((AbstractWidget)new Label(subtitle, EaselFonts.MEDIUM_ITALIC, Color.GRAY))).scaleToWidestChild();

     constructHeaderNP();

     return this;
   }

   public StyledContainer withHeader(AbstractWidget customHeader, boolean autoAddMargins) {
     this.hasHeader = true;
     this.hasCustomHeader = true;

     this.customHeader = customHeader;

     if (autoAddMargins) {
       this.customHeader.withMargins(40.0F, 20.0F);
     }
     constructHeaderNP();

     return this;
   }

   public StyledContainer withHeaderColor(Color headerColor) {
     this.headerColor = headerColor;

     if (this.hasHeader) {
       this.npHeaderBase.withColor(headerColor);
     }

     return this;
   }

   public StyledContainer withBaseColor(Color baseColor) {
     this.baseColor = baseColor;
     this.npFullBase.withColor(baseColor);
     return this;
   }

   public StyledContainer withTrimColors(Color trimColor, Color trimHighlightColor) {
     this.trimColor = trimColor;
     this.trimHighlightColor = trimHighlightColor;

     this.npFullTrim.withColor(trimColor);
     this.npFullTrimHighlight.withColor(trimHighlightColor);

     if (this.hasHeader) {
       this.npHeaderTrim.withColor(trimColor);
     }
     return this;
   }

   public StyledContainer withHeaderAnchor(AnchorPosition headerAnchor) {
     this.headerAnchor = headerAnchor;

     if (this.hasHeader && !this.hasCustomHeader) {
       this.defaultHeader.forceChildAnchors(headerAnchor);
     }

     return this;
   }

   public StyledContainer withContentAnchor(AnchorPosition contentAnchor) {
     this.contentAnchor = contentAnchor;
     return this;
   }

   public StyledContainer withShadows(boolean enabled) {
     this.renderFullShadows = enabled;
     return this;
   }

   public StyledContainer withContent(AbstractWidget content, boolean autoAddMargins) {
     this.content = content;

     if (autoAddMargins) {
       this.content.withMargins(40.0F);
     }
     return this;
   }

   public StyledContainer withWidth(float newWidth) {
     this.npFullShadow.withWidth(newWidth);
     this.npFullBase.withWidth(newWidth);
     this.npFullTrim.withWidth(newWidth);
     this.npFullTrimHighlight.withWidth(newWidth);

     if (this.hasHeader) {
       this.npHeaderBase.withWidth(newWidth);
       this.npHeaderTrim.withWidth(newWidth);
     }

     this.width = newWidth;
     scaleHitboxToContent();

     return this;
   }

   public StyledContainer withHeight(float newHeight) {
     this.npFullShadow.withHeight(newHeight);
     this.npFullBase.withHeight(newHeight);
     this.npFullTrim.withHeight(newHeight);
     this.npFullTrimHighlight.withHeight(newHeight);

     this.height = newHeight;
     scaleHitboxToContent();

     return this;
   }

   public StyledContainer withDimensions(float newWidth, float newHeight) {
     withWidth(newWidth);
     return withHeight(newHeight);
   }

   public StyledContainer scaleToContentWidth() {
     if (this.content == null) {
       return this;
     }
     float newWidth = this.content.getWidth();

     if (this.hasHeader) {
       if (this.hasCustomHeader) {
         newWidth = Math.max(newWidth, this.customHeader.getWidth());
       } else {
         newWidth = Math.max(newWidth, this.defaultHeader.getWidth());
       }
     }
     return withWidth(newWidth);
   }

   public StyledContainer scaleToContentHeight() {
     if (this.content == null) {
       return this;
     }
     return withHeight(this.content.getHeight() + getHeaderHeight());
   }

   public StyledContainer scaleToContent() {
     scaleToContentWidth();
     return scaleToContentHeight();
   }

   public static void syncContainerWidths(StyledContainer... containers) {
     syncContainerWidths(false, containers);
   }

   public static void syncContainerWidths(Stream<StyledContainer> containers) {
     syncContainerWidths(false, containers);
   }

   public static void syncContainerWidths(List<StyledContainer> containers) {
     syncContainerWidths(false, containers);
   }

   public static void syncContainerWidths(boolean scaleToContentFirst, StyledContainer... containers) {
     syncContainerWidths(scaleToContentFirst, Stream.of(containers));
   }

   public static void syncContainerWidths(boolean scaleToContentFirst, Stream<StyledContainer> containers) {
     syncContainerWidths(scaleToContentFirst, containers.collect(Collectors.toList()));
   }

   public static void syncContainerWidths(boolean scaleToContentFirst, List<StyledContainer> containers) {
     if (scaleToContentFirst) {
       containers.forEach(StyledContainer::scaleToContent);
     }

     float maxWidth = ((Float)containers.stream().map(container -> Float.valueOf(container.width)).max(Float::compareTo).orElse(Float.valueOf(0.0F))).floatValue();

     containers.forEach(container -> container.withWidth(maxWidth));
   }

   public static void syncContainerHeights(StyledContainer... containers) {
     syncContainerHeights(false, containers);
   }

   public static void syncContainerHeights(Stream<StyledContainer> containers) {
     syncContainerHeights(false, containers);
   }

   public static void syncContainerHeights(List<StyledContainer> containers) {
     syncContainerHeights(false, containers);
   }

   public static void syncContainerHeights(boolean scaleToContentFirst, StyledContainer... containers) {
     syncContainerHeights(scaleToContentFirst, Stream.of(containers));
   }

   public static void syncContainerHeights(boolean scaleToContentFirst, Stream<StyledContainer> containers) {
     syncContainerHeights(scaleToContentFirst, containers.collect(Collectors.toList()));
   }

   public static void syncContainerHeights(boolean scaleToContentFirst, List<StyledContainer> containers) {
     if (scaleToContentFirst) {
       containers.forEach(StyledContainer::scaleToContent);
     }

     float maxHeight = ((Float)containers.stream().map(container -> Float.valueOf(container.height)).max(Float::compareTo).orElse(Float.valueOf(0.0F))).floatValue();

     containers.forEach(container -> container.withHeight(maxHeight));
   }

   public float getContentWidth() {
     return this.width; } public float getContentHeight() {
     return this.height;
   }

   private float getHeaderHeight() {
     return this.hasHeader ? this.npHeaderBase.getHeight() : 0.0F;
   }

   private float getMainContentAreaHeight() {
     return getContentHeight() - getHeaderHeight();
   }

   public StyledContainer anchoredAt(float x, float y, AnchorPosition anchorPosition, InterpolationSpeed movementSpeed) {
     super.anchoredAt(x, y, anchorPosition, movementSpeed);

     this.npFullShadow.anchoredAt(getContentLeft() + 7.0F, getContentTop() - 7.0F, AnchorPosition.LEFT_TOP, movementSpeed);
     this.npFullBase.anchoredAt(getContentLeft(), getContentTop(), AnchorPosition.LEFT_TOP, movementSpeed);
     this.npFullTrim.anchoredAt(getContentLeft(), getContentTop(), AnchorPosition.LEFT_TOP, movementSpeed);
     this.npFullTrimHighlight.anchoredAt(getContentLeft(), getContentTop(), AnchorPosition.LEFT_TOP, movementSpeed);

     if (this.hasHeader) {
       this.npHeaderBase.anchoredAt(getContentLeft(), getContentTop(), AnchorPosition.LEFT_TOP, movementSpeed);
       this.npHeaderTrim.anchoredAt(getContentLeft(), getContentTop(), AnchorPosition.LEFT_TOP, movementSpeed);

       float hx = this.headerAnchor.getXFromLeft(getContentLeft(), getContentWidth());
       float hy = this.headerAnchor.getYFromTop(getContentTop(), getHeaderHeight());

       if (this.hasCustomHeader) {
         this.customHeader.anchoredAt(hx, hy, this.headerAnchor, movementSpeed);
       } else {
         this.defaultHeader.anchoredAt(hx, hy, this.headerAnchor, movementSpeed);
       }
     }

     if (this.content != null) {
       float cx = this.contentAnchor.getXFromLeft(getContentLeft(), getContentWidth());
       float cy = this.contentAnchor.getYFromBottom(getContentBottom(), getMainContentAreaHeight());

       this.content.anchoredAt(cx, cy, this.contentAnchor, movementSpeed);
     }

     return this;
   }

   protected void cancelMovementQueueForAllChildren(boolean shouldTryAndResolveOneLastTime) {
     Stream.<NinePatchWidget>of(new NinePatchWidget[] { this.npFullBase, this.npFullShadow, this.npFullTrim, this.npFullTrimHighlight
         }).forEach(elt -> elt.cancelMovementQueue(shouldTryAndResolveOneLastTime));
     if (this.hasHeader) {
       this.npHeaderBase.cancelMovementQueue(shouldTryAndResolveOneLastTime);
       this.npHeaderTrim.cancelMovementQueue(shouldTryAndResolveOneLastTime);

       if (this.hasCustomHeader) {
         this.customHeader.cancelMovementQueue(shouldTryAndResolveOneLastTime);
       } else {
         this.defaultHeader.cancelMovementQueue(shouldTryAndResolveOneLastTime);
       }
     }
     if (this.content != null) {
       this.content.cancelMovementQueue(shouldTryAndResolveOneLastTime);
     }
   }

   protected void setChildrenDelayedMovement(float deltaX, float deltaY, InterpolationSpeed movementSpeed, long startingTimeMillis) {
     Stream.<NinePatchWidget>of(new NinePatchWidget[] { this.npFullBase, this.npFullShadow, this.npFullTrim, this.npFullTrimHighlight
         }).forEach(elt -> elt.setAllDelayedMovement(deltaX, deltaY, movementSpeed, startingTimeMillis));

     if (this.hasHeader) {
       this.npHeaderBase.setAllDelayedMovement(deltaX, deltaY, movementSpeed, startingTimeMillis);
       this.npHeaderTrim.setAllDelayedMovement(deltaX, deltaY, movementSpeed, startingTimeMillis);

       if (this.hasCustomHeader) {
         this.customHeader.setAllDelayedMovement(deltaX, deltaY, movementSpeed, startingTimeMillis);
       } else {
         this.defaultHeader.setAllDelayedMovement(deltaX, deltaY, movementSpeed, startingTimeMillis);
       }
     }
     if (this.content != null) {
       this.content.setAllDelayedMovement(deltaX, deltaY, movementSpeed, startingTimeMillis);
     }
   }

   protected void updateWidget() {
     super.updateWidget();

     if (this.hasHeader && this.hasCustomHeader) {
       this.customHeader.update();
     }
     if (this.content != null) {
       this.content.update();
     }
   }

   protected void renderWidget(SpriteBatch sb) {
     if (this.renderFullShadows) {
       this.npFullShadow.render(sb);
     }
     this.npFullBase.render(sb);

     if (this.hasHeader) {
       this.npHeaderBase.render(sb);

       if (this.hasCustomHeader) {
         this.customHeader.render(sb);
       } else {
         this.defaultHeader.render(sb);
       }
       this.npHeaderTrim.render(sb);
     }

     if (this.content != null) {
       this.content.render(sb);
     }
     if (this.hasHeader) {

       float left = (getContentLeft() + 4.0F) * Settings.xScale;
       float bottom = (getContentTop() - getHeaderHeight() - 4.0F) * Settings.yScale;
       float width = (getContentWidth() - 8.0F) * Settings.xScale;
       float height = 4.0F;

       sb.setColor(EaselColors.HALF_TRANSPARENT_WHITE);
       sb.draw(SHADOW_TEXTURE, left, bottom, width, height);
     }

     this.npFullTrim.render(sb);
     this.npFullTrimHighlight.render(sb);
   }
}
