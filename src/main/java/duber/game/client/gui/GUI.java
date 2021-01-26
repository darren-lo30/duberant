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

/**
 * A class that holds necessary data to draw the GUI
 * @author Darren Lo
 * @version 1.0
 */
public abstract class GUI extends GameState {
    private Frame frame;
    private Context context;
    private CallbackKeeper callbackKeeper;
    private EventProcessor eventProcessor;
    private SystemEventProcessor systemEventProcessor;
    private Renderer guiRenderer;

    /**
     * Initializes the GUI variables
     */
    @Override
    public void init() {
        DefaultInitializer guiManager = getWindow().getDefaultInitializer();

        frame = guiManager.getFrame();
        context = guiManager.getContext();
        callbackKeeper = guiManager.getCallbackKeeper();
        eventProcessor = guiManager.getGuiEventProcessor();
        systemEventProcessor = guiManager.getSystemEventProcessor();
        guiRenderer = guiManager.getRenderer();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void startup() {
        //Nothing to do on startup
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enter() {
        getWindow().setOption(Window.Options.SHOW_CURSOR, true);
        getWindow().applyOptions();
        frame.getContainer().clearChildComponents();
        createGuiElements();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void exit() {
        //Nothing to do on exit
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        //Nothing to do on close
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void render() {
        guiRenderer.render(frame, context);
        systemEventProcessor.processEvents(frame, context);
        EventProcessorProvider.getInstance().processEvents();
        LayoutManager.getInstance().layout(frame);
        AnimatorProvider.getAnimator().runAnimations();
    }

    /**
     * Creates the elements used to draw the GUI.
     */
    public abstract void createGuiElements();

    /**
     * Gets the Frame used to store GUI components.
     * @return the frame
     */
    public Frame getFrame() {
        return frame;
    }

    /**
     * Gets the Context used to draw the GUI.
     * @return the context
     */
    public Context getContext() {
        return context;
    }

    /**
     * Gets the CallbackKeeper used to manage callbacks.
     * @return the callbackkeeper
     */
    public CallbackKeeper getCallbackKeeper() {
        return callbackKeeper;
    }

    /**
     * Gets the EventProcessor that processes GUI events.
     * @return the event processor
     */
    public EventProcessor getEventProcessor() {
        return eventProcessor;
    }

    /**
     * Gets the SystemEvenetProcessor that processes system events
     * @return the system event processor
     */
    public SystemEventProcessor getSystemEventProcessor() {
        return systemEventProcessor;
    }

    /**
     * Gets the Renderer used to render GUI.
     * @return the GUI renderer
     */
    public Renderer getGuiRenderer() {
        return guiRenderer;
    }
}