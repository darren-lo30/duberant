package duber.engine.graphics.animations;
import java.util.List;

public class AnimationData {

    private int currentFrame;

    private List<AnimationFrame> frames;

    private String name;
    
    private double duration;

    public AnimationData(String name, List<AnimationFrame> frames, double duration) {
        this.name = name;
        this.frames = frames;
        currentFrame = 0;
        this.duration = duration;
    }

    public AnimationFrame getCurrentFrame() {
        return this.frames.get(currentFrame);
    }

    public double getDuration() {
        return this.duration;        
    }
    
    public List<AnimationFrame> getFrames() {
        return frames;
    }

    public String getName() {
        return name;
    }

    public AnimationFrame getNextFrame() {
        nextFrame();
        return this.frames.get(currentFrame);
    }

    public void nextFrame() {
        int nextFrame = currentFrame + 1;
        if (nextFrame > frames.size() - 1) {
            currentFrame = 0;
        } else {
            currentFrame = nextFrame;
        }
    }

}