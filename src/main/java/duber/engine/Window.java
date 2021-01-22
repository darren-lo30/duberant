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


public class Window {
    private long windowHandle;
    
	private String title;
    private int width;
    private int height;

    private Matrix4f projectionMatrix;
    private Options options;

    //Used for LEGUI
    private DefaultInitializer defaultInitializer;
    
    public Window(String title, int width, int height) {
        this.title = title;
        this.width = width;
        this.height = height;

        projectionMatrix = new Matrix4f();
        options = new Options();

        init();
    }

    private void init() {
        GLFWErrorCallback.createPrint(System.err).set();

        if(!glfwInit()) {
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
        if(windowHandle == NULL) {
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

    public DefaultInitializer getDefaultInitializer() {
        return defaultInitializer;
    }

    private void configureDefaultKeyCallbacks() {
        GLFWKeyCallbackI defaultKeyCallbacks = (window, keyCode, scanCode, action, mods) -> {
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            //Full screen callback
            if(keyCode == GLFW_KEY_F11 && action == GLFW_RELEASE) {
                if(isFullScreen()) {
                    glfwSetWindowMonitor(window, NULL, 0, 0, width, height, GLFW_DONT_CARE);
                } else {
                    long monitor = glfwGetPrimaryMonitor();
                    glfwSetWindowMonitor(window, monitor, 0, 0, vidmode.width(), vidmode.height(), vidmode.refreshRate());
                }
            }
        };

        addCallback(defaultKeyCallbacks);
    }


    private void configureDefaultFrameBufferSizeCallback() {
        GLFWFramebufferSizeCallbackI defaultFrameBufferSizeCallback = (window, newWidth, newHeight) -> {
            width = newWidth;
            height = newHeight;
        };

        addCallback(defaultFrameBufferSizeCallback);
    }

    @SuppressWarnings("unchecked")
    private <T extends CallbackI> IChainCallback<T> getChainCallback(T callback) {
        CallbackKeeper callbackKeeper = defaultInitializer.getCallbackKeeper();

        if(callback instanceof GLFWCursorPosCallbackI) {
            return (IChainCallback<T>) callbackKeeper.getChainCursorPosCallback();
        } else if(callback instanceof GLFWMouseButtonCallbackI) {
            return (IChainCallback<T>) callbackKeeper.getChainMouseButtonCallback();
        } else if(callback instanceof GLFWFramebufferSizeCallbackI) {
            return (IChainCallback<T>) callbackKeeper.getChainFramebufferSizeCallback();
        } else if(callback instanceof GLFWKeyCallbackI) {
            return (IChainCallback<T>) callbackKeeper.getChainKeyCallback();
        } else {
            throw new IllegalArgumentException("The callback is not valid. Register it before adding it");
        }
    }

    public <T extends CallbackI> void addCallback(T callback) {
        getChainCallback(callback).add(callback);
    }

    public <T extends CallbackI> void removeCallback(T callback) {
        IChainCallback<T> chainCallback = getChainCallback(callback);
        if(chainCallback.contains(callback)) {
            chainCallback.remove(callback);
        }
    }


    public void restoreState() {
        glClearColor(0.f, 0.f, 0.f, 0.f);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_STENCIL_TEST);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        applyOptions();
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public long getWindowHandle() {
        return windowHandle;
    }

    public void setTitle(String title) {
        this.title = title;
        glfwSetWindowTitle(windowHandle, title);
    }

    public String getTitle() {
        return title;
    }

    public boolean shouldClose() {
        return glfwWindowShouldClose(windowHandle);
    }

    public void setShouldClose(boolean shouldClose) {
        glfwSetWindowShouldClose(windowHandle, shouldClose);
    }

    public final Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    public final Matrix4f updateProjectionMatrix(float fov, float zNear, float zFar) {
        float aspectRatio = (float) width / height;
        return projectionMatrix.identity().setPerspective(fov, aspectRatio, zNear, zFar);
    }

    public void setOption(int option, boolean turnedOn) {
        options.setOption(option, turnedOn);
    }

    public boolean optionIsTurnedOn(int option) {
        return options.isTurnedOn(option);
    }


    public void applyOptions() {
        if(options.isTurnedOn(Options.CULL_FACES)) {
            glEnable(GL_CULL_FACE);
            glCullFace(GL_BACK);
        } else {
            glDisable(GL_CULL_FACE);
        }

        if(options.isTurnedOn(Options.SHOW_CURSOR)) {
            glfwSetInputMode(windowHandle, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
        } else {
            glfwSetInputMode(windowHandle, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
        }

        if(options.isTurnedOn(Options.DISPLAY_TRIANGLES)) {
            glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
        } else {
            glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
        }

        if(options.isTurnedOn(Options.ENABLE_VSYNC)) {
            glfwSwapInterval(1);
        } else {
            glfwSwapInterval(0);
        }
    }

    public void update() {
        defaultInitializer.getContext().updateGlfwWindow();
        defaultInitializer.getFrame().setSize(width, height);
        glViewport(0, 0, width, height);
        glfwPollEvents();
        glfwSwapBuffers(windowHandle);
    }

    private boolean isFullScreen() {
        return glfwGetWindowMonitor(windowHandle) != NULL;
    }

    public static class Options {
        private Map<Integer, Boolean> optionsMap;

        public static final int CULL_FACES = 1;
        public static final int DISPLAY_TRIANGLES = 2;
        public static final int DISPLAY_FPS = 3;
        public static final int SHOW_CURSOR = 4;
        public static final int ANTI_ALIASING = 5;
        public static final int ENABLE_VSYNC = 6;

        private Options() {
            optionsMap = new HashMap<>();
            optionsMap.put(DISPLAY_FPS, true);
            optionsMap.put(SHOW_CURSOR, false);
            optionsMap.put(ANTI_ALIASING, true);
            optionsMap.put(ENABLE_VSYNC, true);
        }

        public boolean isTurnedOn(int option) {
            //Default is option turned off
            return optionsMap.keySet().contains(option) && optionsMap.get(option);
        }

        public void setOption(int option, boolean turnedOn) {
            optionsMap.put(option, turnedOn);
        }
    }

    public void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
    }
}