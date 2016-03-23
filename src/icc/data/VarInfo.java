package icc.data;

public class VarInfo
{
  public String type;
  public String value;
  
  public VarInfo(String type, String value)
  {
    this.type = type;
    this.value = value;
  }

@Override
public String toString() {
	return "[type=" + type + ", value=" + value + "]";
}
  
  
}
