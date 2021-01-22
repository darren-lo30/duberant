package duber.game.client.gui;

import org.liquidengine.legui.DefaultInitializer;
import org.liquidengine.legui.animation.AnimatorProvider;
import org.liquidengine.legui.component.Frame;
import org.liquidengine.legui.listener.processor.EventProcessor;
import org.liquidengine.legui.listener.processor.EventProcessorProvider;
import org.liquidengine.legui.system.context.CallbackKeeper;
import org.liquidengine.legui.system.context.Context;
import org.liquidengine.legui.system.handler.processor.SystemEventProcessor;
import org.liquidengine.legui.system.layout.LayoutManager;
import org.liquidengine.legui.system.renderer.Renderer;

import duber.engine.Window;
import duber.game.client.GameState;

public abstract class GUI extends GameState {
    private Frame frame;
    private Context context;
    private CallbackKeeper callbackKeeper;
    private EventProcessor eventProcessor;
    private SystemEventProcessor systemEventProcessor;
    private Renderer guiRenderer;

    @Override
    public void init() {
        DefaultInitializer guiManager = getGame().getWindow().getDefaultInitializer();

        frame = guiManager.getFrame();
        context = guiManager.getContext();
        callbackKeeper = guiManager.getCallbackKeeper();
        eventProcessor = guiManager.getGuiEventProcessor();
        systemEventProcessor = guiManager.getSystemEventProcessor();
        guiRenderer = guiManager.getRenderer();
    }
    
    @Override
    public void startup() {
        //Nothing to do on startup
    }

    @Override
    public void enter() {
        getGame().getWindow().setOption(Window.Options.SHOW_CURSOR, true);
        getGame().getWindow().applyOptions();

        frame.getContainer().clearChildComponents();
        createGuiElements();
    }

    @Override
    public void exit() {
        //Nothing to do on exit
    }

    @Override
    public void close() {
        //Nothing to do on close
    }
    
    @Override
    public void render() {
        guiRenderer.render(frame, context);
        systemEventProcessor.processEvents(frame, context);
        
        EventProcessorProvider.getInstance().processEvents();
        LayoutManager.getInstance().layout(frame);
        AnimatorProvider.getAnimator().runAnimations();
    }

    public abstract void createGuiElements();

    public Frame getFrame() {
        return frame;
    }

    public Context getContext() {
        return context;
    }

    public CallbackKeeper getCallbackKeeper() {
        return callbackKeeper;
    }

    public EventProcessor getEventProcessor() {
        return eventProcessor;
    }

    public SystemEventProcessor getSystemEventProcessor() {
        return systemEventProcessor;
    }

    public Renderer getGuiRenderer() {
        return guiRenderer;
    }
}