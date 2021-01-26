package duber.engine.graphics.animations;
import java.util.List;

/**
 * Data about an animation.
 * @author Darren Lo
 * @version 1.0
 */
public class AnimationData {

    /** The current frame of the animation. */
    private int currentFrame;

    /** All the frames of the animation. */
    private List<AnimationFrame> frames;

    /** The name of the animation. */
    private String name;
    
    /** The duration of the animation. */
    private double duration;

    /** 
     * Constructs AnimationData.
     * @param name the name of the animation
     * @param frames the AnimationFrames
     * @param duration the duration of the animation
     */
    public AnimationData(String name, List<AnimationFrame> frames, double duration) {
        this.name = name;
        this.frames = frames;
        currentFrame = 0;
        this.duration = duration;
    }

    /**
     * Gets the current frame.
     * @return the current frame
     */
    public AnimationFrame getCurrentFrame() {
        return this.frames.get(currentFrame);
    }

    /**
     * Gets the duration of the animation.
     * @return the duration of the animation
     */
    public double getDuration() {
        return this.duration;        
    }
    
    /**
     * Gets all the AnimationFrames.
     * @return the AnimationFrames
     */
    public List<AnimationFrame> getFrames() {
        return frames;
    }

    /**
     * Gets the name of the animation.
     * @return the name of the animation
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the next AnimationFrame.
     * @return the next AnimationFrame
     */
    public AnimationFrame getNextFrame() {
        nextFrame();
        return this.frames.get(currentFrame);
    }

    /**
     * Moves on to the next AnimationFrame.
     */
    public void nextFrame() {
        int nextFrame = currentFrame + 1;
        if (nextFrame > frames.size() - 1) {
            currentFrame = 0;
        } else {
            currentFrame = nextFrame;
        }
    }

    /**
     * Resets the current animatin frame to the very start.
     */
    public void resetFrame() {
        currentFrame = 0;
    }

}