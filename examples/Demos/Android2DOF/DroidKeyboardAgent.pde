public class DroidKeyboardAgent extends KeyboardAgent {
  public DroidKeyboardAgent(Scene scn, String n) {
    super(scn, n);
  }

  @Override
  public void setDefaultShortcuts() {
    super.setDefaultShortcuts();
    keyboardProfile().setShortcut('1', KeyboardAction.ADD_KEYFRAME_TO_PATH_1);
    keyboardProfile().setShortcut('2', KeyboardAction.DELETE_PATH_1);
    keyboardProfile().setShortcut('3', KeyboardAction.PLAY_PATH_1);
  }

  public void keyEvent(processing.event.KeyEvent e) {
    if (e.getAction() == processing.event.KeyEvent.PRESS)
      handle(new KeyboardEvent(e.getKey()));
  }
}

