package duber.game.client.match;

import duber.engine.Cleansable;
import duber.engine.Window;
import duber.engine.entities.components.Named;
import duber.engine.exceptions.LWJGLException;
import duber.engine.utilities.Utils;
import duber.game.User;
import duber.game.gameobjects.Gun;
import duber.game.gameobjects.Player.PlayerData;

import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.system.MemoryUtil;

import static org.lwjgl.system.MemoryUtil.NULL;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import org.joml.Vector4f;

import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_CENTER;
import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_MIDDLE;
import static org.lwjgl.nanovg.NanoVG.nvgBeginFrame;
import static org.lwjgl.nanovg.NanoVG.nvgCreateFontMem;
import static org.lwjgl.nanovg.NanoVG.nvgBeginPath;
import static org.lwjgl.nanovg.NanoVG.nvgEndFrame;
import static org.lwjgl.nanovg.NanoVG.nvgRect;
import static org.lwjgl.nanovg.NanoVG.nvgFillColor;
import static org.lwjgl.nanovg.NanoVG.nvgFill;
import static org.lwjgl.nanovg.NanoVG.nvgFontSize;
import static org.lwjgl.nanovg.NanoVG.nvgFontFace;
import static org.lwjgl.nanovg.NanoVG.nvgText;
import static org.lwjgl.nanovg.NanoVG.nvgTextBounds;
import static org.lwjgl.nanovg.NanoVG.nvgTextAlign;

import static org.lwjgl.nanovg.NanoVGGL3.NVG_ANTIALIAS;
import static org.lwjgl.nanovg.NanoVGGL3.NVG_STENCIL_STROKES;
import static org.lwjgl.nanovg.NanoVGGL3.nvgCreate;
import static org.lwjgl.nanovg.NanoVGGL3.nvgDelete;

/**
 * The HUD used during the a match.
 * @author Darren Lo
 * @version 1.0
 */
public class HUD implements Cleansable {   
    /** White colour. */
    public static final Vector4f WHITE = new Vector4f(255, 255, 255, 255);
    
    /** Red colour. */
    public static final Vector4f RED = new Vector4f(255, 0, 0, 255);

    /** Translucent black colour. */
    public static final Vector4f TRANSLUCENT_BLACK = new Vector4f(0, 0, 0, 100);

    /** The main font of the HUD. */
    public static final String MAIN_FONT_ID = "OpenSans";
    
    /** The font used for the title. */
    public static final Font TITLE_FONT = new Font(MAIN_FONT_ID, 50.0f, WHITE);

    /** The font used for labels. */
    public static final Font LABEL_FONT = new Font(MAIN_FONT_ID, 30.0f, WHITE);

    /** Center align for text. */
    public static final int CENTER_ALIGN = NVG_ALIGN_CENTER | NVG_ALIGN_MIDDLE;
    
    /** The window the HUD is rendered on. */
    private Window window;

    /** A counter to only start nvg once. */
    private long displayCounter = 0;
    
    /** A map of fonts to their buffers. */
    Map<String, ByteBuffer> fonts = new HashMap<>();

    /** The nano vg context. */
    private long vgContext;

    /** The current colour being used. */
    private NVGColor colour;

    /** 
     * Constructs a HUD
     * @param window the Window to draw on
     * @throws LWJGLException if the HUD could not be created
     * @throws IOException if a font could not be loaded
     */
    public HUD(Window window) throws LWJGLException, IOException {
        colour = NVGColor.create();
        this.window = window;

        vgContext = window.optionIsTurnedOn(Window.Options.ANTI_ALIASING) ? 
            nvgCreate(NVG_ANTIALIAS | NVG_STENCIL_STROKES) :
            nvgCreate(NVG_STENCIL_STROKES);
            
        if (vgContext == NULL) {
            throw new LWJGLException("Could not create a HUD with NanoVG");
        }

        createFont(MAIN_FONT_ID, "/fonts/OpenSans-Regular.ttf", 150*1024);
    }

