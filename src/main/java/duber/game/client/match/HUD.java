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

import static org.lwjgl.nanovg.NanoVG.*;
import static org.lwjgl.nanovg.NanoVGGL3.*;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import org.joml.Vector4f;

public class HUD implements Cleansable {   
    public static final Vector4f WHITE = new Vector4f(255, 255, 255, 255);
    public static final Vector4f RED = new Vector4f(255, 0, 0, 255);
    public static final Vector4f TRANSLUCENT_BLACK = new Vector4f(0, 0, 0, 100);

    public static final String MAIN_FONT_ID = "OpenSans";
    
    public static final Font TITLE_FONT = new Font(MAIN_FONT_ID, 50.0f, WHITE);
    public static final Font LABEL_FONT = new Font(MAIN_FONT_ID, 30.0f, WHITE);

    public static final int CENTER_ALIGN = NVG_ALIGN_CENTER | NVG_ALIGN_MIDDLE;
    
    private Window window;

    private long displayCounter = 0;
    
    Map<String, ByteBuffer> fonts = new HashMap<>();

    private long vgContext;
    private NVGColor colour;

    public HUD(Window window) throws LWJGLException, IOException {
        colour = NVGColor.create();
        this.window = window;

        init();
    }
    
    private void init() throws LWJGLException, IOException {
        vgContext = window.optionIsTurnedOn(Window.Options.ANTI_ALIASING) ? 
            nvgCreate(NVG_ANTIALIAS | NVG_STENCIL_STROKES) :
            nvgCreate(NVG_STENCIL_STROKES);
            
        if (vgContext == NULL) {
            throw new LWJGLException("Could not create a HUD with NanoVG");
        }

        createFont(MAIN_FONT_ID, "/fonts/OpenSans-Regular.ttf", 150*1024);
    }

    private void createFont(String fontName, String fontFile, int bufferSize) throws IOException, LWJGLException {
        ByteBuffer fontBuffer = Utils.ioResourceToByteBuffer(fontFile, bufferSize);
        fonts.put(fontName, fontBuffer);
        int font = nvgCreateFontMem(vgContext, fontName, fontBuffer, 0);
        if (font == -1) {
            throw new LWJGLException("Unable to create font");
        }
    }

    public void displayMatchHud(Match match) {
        displayInit();
        User currUser = match.getGame().getUser();
        Crosshair crosshair = currUser.getCrosshair();
        displayCrosshair(crosshair, window.getWidth()/2, window.getHeight()/2);

        displayGun(match.getMainPlayer().getWeaponsInventory().getEquippedGun());
        displayPlayerData(match.getMainPlayer().getPlayerData());

        displayEnd();
    }

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

    private void displayPlayerData(PlayerData playerData) {
        String healthText = "HP: " + playerData.getHealth();
        String moneyText = "$" + playerData.getMoney();
        displayTextWithBackground(healthText, 0.1f, 0.9f, true, LABEL_FONT, 10f, TRANSLUCENT_BLACK);
        displayTextWithBackground(moneyText, 0.1f, 0.95f, true, LABEL_FONT, 10f, TRANSLUCENT_BLACK);
    }

    private void displayInit() {
        if (displayCounter++ == 0) {
            nvgBeginFrame(vgContext, window.getWidth(), window.getHeight(), 1);
        } 
    }

    private void displayEnd() {
        if (--displayCounter == 0) {
            nvgEndFrame(vgContext);
            window.restoreState();
        }
    }

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

    private void setFont(Font font) {
        nvgFontSize(vgContext, font.size);
        nvgFontFace(vgContext, font.id);
        nvgFillColor(vgContext, setColour(font.colour));
        nvgTextAlign(vgContext, CENTER_ALIGN);
    }

    private NVGColor setColour(float r, float g, float b, float a) {
        colour.r(r / 255.0f);
        colour.g(g / 255.0f);
        colour.b(b / 255.0f);
        colour.a(a / 255.0f);
        return colour;
    }

    private NVGColor setColour(Vector4f colour) {
        return setColour(colour.x(), colour.y(), colour.z(), colour.w());
    }

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

    @Override
    public void cleanup() {
        nvgDelete(vgContext);
    }

    public static class Font {
        private String id;
        private float size;
        private Vector4f colour;

        public Font(String id, float size, Vector4f colour) {
            this.id = id;
            this.size = size;
            this.colour = colour;
        }
    }
}

