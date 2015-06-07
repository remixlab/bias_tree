public class ClickButton extends Button2D {
  boolean addTorus;

  public ClickButton(Scene scn, PVector p, PFont font, String t, boolean addT) {
    super(scn, p, font, t);
    addTorus = addT;
  }

  @Override
  public void performInteraction(ClickEvent event) {
    if (event.clickCount() == 1) {
      if (addTorus)
        addTorus();
      else
        removeTorus();
    }
  }
}
