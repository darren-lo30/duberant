package duber.engine.entities.components;

import java.util.Map;
import java.util.Optional;

import duber.engine.graphics.animations.AnimationData;

/**
 * The animation component of an Entity
 * @author Darren Lo
 * @version 1.0
 */
public class Animation extends Component {
    /** The animation data. */
    private Map<String, AnimationData> animations;

    /** The currently playing animation. */
    private AnimationData currentAnimation;

    /**
     * Constructs an Animation component.
     * @param animations the AnimationData to use
     */
    public Animation(Map<String, AnimationData> animations) {
        this.animations = animations;
        Optional<AnimationData> firstAnimation = animations.values().stream().findFirst();

        currentAnimation = firstAnimation.isPresent() ? firstAnimation.get() : null;
    }

    /**
     * Gets an AnimationData from its name.
     * @param name the name of the AnimationData
     * @return the AnimationData
     */
    public AnimationData getAnimation(String name) {
        return animations.get(name);
    }

    /**
     * Gets the current AnimationData.
     * @return the current AnimationData
     */
    public AnimationData getCurrentAnimation() {
        return currentAnimation;
    }

    /**
     * Sets the current animation.
     * @param currentAnimation the current animation
     */
    public void setCurrentAnimation(AnimationData currentAnimation) {
        this.currentAnimation = currentAnimation;
    }

    /**
     * Sets the current animation from its name..
     * @param animationName the name of the new current animation
     */
    public void setCurrentAnimation(String animationName) {
        currentAnimation = getAnimation(animationName);
    }
}