    /**
     * Creates a font.
     * @param fontName the name of the font
     * @param fontFile the file storing font data
     * @param bufferSize the buffer size for the font
     * @throws LWJGLException if the font could not be created
     * @throws IOException if the font file could not be loaded
     */
    private void createFont(String fontName, String fontFile, int bufferSize) throws IOException, LWJGLException {
        ByteBuffer fontBuffer = Utils.ioResourceToByteBuffer(fontFile, bufferSize);
        fonts.put(fontName, fontBuffer);
        int font = nvgCreateFontMem(vgContext, fontName, fontBuffer, 0);
        if (font == -1) {
            throw new LWJGLException("Unable to create font");
        }
    }

    /**
     * Displays the match HUD.
     * @param match the match
     */
    public void displayMatchHud(Match match) {
        displayInit();
        User currUser = match.getGame().getUser();
        Crosshair crosshair = currUser.getCrosshair();
        displayCrosshair(crosshair, window.getWidth()/2, window.getHeight()/2);

        displayGun(match.getMainPlayer().getWeaponsInventory().getEquippedGun());
        displayPlayerData(match.getMainPlayer().getPlayerData());

        displayEnd();
    }

    /**
     * Displays information about a gun.
     * @param equippedGun the equipped gun
     */
    private void displayGun(Gun equippedGun) {
        String gunType;
        String bulletsRemaining;
        if (equippedGun == null) {
            gunType = "No gun equipped";
            bulletsRemaining = "N/A";
        } else {
            gunType = equippedGun.getComponent(Named.class).getName();
            bulletsRemaining = String.valueOf(equippedGun.getGunData().getRemainingBullets());
        }
        
        String gunText = "Gun: " + gunType;
        displayTextWithBackground(gunText, 0.1f, 0.8f, true, LABEL_FONT, 10f, TRANSLUCENT_BLACK);
        String bulletCounterText = "Bullets remaining: " + bulletsRemaining;
        displayTextWithBackground(bulletCounterText, 0.1f, 0.85f, true, LABEL_FONT, 10f, TRANSLUCENT_BLACK);
    }

    /**
     * Displays information about a player.
     * @param playerData the player data to display
     */
    private void displayPlayerData(PlayerData playerData) {
        String healthText = "HP: " + playerData.getHealth();
        String moneyText = "$" + playerData.getMoney();
        displayTextWithBackground(healthText, 0.1f, 0.9f, true, LABEL_FONT, 10f, TRANSLUCENT_BLACK);
        displayTextWithBackground(moneyText, 0.1f, 0.95f, true, LABEL_FONT, 10f, TRANSLUCENT_BLACK);
    }

    /**
     * Called before displaying HUD.
     */
    private void displayInit() {
        if (displayCounter++ == 0) {
            nvgBeginFrame(vgContext, window.getWidth(), window.getHeight(), 1);
        } 
    }

    /**
     * Called after displaying HUD.
     */
    private void displayEnd() {
        if (--displayCounter == 0) {
            nvgEndFrame(vgContext);
            window.restoreState();
        }
    }

    /**
     * Displays the crosshair
     * @param crosshair the crosshair
     * @param posX the x position of the crosshair
     * @param posY the y position of the crosshair
     */
    private void displayCrosshair(Crosshair crosshair, int posX, int posY) {
        displayInit();

        float crosshairHorizontalStart = posX - (float) crosshair.getWidth()/2;
        float crosshairVerticalStart = posY - (float)crosshair.getHeight()/2;
        float lineThickness = crosshair.getThickness();
        
        nvgBeginPath(vgContext);
        nvgRect(vgContext, crosshairHorizontalStart, posY - lineThickness/2, crosshair.getWidth(), lineThickness);
        nvgFillColor(vgContext, setColour(crosshair.getColour()));
        nvgFill(vgContext);

        nvgBeginPath(vgContext);
        nvgRect(vgContext, posX - lineThickness/2, crosshairVerticalStart, lineThickness, crosshair.getHeight());
        nvgFillColor(vgContext, setColour(crosshair.getColour()));
        nvgFill(vgContext);
        
        displayEnd();
    }

