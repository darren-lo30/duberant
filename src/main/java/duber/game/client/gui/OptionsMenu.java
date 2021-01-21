package duber.game.client.gui;

import duber.engine.exceptions.LWJGLException;

import org.liquidengine.legui.animation.AnimatorProvider;
import org.liquidengine.legui.component.*;
import org.liquidengine.legui.event.CursorEnterEvent;
import org.liquidengine.legui.event.MouseClickEvent;
import org.liquidengine.legui.listener.processor.EventProcessorProvider;
import org.liquidengine.legui.style.border.SimpleLineBorder;
import org.liquidengine.legui.style.color.ColorConstants;
import org.liquidengine.legui.system.context.CallbackKeeper;
import org.liquidengine.legui.system.context.Context;
import org.liquidengine.legui.system.handler.processor.SystemEventProcessor;
import org.liquidengine.legui.system.layout.LayoutManager;
import org.liquidengine.legui.system.renderer.Renderer;


import java.util.ArrayList;
import java.util.List;

public class OptionsMenu extends GUI {
    Frame frame;
    Context context;
    CallbackKeeper keeper;
    SystemEventProcessor systemEventProcessor;
    Renderer renderer;

    @Override
    protected void init() throws LWJGLException {
        
        // Firstly we need to create frame component for window.
        frame = getWindow().getDefaultInitializer().getFrame();
        context = getWindow().getDefaultInitializer().getContext();
        // we can add elements here or on the fly
        createGuiElements(frame);
    }

    @Override
    public void update() {
        // TODO Auto-generated method stub
    }

    @Override
    public void render() {
         
        renderer = getWindow().getDefaultInitializer().getRenderer();
        renderer.render(frame, context);
        
        getWindow().getDefaultInitializer().getSystemEventProcessor().processEvents(frame, getWindow().getDefaultInitializer().getContext());
        EventProcessorProvider.getInstance().processEvents();
        LayoutManager.getInstance().layout(frame);
        AnimatorProvider.getAnimator().runAnimations();
    }

    private static void createGuiElements(Frame frame) {
        // Set background color for frame
        frame.getContainer().getStyle().getBackground().setColor(ColorConstants.lightBlue());
        frame.getContainer().setFocusable(false);

        Button button = new Button("SMG", 20, 20, 160, 30);
        SimpleLineBorder border = new SimpleLineBorder(ColorConstants.black(), 1);
        button.getStyle().setBorder(border);

        boolean[] added = {false};
        button.getListenerMap().addListener(MouseClickEvent.class, event -> {
            //Command
        });

        button.getListenerMap().addListener(CursorEnterEvent.class, System.out::println);

        frame.getContainer().add(button);
    }

    


    
}

