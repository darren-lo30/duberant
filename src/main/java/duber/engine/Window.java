package duber.engine;

import static org.lwjgl.opengl.GL11.glClearColor;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;

import org.lwjgl.opengl.GL;
import org.lwjgl.system.CallbackI;
import org.lwjgl.glfw.GLFWCursorPosCallbackI;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWFramebufferSizeCallbackI;
import org.lwjgl.glfw.GLFWKeyCallbackI;
import org.lwjgl.glfw.GLFWMouseButtonCallbackI;
import org.lwjgl.glfw.GLFWVidMode;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_FRONT_AND_BACK;
import static org.lwjgl.opengl.GL11.GL_LINE;
import static org.lwjgl.opengl.GL11.GL_STENCIL_TEST;
import static org.lwjgl.opengl.GL11.GL_FILL;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_BACK;

import static org.lwjgl.opengl.GL11.glCullFace;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glPolygonMode;
import static org.lwjgl.system.MemoryUtil.NULL;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_STENCIL_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;

import static org.lwjgl.opengl.GL11.glViewport;

import java.util.HashMap;
import java.util.Map;

import org.joml.Matrix4f;
import org.liquidengine.cbchain.IChainCallback;
import org.liquidengine.legui.DefaultInitializer;
import org.liquidengine.legui.component.Frame;
import org.liquidengine.legui.system.context.CallbackKeeper;


/**
 * A window used to display graphics.
 * @author Darren Lo
 * @version 1.0
 */
public class Window {

    /** The memory location of the window */
    private long windowHandle;
    
    /** The title displayed at the top left of the window */
    private String title;
    
    /** The width of the window */
    private int width;

    /** The height of the window */
    private int height;

    /**
     * The projection matrix used to display 3D worlds on the window
     */
    private Matrix4f projectionMatrix;

    /** The options for the Window */
    private Options options;

    /** The DefaultIntiializer used for the GUI */
    private DefaultInitializer defaultInitializer;
    
    /** 
     * Constructs a Window with a title, width and height
     * @param title the Window's title
     * @param width the Window's width
     * @param height the Window's height
     */
    public Window(String title, int width, int height) {
        this.title = title;
        this.width = width;
        this.height = height;

        projectionMatrix = new Matrix4f();
        options = new Options();

        init();
    }

    /** 
     * Intiializes the window 
     */
    private void init() {
        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit()) {
            throw new IllegalStateException("Could not start glfw");
        }

