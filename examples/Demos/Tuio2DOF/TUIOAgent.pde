public class TUIOAgent extends MouseAgent {
  Scene scene;

  public TUIOAgent(Scene scn, String n) {
    super(scn, n);
    this.enableTracking();
    scene = scn;
    setGestureBinding(Target.EYE, DOF2Action.ROTATE);
    setGestureBinding(Target.FRAME, DOF2Action.TRANSLATE);
  }

  public void addTuioCursor(TuioCursor tcur) {
    currentEvent = new DOF2Event(prevEvent, tcur.getScreenX(scene.width()), tcur.getScreenY(scene.height()), DOF2Event.NO_MODIFIER_MASK, DOF2Event.NO_ID);
    updateTrackedGrabber(currentEvent);
    prevEvent = currentEvent.get();
  }

  // called when a cursor is moved
  public void updateTuioCursor(TuioCursor tcur) {
    currentEvent = new DOF2Event(prevEvent, tcur.getScreenX(scene.width()), tcur.getScreenY(scene.height()), DOF2Event.NO_MODIFIER_MASK, DOF2Event.NO_ID);
    handle(currentEvent);
    prevEvent = currentEvent.get();
  }

  // called when a cursor is removed from the scene
  public void removeTuioCursor(TuioCursor tcur) {
    currentEvent = new DOF2Event(prevEvent, tcur.getScreenX(scene.width()), tcur.getScreenY(scene.height()), DOF2Event.NO_MODIFIER_MASK, DOF2Event.NO_ID);
    prevEvent = currentEvent.get();
    disableTracking();
    enableTracking();
  }
}