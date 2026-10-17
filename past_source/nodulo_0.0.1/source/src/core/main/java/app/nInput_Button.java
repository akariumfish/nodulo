package app;

public class nInput_Button {
  public boolean state = false;
public boolean trigClick = false;
public boolean trigUClick = false;
  //boolean trigJClick = false, trigJUClick = false;
  char key_char;
  String ref;
  nInput_Button(String r, char c) { 
    ref = new String(r); 
    key_char = c;
  }
  nInput_Button(String r) { 
    ref = new String(r); 
  }
  void eventPress() {
    state=true;
    trigClick=true;
//    trigUClick = false;
  }
  void eventRelease() {
    state=false;
//    trigClick = false; 
    trigUClick=true;
  }
  void frame() {
    trigClick = false; 
    trigUClick = false;
  }
}