        //Set window settings before creation
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GL_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GL_TRUE);
        glfwWindowHint(GLFW_DECORATED, GL_TRUE);
        //glfwWindowHint(GLFW_MAXIMIZED, GL_TRUE);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_COMPAT_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);

        //GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        //windowHandle = glfwCreateWindow(vidmode.width(), vidmode.height(), title, NULL, NULL);
        windowHandle = glfwCreateWindow(width, height, title, NULL, NULL);
        if (windowHandle == NULL) {
            throw new IllegalStateException("Could not start the window");
        }
        
        glfwMakeContextCurrent(windowHandle);
        //Display window
        glfwShowWindow(windowHandle);
        GL.createCapabilities();
        
        restoreState();
        
        defaultInitializer = new DefaultInitializer(windowHandle, new Frame(width, height));
        defaultInitializer.getRenderer().initialize();
        defaultInitializer.getContext().updateGlfwWindow();

        configureDefaultKeyCallbacks();
        configureDefaultFrameBufferSizeCallback();
    }

    /**
     * Gets the defaultInitializer
     * @return the defaultInitializer
     */
    public DefaultInitializer getDefaultInitializer() {
        return defaultInitializer;
    }

    /**
     * Configures key callbacks.
     */
    private void configureDefaultKeyCallbacks() {
        GLFWKeyCallbackI defaultKeyCallbacks = (window, keyCode, scanCode, action, mods) -> {
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            //Full screen callback
            if (keyCode == GLFW_KEY_F11 && action == GLFW_RELEASE) {
                if (isFullScreen()) {
                    glfwSetWindowMonitor(window, NULL, 0, 0, width, height, GLFW_DONT_CARE);
                } else {
                    long monitor = glfwGetPrimaryMonitor();
                    glfwSetWindowMonitor(window, monitor, 0, 0, vidmode.width(), vidmode.height(), vidmode.refreshRate());
                }
            }
        };

        addCallback(defaultKeyCallbacks);
    }


    /**
     * Configures frame buffer callbacks.
     */
    private void configureDefaultFrameBufferSizeCallback() {
        GLFWFramebufferSizeCallbackI defaultFrameBufferSizeCallback = (window, newWidth, newHeight) -> {
            width = newWidth;
            height = newHeight;
        };

        addCallback(defaultFrameBufferSizeCallback);
    }

    /**
     * Gets the ChainCallback for a callback
     * @param <T> the type of callback
     * @param callback the callback
     * @return the ChainCallback
     */
    @SuppressWarnings("unchecked")
    private <T extends CallbackI> IChainCallback<T> getChainCallback(T callback) {
        CallbackKeeper callbackKeeper = defaultInitializer.getCallbackKeeper();

        if (callback instanceof GLFWCursorPosCallbackI) {
            return (IChainCallback<T>) callbackKeeper.getChainCursorPosCallback();
        } else if (callback instanceof GLFWMouseButtonCallbackI) {
            return (IChainCallback<T>) callbackKeeper.getChainMouseButtonCallback();
        } else if (callback instanceof GLFWFramebufferSizeCallbackI) {
            return (IChainCallback<T>) callbackKeeper.getChainFramebufferSizeCallback();
        } else if (callback instanceof GLFWKeyCallbackI) {
            return (IChainCallback<T>) callbackKeeper.getChainKeyCallback();
        } else {
            throw new IllegalArgumentException("The callback is not valid. Register it before adding it");
        }
    }

    /**
     * Adds a callback.
     * @param <T> the type of callback
     * @param callback the callback
     */
    public <T extends CallbackI> void addCallback(T callback) {
        getChainCallback(callback).add(callback);
    }

    /**
     * Removes a callback
     * @param <T> the type of callback
     * @param callback the callback
     */
    public <T extends CallbackI> void removeCallback(T callback) {
        IChainCallback<T> chainCallback = getChainCallback(callback);
        if (chainCallback.contains(callback)) {
            chainCallback.remove(callback);
        }
    }


    /**
     * Restores this Window's state
     */
    public void restoreState() {
        glClearColor(0.f, 0.f, 0.f, 0.f);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_STENCIL_TEST);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        applyOptions();
    }

    /**
     * Gets the window handle.
     * @return the window handle
     */
    public long getWindowHandle() {
        return windowHandle;
    }

    /**
     * Gets the width.
     * @return the width
     */
    public int getWidth() {
        return width;
    }

    /**
     * Gets the height.
     * @return the height
     */
    public int getHeight() {
        return height;
    }
    
    /**
     * Sets the title.
     * @param title the title
     */
    public void setTitle(String title) {
        this.title = title;
        glfwSetWindowTitle(windowHandle, title);
    }

    /**
     * Gets the title.
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Determines if this Window should close.
     * @return if this Window should close
     */
    public boolean shouldClose() {
        return glfwWindowShouldClose(windowHandle);
    }

    /**
     * Sets if this Window should close
     * @param shouldClose if this Window should close
     */
    public void setShouldClose(boolean shouldClose) {
        glfwSetWindowShouldClose(windowHandle, shouldClose);
    }

    /**
     * Gets this Window's projection matrix.
     * @return the projection matrix
     */
    public final Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    /**
     * Updates this Window's projection matrix.
     */
    public final Matrix4f updateProjectionMatrix(float fov, float zNear, float zFar) {
        float aspectRatio = (float) width / height;
        return projectionMatrix.identity().setPerspective(fov, aspectRatio, zNear, zFar);
    }

    /**
     * Sets the Window's option.
     * @param option the option to set
     * @param turnedOn if the option is turned on
     */
    public void setOption(int option, boolean turnedOn) {
        options.setOption(option, turnedOn);
    }

    /**
     * Determines if an option is turned on.
     * @param option the option to check
     * @return if the option is turned on
     */
    public boolean optionIsTurnedOn(int option) {
        return options.isTurnedOn(option);
    }


    /**
     * Applies any oustanding options.
     */
    public void applyOptions() {
        if (options.isTurnedOn(Options.CULL_FACES)) {
            glEnable(GL_CULL_FACE);
            glCullFace(GL_BACK);
        } else {
            glDisable(GL_CULL_FACE);
        }

        if (options.isTurnedOn(Options.SHOW_CURSOR)) {
            glfwSetInputMode(windowHandle, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
        } else {
            glfwSetInputMode(windowHandle, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
        }

        if (options.isTurnedOn(Options.DISPLAY_TRIANGLES)) {
            glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
        } else {
            glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
        }

        if (options.isTurnedOn(Options.ENABLE_VSYNC)) {
            glfwSwapInterval(1);
        } else {
            glfwSwapInterval(0);
        }
    }

    /**
     * Updates the window
     */
    public void update() {
        defaultInitializer.getContext().updateGlfwWindow();
        defaultInitializer.getFrame().setSize(width, height);
        glViewport(0, 0, width, height);
        glfwPollEvents();
        glfwSwapBuffers(windowHandle);
    }

        /**
     * Clears the window.
     */
    public void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
    }

    /**
     * Determines if this Window is full screen.
     * @return whether or not this Window is full screen
     */
    private boolean isFullScreen() {
        return glfwGetWindowMonitor(windowHandle) != NULL;
    }

    public static class Options {
        /** A map containing if each option is turned on */
        private Map<Integer, Boolean> optionsMap;
        

        /** Backfaced culling */
        public static final int CULL_FACES = 1;

        /** Displaying triangles only */
        public static final int DISPLAY_TRIANGLES = 2;

        /** Displaying FPS counter */
        public static final int DISPLAY_FPS = 3;

        /** Displaying cursor */
        public static final int SHOW_CURSOR = 4;

        /** Anti-aliasing for GUI */
        public static final int ANTI_ALIASING = 5;

        /** If V-SYNC is enabled */
        public static final int ENABLE_VSYNC = 6;

        /**
         * Constructs default Window options.
         */
        private Options() {
            optionsMap = new HashMap<>();
            optionsMap.put(DISPLAY_FPS, true);
            optionsMap.put(SHOW_CURSOR, false);
            optionsMap.put(ANTI_ALIASING, true);
            optionsMap.put(ENABLE_VSYNC, true);
        }

        /**
         * Determines if an option is turned on.
         * @param option the option to check
         * @return if the option is turned on
         */
        public boolean isTurnedOn(int option) {
            //Default is option turned off
            return optionsMap.keySet().contains(option) && optionsMap.get(option);
        }

        /**
         * Sets an option.
         * @param option the option to set
         * @param turnedOn if the option is turned on
         */
        public void setOption(int option, boolean turnedOn) {
            optionsMap.put(option, turnedOn);
        }
    }
}