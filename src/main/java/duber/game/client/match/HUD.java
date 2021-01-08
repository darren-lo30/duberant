package duber.game.client.match;

import duber.engine.Window;
import duber.engine.exceptions.LWJGLException;

import org.lwjgl.nanovg.NVGColor;
import static org.lwjgl.nanovg.NanoVG.*;
import static org.lwjgl.nanovg.NanoVGGL3.*;
import static org.lwjgl.system.MemoryUtil.NULL;

import org.joml.Vector4f;

public class HUD {
    private long vgContext;
    private NVGColor colour;

    public HUD(Window window) throws LWJGLException {
        colour = NVGColor.create();
        init(window);
    }
    
    private void init(Window window) throws LWJGLException {
        vgContext = window.optionIsTurnedOn(Window.Options.ANTI_ALIASING) ? 
            nvgCreate(NVG_ANTIALIAS | NVG_STENCIL_STROKES) :
            nvgCreate(NVG_STENCIL_STROKES);
        if(vgContext == NULL) {
            throw new LWJGLException("Could not create a HUD with NanoVG");
        }
    }

    private void displayInit(Window window) {
        nvgBeginFrame(vgContext, window.getWidth(), window.getHeight(), 1);
    }

    private void displayEnd(Window window) {
        nvgEndFrame(vgContext);
        window.restoreState();
    }

    public void displayCrosshair(Window window, Crosshair crosshair, int posX, int posY) {
        displayInit(window);

        float crosshairHorizontalStart = posX - (float) crosshair.getWidth()/2;
        float crosshairVerticalStart = posY - (float)crosshair.getHeight()/2;
        float lineThickness = crosshair.getThickness();
        
        //Draw horizontal portion of crosshair
        nvgBeginPath(vgContext);
        nvgRect(vgContext, crosshairHorizontalStart, posY - lineThickness/2, crosshair.getWidth(), lineThickness);
        nvgFillColor(vgContext, setColour(crosshair.getColour()));
        nvgFill(vgContext);

        nvgBeginPath(vgContext);
        nvgRect(vgContext, posX - lineThickness/2, crosshairVerticalStart, lineThickness, crosshair.getHeight());
        nvgFillColor(vgContext, setColour(crosshair.getColour()));
        nvgFill(vgContext);
        
        displayEnd(window);
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
}