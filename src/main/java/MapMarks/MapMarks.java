//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package MapMarks;

import MapMarks.ui.LegendObject;
import MapMarks.ui.PaintContainer;
import MapMarks.ui.RadialMenu;
import MapMarks.utils.ColorEnum;
import MapMarks.utils.MapMarksTextureDatabase;
import MapMarks.utils.SoundHelper;
import basemod.BaseMod;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon.CurrentScreen;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.map.LegendItem;
import easel.ui.AnchorPosition;
import easel.utils.EaselInputHelper;
import easel.utils.EaselSoundHelper;
import easel.utils.textures.TextureLoader;
import java.util.ArrayList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SpireInitializer
public class MapMarks {
    public static final Logger logger = LogManager.getLogger(MapMarks.class);
    private RadialMenu menu;
    public static PaintContainer paintContainer;
    public static LegendObject legendObject;
    private boolean rightMouseDown = false;
    private int previouslySelectedIndex = -1;
    private RightMouseDownMode rightMouseDownMode;

    public static void initialize() {
        new MapMarks();
    }

    public MapMarks() {
        this.rightMouseDownMode = MapMarks.RightMouseDownMode.NONE;
    }

    public void receivePostInitialize() {
        TextureLoader.loadTextures(MapMarksTextureDatabase.values());
        MapMarksStorage.init();
        this.menu = new RadialMenu();
        legendObject = (LegendObject)((LegendObject)(new LegendObject()).onRightClick((var0) -> {
            EaselSoundHelper.uiClick2();
            if (EaselInputHelper.isAltPressed()) {
                paintContainer.clear();
            } else {
                MapTileManager.clearAllHighlights();
            }

        })).anchoredAt(1575.0F, 767.0F, AnchorPosition.CENTER);
        paintContainer = new PaintContainer();
        BaseMod.addSaveField("MapMarks", new MapMarksSaver());
    }

    public void receiveStartGame() {
        MapTileManager.clear();
        if (!CardCrawlGame.loadingSave) {
            MapMarksSaver.clearAllData();
            MapMarksStorage.delete();
        } else {
            ArrayList var1 = MapMarksStorage.load();
            if (var1 != null && !var1.isEmpty()) {
                System.out.println("MapMarks: 从 SpireConfig 发现备份数据，正在合并...");
                (new MapMarksSaver()).onLoad(var1);
            }
        }

    }

    public void receiveStartAct() {
    }

    public void receiveRender(SpriteBatch var1) {
        this.menu.render(var1);
    }

    public void receivePostUpdate() {
        if (CardCrawlGame.isInARun() && AbstractDungeon.screen == CurrentScreen.MAP) {
            MapTileManager.updateAllTracked();
            if (EaselInputHelper.isAltPressed()) {
                paintContainer.update();
                this.rightMouseDownMode = MapMarks.RightMouseDownMode.NONE;
                this.rightMouseDown = false;
                if (this.menu.isMenuOpen()) {
                    this.menu.close();
                }
            } else {
                if (InputHelper.isMouseDown_R && !this.rightMouseDown) {
                    this.rightMouseDown = true;
                    if (EaselInputHelper.isControlPressed()) {
                        if (MapTileManager.isAnyTileHovered()) {
                            MapTileManager.removeHighlightsFromUnreachableNodes();
                            MapTileManager.setHoveredTileHighlightStatus(true);
                            this.rightMouseDownMode = MapMarks.RightMouseDownMode.NONE;
                        }
                    } else if (MapTileManager.isAnyTileHovered()) {
                        if (MapTileManager.hoveredTileIsHighlighted()) {
                            if (MapTileManager.isARepaint()) {
                                this.rightMouseDownMode = MapMarks.RightMouseDownMode.HIGHLIGHTING;
                                MapTileManager.setHoveredTileHighlightStatus(true);
                            } else {
                                this.rightMouseDownMode = MapMarks.RightMouseDownMode.UNHIGHLIGHTING;
                                MapTileManager.setHoveredTileHighlightStatus(false);
                            }
                        } else {
                            this.rightMouseDownMode = MapMarks.RightMouseDownMode.HIGHLIGHTING;
                            MapTileManager.setHoveredTileHighlightStatus(true);
                        }
                    } else {
                        boolean var4 = !legendObject.isMouseInContentBounds();
                        if (CardCrawlGame.isInARun() && AbstractDungeon.dungeonMapScreen != null && AbstractDungeon.dungeonMapScreen.map != null && AbstractDungeon.dungeonMapScreen.map.legend != null) {
                            for(LegendItem var3 : AbstractDungeon.dungeonMapScreen.map.legend.items) {
                                if (var3.hb.hovered) {
                                    var4 = false;
                                    break;
                                }
                            }
                        }

                        if (var4) {
                            SoundHelper.playRadialOpenSound();
                            this.menu.open();
                            this.rightMouseDownMode = MapMarks.RightMouseDownMode.RADIAL_MENU;
                        }
                    }
                } else if (!InputHelper.isMouseDown_R && this.rightMouseDown) {
                    this.rightMouseDown = false;
                    this.rightMouseDownMode = MapMarks.RightMouseDownMode.NONE;
                    if (this.menu.isMenuOpen()) {
                        this.menu.close();
                        SoundHelper.playRadialCloseSound();
                        int var1 = this.menu.getSelectedIndex();
                        if (var1 != -1 && var1 != this.previouslySelectedIndex) {
                            ColorEnum var2 = this.menu.getSelectedColorOrDefault();
                            legendObject.setColor(var2);
                            MapTileManager.setHighlightingColor(var2.get());
                            this.previouslySelectedIndex = var1;
                        }
                    }
                } else if (this.rightMouseDownMode == MapMarks.RightMouseDownMode.HIGHLIGHTING) {
                    MapTileManager.setHoveredTileHighlightStatus(true);
                } else if (this.rightMouseDownMode == MapMarks.RightMouseDownMode.UNHIGHLIGHTING) {
                    MapTileManager.setHoveredTileHighlightStatus(false);
                }

                this.menu.update();
            }
        } else {
            this.rightMouseDownMode = MapMarks.RightMouseDownMode.NONE;
        }

    }

    public void receiveAddAudio() {
        BaseMod.addAudio("MAP_MARKS_CLICK", "MapMarks/output_2.wav");
    }

    private static enum RightMouseDownMode {
        RADIAL_MENU,
        HIGHLIGHTING,
        UNHIGHLIGHTING,
        NONE;

        private RightMouseDownMode() {
        }
    }
}
