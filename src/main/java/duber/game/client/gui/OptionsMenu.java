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
         // Also we can do it in one line
         //context.updateGlfwWindow();

         //Vector2i windowSize = context.getFramebufferSize();

        // Set viewport size
        //glViewport(0, 0, windowSize.x, windowSize.y);
        renderer = getWindow().getDefaultInitializer().getRenderer();
        renderer.render(frame, context);
        /*
         // render frame

         // poll events to callbacks

         // Now we need to process events. Firstly we need to process system events.
         
         // When system events are translated to GUI events we need to process them.
         // This event processor calls listeners added to ui components
         
         // When everything done we need to relayout components.
         
         systemEventProcessor.processEvents(frame, context);
         // Run animations. Should be also called cause some components use animations for updating state.
         */
        getWindow().getDefaultInitializer().getSystemEventProcessor().processEvents(frame, getWindow().getDefaultInitializer().getContext());
        EventProcessorProvider.getInstance().processEvents();
        LayoutManager.getInstance().layout(frame);
        AnimatorProvider.getAnimator().runAnimations();
    }

    private static void createGuiElements(Frame frame) {
        // Set background color for frame
        frame.getContainer().getStyle().getBackground().setColor(ColorConstants.lightBlue());
        frame.getContainer().setFocusable(false);

        Button button = new Button("Add components", 20, 20, 160, 30);
        SimpleLineBorder border = new SimpleLineBorder(ColorConstants.black(), 1);
        button.getStyle().setBorder(border);

        boolean[] added = {false};
        button.getListenerMap().addListener(MouseClickEvent.class, event -> {
            if (!added[0]) {
                added[0] = true;
                for (Component c : generateOnFly()) {
                    frame.getContainer().add(c);
                }
            }
        });

        button.getListenerMap().addListener(CursorEnterEvent.class, System.out::println);

        frame.getContainer().add(button);
    }

    private static List<Component> generateOnFly() {
        List<Component> list = new ArrayList<>();

        Label label = new Label(20, 60, 200, 20);
        label.getTextState().setText("Generated on fly label");
        label.getStyle().setTextColor(ColorConstants.red());

        RadioButtonGroup group = new RadioButtonGroup();
        RadioButton radioButtonFirst = new RadioButton("First", 20, 90, 200, 20);
        RadioButton radioButtonSecond = new RadioButton("Second", 20, 110, 200, 20);

        radioButtonFirst.setRadioButtonGroup(group);
        radioButtonSecond.setRadioButtonGroup(group);

        list.add(label);
        list.add(radioButtonFirst);
        list.add(radioButtonSecond);

        return list;
    }


    
}

