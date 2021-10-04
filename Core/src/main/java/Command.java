import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class Command implements Serializable {
public abstract CommandType getType();

}
