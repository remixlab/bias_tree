
package remixlab.dandelion.core;

public interface Sensitivities {
	public float dampingFriction();

	public void setDampingFriction(float f);

	public void setRotationSensitivity(float sensitivity);

	public void setTranslationSensitivity(float sensitivity);

	public void setSpinningSensitivity(float sensitivity);

	public void setWheelSensitivity(float sensitivity);

	public float rotationSensitivity();

	public float translationSensitivity();

	public float spinningSensitivity();

	public float wheelSensitivity();
}