    /**
     * Sets the active font.
     * @param font the font to use
     */
    private void setFont(Font font) {
        nvgFontSize(vgContext, font.size);
        nvgFontFace(vgContext, font.id);
        nvgFillColor(vgContext, setColour(font.colour));
        nvgTextAlign(vgContext, CENTER_ALIGN);
    }

    /**
     * Sets the active colour.
     * @param r the red component
     * @param g the green component
     * @param b the blue component
     * @param a the alpha component
     * @return the NVGColor being used
     */
    private NVGColor setColour(float r, float g, float b, float a) {
        colour.r(r / 255.0f);
        colour.g(g / 255.0f);
        colour.b(b / 255.0f);
        colour.a(a / 255.0f);
        return colour;
    }

    /**
     * Sets the active colour.
     * @param colour the colour
     * @return the NVGColor being used
     */
    private NVGColor setColour(Vector4f colour) {
        return setColour(colour.x(), colour.y(), colour.z(), colour.w());
    }

    /**
     * Displays text.
     * @param text the text
     * @param xPos the x position
     * @param yPos the y position
     * @param ratio if it is a percentage of the screen
     * @param font the Font to use
     */
    public void displayText(String text, float xPos, float yPos, boolean ratio, Font font) {
        if (ratio) {
            xPos *= window.getWidth();
            yPos *= window.getHeight();
        }

        displayInit();
        
        //Set font
        setFont(font);

        nvgText(vgContext, xPos, yPos, text);
        displayEnd();
    }

    /**
     * Displays text with a background.
     * @param text the text
     * @param xPos the x position
     * @param yPos the y position
     * @param ratio if it is a percentage of the screen
     * @param font the Font to use
     * @param padding the padding around the text
     * @param backgroundColour the colour of the background
     */
    public void displayTextWithBackground(String text, float xPos, float yPos, boolean ratio, Font font, float padding, Vector4f backgroundColour) {
        if (ratio) {
            xPos *= window.getWidth();
            yPos *= window.getHeight();
        }

        setFont(font);

        FloatBuffer textBounds = MemoryUtil.memAllocFloat(4);
        nvgTextBounds(vgContext, xPos, yPos, text, textBounds);
        MemoryUtil.memFree(textBounds);

        float backgroundX = textBounds.get(0) - padding;
        float backgroundY = textBounds.get(1) - padding;
        float backgroundWidth = textBounds.get(2) - backgroundX + padding;
        float backgroundHeight = textBounds.get(3) - backgroundY + padding;

        displayRectangle(backgroundX, backgroundY, backgroundWidth, backgroundHeight, false, backgroundColour);
        displayText(text, xPos, yPos, false, font);
    }


    /**
     * Displays a rectangle.
     * @param xPos the x position
     * @param yPos the y position
     * @param width the width
     * @param height the height
     * @param ratio if it is a percentage of the screen
     * @param colour the colour of the rectangle
     */
    public void displayRectangle(float xPos, float yPos, float width, float height, boolean ratio, Vector4f colour) {
        if (ratio) {
            xPos *= window.getWidth();
            yPos *= window.getHeight();
            width *= window.getWidth();
            height *= window.getHeight();
        }

        displayInit();
        nvgBeginPath(vgContext);
        nvgRect(vgContext, xPos, yPos, width, height);
        nvgFillColor(vgContext, setColour(colour));
        nvgFill(vgContext);
        displayEnd();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void cleanup() {
        nvgDelete(vgContext);
    }

    /**
     * A class that stores data about a font.
     */
    public static class Font {
        /** The font id. */
        private String id;

        /** The size of the font. */
        private float size;

        /** The colour of the font. */
        private Vector4f colour;

        /** 
         * Constructs a Font.
         * @param id the id
         * @param size the size
         * @param colour the colour
         */
        public Font(String id, float size, Vector4f colour) {
            this.id = id;
            this.size = size;
            this.colour = colour;
        }
    }
}

