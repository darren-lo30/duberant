package duber.engine.entities.components;

import java.util.Map;
import java.util.Optional;

import duber.engine.graphics.animations.AnimationData;

public class Animation extends Component {
    private Map<String, AnimationData> animations;
    private AnimationData currentAnimation;

    public Animation(Map<String, AnimationData> animations) {
        this.animations = animations;
        Optional<AnimationData> firstAnimation = animations.values().stream().findFirst();

        currentAnimation = firstAnimation.isPresent() ? firstAnimation.get() : null;
    }

    public AnimationData getAnimation(String name) {
        return animations.get(name);
    }

    public AnimationData getCurrentAnimation() {
        return currentAnimation;
    }

    public void setCurrentAnimation(AnimationData currentAnimation) {
        this.currentAnimation = currentAnimation;
    }

    public void setCurrentAnimation(String animationName) {
        currentAnimation = getAnimation(animationName);
    }
}