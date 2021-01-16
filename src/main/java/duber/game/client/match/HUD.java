package duber.game.client.match;

import duber.engine.Cleansable;
import duber.engine.Window;
import duber.engine.exceptions.LWJGLException;
import duber.engine.utilities.Utils;

import org.lwjgl.nanovg.NVGColor;
import static org.lwjgl.nanovg.NanoVG.*;
import static org.lwjgl.nanovg.NanoVGGL3.*;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.joml.Vector4f;

public class HUD implements Cleansable {
    private static final String MAIN_FONT = "OpenSans";
    
    private Window window;
    

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
            
        if(vgContext == NULL) {
            throw new LWJGLException("Could not create a HUD with NanoVG");
        }

        createFont(MAIN_FONT, "/fonts/OpenSans-Regular.ttf", 150*1024);
    }

    private void createFont(String fontName, String fontFile, int bufferSize) throws IOException, LWJGLException {
        ByteBuffer fontBuffer = Utils.ioResourceToByteBuffer(fontFile, bufferSize);
        int font = nvgCreateFontMem(vgContext, fontName, fontBuffer, 0);
        if(font == -1) {
            throw new LWJGLException("Unable to create font");
        }
    }

    private void displayInit() {
        nvgBeginFrame(vgContext, window.getWidth(), window.getHeight(), 1);
    }

    private void displayEnd() {
        nvgEndFrame(vgContext);
        window.restoreState();
    }

    public void displayCrosshair(Crosshair crosshair, int posX, int posY) {
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

    private NVGColor setColour(float r, float g, float b, float a) {
        colour.r(r / 255.0f);
        colour.g(g / 255.0f);
        colour.b(b / 255.0f);
        colour.a(a / 255.0f);
        return colour;
    }

    private NVGColor setColour(Vector4f colour) {
        return setColour(colour.x() * 255.0f, colour.y() * 255.0f, colour.z() * 255.0f, colour.w() * 255.0f);
    }

    public void displayText(String text) {
        displayInit();
        //Set font
        
        nvgFontSize(vgContext, 25.0f);
        nvgFontFace(vgContext, MAIN_FONT);
        nvgTextAlign(vgContext, NVG_ALIGN_CENTER | NVG_ALIGN_TOP);
        nvgFillColor(vgContext, setColour(255.0f, 255.0f, 255.0f, 255.0f));
        nvgText(vgContext, window.getWidth()/2.0f, window.getHeight()/2.0f, text);

        displayEnd();
    }

    @Override
    public void cleanup() {
        nvgDelete(vgContext);
    }
}