package duber.engine;

import static org.lwjgl.opengl.GL11.glClearColor;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;

import org.lwjgl.opengl.GL;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_FRONT_AND_BACK;
import static org.lwjgl.opengl.GL11.GL_LINE;
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

import java.util.HashMap;
import java.util.Map;

import org.joml.Matrix4f;


public class Window {
    private long windowHandle;
    
	private String title;
    
    private int width;

    private int height;

    private boolean resized;
    
    private boolean vSync;

    private Matrix4f projectionMatrix;

    private Options options;

    public Window(String title, int width, int height, boolean vSync){
        this.title = title;
        this.width = width;
        this.height = height;
        this.vSync = vSync;

        projectionMatrix = new Matrix4f();
        options = new Options();
    }

    public void init(){
        GLFWErrorCallback.createPrint(System.err).set();

        if(!glfwInit()){
            throw new IllegalStateException("Could not start glfw");
        }

        //Set window settings before creation
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GL_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GL_TRUE);
        glfwWindowHint(GLFW_DECORATED, GL_TRUE);
        glfwWindowHint(GLFW_MAXIMIZED, GL_TRUE);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);

        GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        windowHandle = glfwCreateWindow(vidmode.width(), vidmode.height(), title, NULL, NULL);
        if(windowHandle == NULL){
            throw new IllegalStateException("Could not start the window");
        }

        //Callback for resizing the window
        glfwSetFramebufferSizeCallback(windowHandle, (window, windowWidth, windowHeight) -> {
            Window.this.width = windowWidth;
            Window.this.height = windowHeight;
            Window.this.setResized(true);
        });

        //Call back to close window on key press
        glfwSetKeyCallback(windowHandle, (window, key, scancode, action, mods) -> {
            if(key == GLFW_KEY_F11 && action == GLFW_RELEASE){
                if(isFullScreen()){
                    glfwSetWindowMonitor(windowHandle, NULL, 0, 0, width, height, GLFW_DONT_CARE);
                } else {
                    long monitor = glfwGetPrimaryMonitor();
                    glfwSetWindowMonitor(windowHandle, monitor, 0, 0, vidmode.width(), vidmode.height(), vidmode.refreshRate());
                }
            }
        });

        glfwMakeContextCurrent(windowHandle);

        //Enable vsync
        if(isvSync()){
            glfwSwapInterval(1);
        }

        //Display window
        glfwShowWindow(windowHandle);
        GL.createCapabilities();

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_DEPTH_TEST);
        glClearColor(0.f, 0.f, 0.f, 0.f);
    }

    public int getWidth(){
        return width;
    }

    public int getHeight(){
        return height;
    }

    public long getWindowHandle(){
        return windowHandle;
    }

    public void setTitle(String title){
        this.title = title;
        glfwSetWindowTitle(windowHandle, title);
    }

    public String getTitle(){
        return title;
    }

    public boolean isKeyPressed(int keyCode){
        return glfwGetKey(windowHandle, keyCode) == GLFW_PRESS;
    }

    public boolean isvSync(){
        return vSync;
    }

    public boolean isResized(){
        return resized;
    }

    public void setResized(boolean resized){
        this.resized = resized;
    }

    public boolean shouldClose(){
        return glfwWindowShouldClose(windowHandle);
    }

    public final Matrix4f getProjectionMatrix(){
        return projectionMatrix;
    }

    public final Matrix4f updateProjectionMatrix(float fov, float zNear, float zFar){
        float aspectRatio = (float) width / height;
        return projectionMatrix.identity().setPerspective(fov, aspectRatio, zNear, zFar);
    }

    public Options getOptions(){
        return options;
    }


    public void applyOptions(){
        if(options.isTurnedOn(Options.CULL_FACES)){
            glEnable(GL_CULL_FACE);
            glCullFace(GL_BACK);
        } else {
            glDisable(GL_CULL_FACE);
        }

        if(options.isTurnedOn(Options.SHOW_CURSOR)){
            glfwSetInputMode(windowHandle, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
        } else {
            glfwSetInputMode(windowHandle, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
        }

        if(options.isTurnedOn(Options.DISPLAY_TRIANGLES)){
            glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
        } else {
            glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
        }
    }

    public void update(){
        glfwPollEvents();
        glfwSwapBuffers(windowHandle);
    }

    public void setClearColour(float r, float g, float b, float alpha){
        glClearColor(r, g, b, alpha);
    }

    private boolean isFullScreen(){
        return glfwGetWindowMonitor(windowHandle) != NULL;
    }

    public static class Options {
        private Map<Integer, Boolean> optionsMap;

        public static final int CULL_FACES = 1;
        public static final int DISPLAY_TRIANGLES = 2;
        public static final int DISPLAY_FPS = 3;
        public static final int SHOW_CURSOR = 4;

        private Options(){
            optionsMap = new HashMap<>();
            optionsMap.put(DISPLAY_FPS, true);
            optionsMap.put(SHOW_CURSOR, false);
        }

        public boolean isTurnedOn(int option){
            //Default is option turned off
            if(!optionsMap.keySet().contains(option)){
                return false;
            }
            return optionsMap.get(option);
        }

        public void setOption(int option, boolean turnedOn){
            optionsMap.put(option, turnedOn);
        }
    }
}